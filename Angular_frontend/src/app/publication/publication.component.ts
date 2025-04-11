// src/app/publication/publication.component.ts
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { PublicationService } from '../services/publication.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable'; // Importer autoTable correctement

@Component({
  selector: 'app-publication',
  templateUrl: './publication.component.html',
  styleUrls: ['./publication.component.css']
})
export class PublicationComponent implements OnInit {
  publications: any[] = [];
  newPublication: any = { title: '', content: '', author: '' };
  editPublication: any = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  searchTitle: string = '';
  searchAuthor: string = '';
  sortOrder: string = 'recent'; // Par défaut, tri par date récente

  constructor(private publicationService: PublicationService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadPublications();
  }

  // Charger toutes les publications
  loadPublications(): void {
    const observable = this.sortOrder === 'recent'
      ? this.publicationService.getPublicationsRecentFirst()
      : this.publicationService.getPublicationsOldestFirst();

    observable.subscribe({
      next: (publications) => {
        this.publications = publications;
        this.errorMessage = null;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors du chargement des publications.';
        console.error(error);
      }
    });
  }

  // Rechercher une publication par titre
  searchByTitle(): void {
    if (this.searchTitle.trim()) {
      this.publicationService.getPublicationsByTitle(this.searchTitle).subscribe({
        next: (publications) => {
          this.publications = publications;
          this.errorMessage = publications.length ? null : 'Aucune publication trouvée.';
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de la recherche par titre.';
          console.error(error);
        }
      });
    } else {
      this.loadPublications();
    }
  }

  // Rechercher une publication par auteur
  searchByAuthor(): void {
    if (this.searchAuthor.trim()) {
      this.publicationService.getPublicationsByAuthor(this.searchAuthor).subscribe({
        next: (publications) => {
          this.publications = publications;
          this.errorMessage = publications.length ? null : 'Aucune publication trouvée.';
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de la recherche par auteur.';
          console.error(error);
        }
      });
    } else {
      this.loadPublications();
    }
  }

  // Ajouter une publication
  addPublication(): void {
    if (this.newPublication.title && this.newPublication.content && this.newPublication.author) {
      this.publicationService.createPublication(this.newPublication).subscribe({
        next: (publication) => {
          this.publications.push(publication);
          this.newPublication = { title: '', content: '', author: '' };
          this.successMessage = 'Publication ajoutée avec succès !';
          this.errorMessage = null;
          setTimeout(() => this.successMessage = null, 3000);
          this.loadPublications();
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de l\'ajout de la publication.';
          console.error(error);
        }
      });
    } else {
      this.errorMessage = 'Tous les champs sont requis.';
    }
  }

  // Préparer l'édition d'une publication
  startEdit(publication: any): void {
    this.editPublication = { ...publication };
  }

  // Annuler l'édition
  cancelEdit(): void {
    this.editPublication = null;
  }

  // Mettre à jour une publication
  updatePublication(): void {
    if (this.editPublication) {
      this.publicationService.updatePublication(this.editPublication.id, this.editPublication).subscribe({
        next: (updatedPublication) => {
          const index = this.publications.findIndex(p => p.id === updatedPublication.id);
          if (index !== -1) {
            this.publications[index] = updatedPublication;
          }
          this.editPublication = null;
          this.successMessage = 'Publication mise à jour avec succès !';
          this.errorMessage = null;
          setTimeout(() => this.successMessage = null, 3000);
          this.loadPublications();
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de la mise à jour de la publication.';
          console.error(error);
        }
      });
    }
  }

  // Supprimer une publication
  deletePublication(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette publication ?')) {
      this.publicationService.deletePublication(id).subscribe({
        next: () => {
          this.publications = this.publications.filter(p => p.id !== id);
          this.successMessage = 'Publication supprimée avec succès !';
          this.errorMessage = null;
          this.cdr.detectChanges();
          setTimeout(() => this.successMessage = null, 3000);
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de la suppression de la publication.';
          console.error(error);
        }
      });
    }
  }

  // Changer l'ordre de tri
  changeSortOrder(order: string): void {
    this.sortOrder = order;
    this.loadPublications();
  }

  // Exporter la liste des publications en PDF
  exportToPDF(): void {
    const doc = new jsPDF();
    // Appliquer autoTable à jsPDF
    autoTable(doc, {
      head: [['ID', 'Titre', 'Contenu', 'Auteur', 'Date de création']],
      body: this.publications.map(pub => [
        pub.id,
        pub.title,
        pub.content,
        pub.author,
        new Date(pub.createdAt).toLocaleString()
      ]),
      startY: 20,
      theme: 'striped',
      headStyles: { fillColor: [0, 123, 255] },
      styles: { fontSize: 10 }
    });

    // Ajouter un titre au PDF
    doc.text('Liste des Publications', 14, 10);

    // Télécharger le PDF
    doc.save('publications.pdf');
  }
}