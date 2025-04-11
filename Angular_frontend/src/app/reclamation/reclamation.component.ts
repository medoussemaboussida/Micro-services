import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ReclamationService } from '../services/reclamation.service';

@Component({
  selector: 'app-reclamation',
  templateUrl: './reclamation.component.html',
  styleUrls: ['./reclamation.component.css']
})
export class ReclamationComponent implements OnInit {
  reclamations: any[] = [];
  newReclamation: any = { subject: '', description: '' };
  selectedReclamation: any | null = null;
  errorMessage: string | null = null;

  constructor(
    private reclamationService: ReclamationService,
    private cdr: ChangeDetectorRef // Injecter ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadReclamations();
  }

  // Charger toutes les réclamations
  loadReclamations(): void {
    this.reclamationService.getAllReclamations().subscribe({
      next: (reclamations) => {
        console.log('Réclamations récupérées:', reclamations);
        this.reclamations = reclamations;
        this.errorMessage = null;
        this.cdr.detectChanges(); // Forcer la détection des changements
      },
      error: (error) => {
        console.error('Erreur lors du chargement des réclamations:', error);
        this.errorMessage = 'Erreur lors du chargement des réclamations. Vérifiez votre connexion au serveur.';
      }
    });
  }

  // Ajouter une réclamation
  addReclamation(): void {
    if (this.newReclamation.subject && this.newReclamation.description) {
      console.log('Données envoyées au backend:', this.newReclamation);
      this.reclamationService.addReclamation(this.newReclamation).subscribe({
        next: (reclamation) => {
          this.reclamations.push(reclamation);
          this.newReclamation = { subject: '', description: '' };
          this.loadReclamations();
          this.errorMessage = null;
        },
        error: (error) => {
          console.error('Erreur lors de l\'ajout de la réclamation:', error);
          this.errorMessage = 'Erreur lors de l\'ajout de la réclamation.';
        }
      });
    } else {
      this.errorMessage = 'Les champs sujet et description sont requis.';
    }
  }

  // Sélectionner une réclamation pour modification
  selectReclamation(reclamation: any): void {
    this.selectedReclamation = { ...reclamation };
  }

  // Mettre à jour une réclamation
  updateReclamation(): void {
    if (this.selectedReclamation && this.selectedReclamation._id) {
      this.reclamationService.updateReclamation(this.selectedReclamation._id, this.selectedReclamation).subscribe({
        next: (updatedReclamation) => {
          const index = this.reclamations.findIndex(r => r._id === updatedReclamation._id);
          if (index !== -1) {
            this.reclamations[index] = updatedReclamation;
          }
          this.selectedReclamation = null;
          this.loadReclamations();
          this.errorMessage = null;
        },
        error: (error) => {
          console.error('Erreur lors de la mise à jour de la réclamation:', error);
          this.errorMessage = 'Erreur lors de la mise à jour de la réclamation.';
        }
      });
    }
  }

  // Supprimer une réclamation
  deleteReclamation(id: string): void {
    this.reclamationService.deleteReclamation(id).subscribe({
      next: () => {
        this.reclamations = this.reclamations.filter(r => r._id !== id);
        this.loadReclamations();
        this.errorMessage = null;
      },
      error: (error) => {
        console.error('Erreur lors de la suppression de la réclamation:', error);
        this.errorMessage = 'Erreur lors de la suppression de la réclamation.';
      }
    });
  }

  // Annuler la modification
  cancelUpdate(): void {
    this.selectedReclamation = null;
  }
}