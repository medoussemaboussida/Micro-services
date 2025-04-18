import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { PublicationService } from '../services/publication.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-publication',
  templateUrl: './publication.component.html',
  styleUrls: ['./publication.component.css']
})
export class PublicationComponent implements OnInit {
  publications: any[] = [];
  allPublications: any[] = [];
  newPublication: any = { title: '', content: '', author: '' };
  editPublication: any = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  searchTerm: string = '';
  sortOrder: string = 'recent';
  showAddForm: boolean = false; // Ajout de la propriété pour contrôler la visibilité du formulaire

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
        this.allPublications = publications;
        this.publications = [...this.allPublications];
        this.errorMessage = null;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors du chargement des publications.';
        console.error(error);
      }
    });
  }

  // Filtrer les publications localement par titre ou auteur
  filterPublications(): void {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) {
      this.publications = [...this.allPublications];
      this.errorMessage = null;
      this.cdr.detectChanges();
      return;
    }

    this.publications = this.allPublications.filter(pub => {
      const titleMatch = pub.title?.toLowerCase().includes(term);
      const authorMatch = pub.author?.toLowerCase().includes(term);
      return titleMatch || authorMatch;
    });

    this.errorMessage = this.publications.length ? null : 'Aucune publication trouvée.';
    this.cdr.detectChanges();
  }

  // Ajouter une publication
  addPublication(): void {
    if (this.newPublication.title && this.newPublication.content && this.newPublication.author) {
      this.publicationService.createPublication(this.newPublication).subscribe({
        next: (publication) => {
          this.allPublications.push(publication);
          this.publications = [...this.allPublications];
          this.filterPublications();
          this.newPublication = { title: '', content: '', author: '' };
          this.showAddForm = false; // Masquer le formulaire après l'ajout
          this.successMessage = 'Publication ajoutée avec succès !';
          this.errorMessage = null;
          setTimeout(() => this.successMessage = null, 3000);
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
          const index = this.allPublications.findIndex(p => p.id === updatedPublication.id);
          if (index !== -1) {
            this.allPublications[index] = updatedPublication;
            this.publications = [...this.allPublications];
            this.filterPublications();
          }
          this.editPublication = null;
          this.successMessage = 'Publication mise à jour avec succès !';
          this.errorMessage = null;
          setTimeout(() => this.successMessage = null, 3000);
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
          this.allPublications = this.allPublications.filter(p => p.id !== id);
          this.publications = [...this.allPublications];
          this.filterPublications();
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

  // Générer l'URL du QR code pour une publication
  getQrCodeUrl(publication: any): string {
    const data = `ID: ${publication.id}\nTitre: ${publication.title}\nContenu: ${publication.content}\nAuteur: ${publication.author}\nDate de création: ${new Date(publication.createdAt).toLocaleString()}`;
    const encodedData = encodeURIComponent(data);
    return `https://api.qrserver.com/v1/create-qr-code/?size=70x70&data=${encodedData}`;
  }

  // Exporter la liste des publications en PDF
  exportToPDF(): void {
    const doc = new jsPDF();
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

    doc.text('Liste des Publications', 14, 10);
    doc.save('publications.pdf');
  }

  // Méthode pour basculer la visibilité du formulaire
  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      // Réinitialiser le formulaire lorsqu'il est masqué
      this.newPublication = { title: '', content: '', author: '' };
    }
  }
}