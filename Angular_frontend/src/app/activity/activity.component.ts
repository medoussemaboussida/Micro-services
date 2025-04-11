// src/app/activity/activity.component.ts
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivityService } from '../services/activity.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable'; // Importer autoTable pour l'exportation PDF

@Component({
  selector: 'app-activity',
  templateUrl: './activity.component.html',
  styleUrls: ['./activity.component.css']
})
export class ActivityComponent implements OnInit {
  activities: any[] = [];
  newActivity: any = { title: '', description: '', category: '' };
  editActivity: any = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  searchTitle: string = '';
  categories: string[] = ['WORKSHOP', 'SUPPORT_GROUP', 'THERAPY', 'EXERCISE', 'MEDITATION']; // Liste des catégories

  constructor(private activityService: ActivityService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadActivities();
  }

  // Charger toutes les activités
  loadActivities(): void {
    this.activityService.getAllActivities().subscribe({
      next: (activities) => {
        this.activities = activities.map(activity => ({
          ...activity,
          qrCodeUrl: this.generateQRCodeUrl(activity) // Ajouter une propriété qrCodeUrl
        }));
        this.errorMessage = null;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors du chargement des activités.';
        console.error(error);
      }
    });
  }

  // Générer l'URL du QR Code pour une activité en utilisant l'API de goqr.me
  generateQRCodeUrl(activity: any): string {
    const activityInfo = `ID: ${activity.id}\nTitre: ${activity.title}\nDescription: ${activity.description}\nCatégorie: ${activity.category}\nDate de création: ${new Date(activity.createdAt).toLocaleString()}`;
    const encodedData = encodeURIComponent(activityInfo);
    return `https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=${encodedData}`;
  }

  // Rechercher une activité par titre
  searchByTitle(): void {
    if (this.searchTitle.trim()) {
      this.activityService.getActivityByTitle(this.searchTitle).subscribe({
        next: (activity) => {
          this.activities = activity ? [activity] : [];
          this.activities = this.activities.map(act => ({
            ...act,
            qrCodeUrl: this.generateQRCodeUrl(act) // Ajouter une propriété qrCodeUrl
          }));
          this.errorMessage = activity ? null : 'Aucune activité trouvée.';
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de la recherche.';
          console.error(error);
        }
      });
    } else {
      this.loadActivities();
    }
  }

  // Ajouter une activité
  addActivity(): void {
    if (this.newActivity.title && this.newActivity.description && this.newActivity.category) {
      this.activityService.createActivity(this.newActivity).subscribe({
        next: (activity) => {
          this.activities.push({ ...activity, qrCodeUrl: this.generateQRCodeUrl(activity) });
          this.newActivity = { title: '', description: '', category: '' };
          this.successMessage = 'Activité ajoutée avec succès !';
          this.errorMessage = null;
          setTimeout(() => this.successMessage = null, 3000);
          this.loadActivities();
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de l\'ajout de l\'activité.';
          console.error(error);
        }
      });
    } else {
      this.errorMessage = 'Tous les champs sont requis.';
    }
  }

  // Préparer l'édition d'une activité
  startEdit(activity: any): void {
    this.editActivity = { ...activity };
  }

  // Annuler l'édition
  cancelEdit(): void {
    this.editActivity = null;
  }

  // Mettre à jour une activité
  updateActivity(): void {
    if (this.editActivity) {
      this.activityService.updateActivity(this.editActivity.id, this.editActivity).subscribe({
        next: (updatedActivity) => {
          const index = this.activities.findIndex(a => a.id === updatedActivity.id);
          if (index !== -1) {
            this.activities[index] = { ...updatedActivity, qrCodeUrl: this.generateQRCodeUrl(updatedActivity) };
          }
          this.editActivity = null;
          this.successMessage = 'Activité mise à jour avec succès !';
          this.errorMessage = null;
          setTimeout(() => this.successMessage = null, 3000);
          this.loadActivities();
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de la mise à jour de l\'activité.';
          console.error(error);
        }
      });
    }
  }

  // Supprimer une activité
  deleteActivity(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette activité ?')) {
      this.activityService.deleteActivity(id).subscribe({
        next: () => {
          this.activities = this.activities.filter(a => a.id !== id);
          this.successMessage = 'Activité supprimée avec succès !';
          this.errorMessage = null;
          this.cdr.detectChanges();
          setTimeout(() => this.successMessage = null, 3000);
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de la suppression de l\'activité.';
          console.error(error);
        }
      });
    }
  }

  // Exporter la liste des activités en PDF
  exportToPDF(): void {
    const doc = new jsPDF();
    autoTable(doc, {
      head: [['ID', 'Titre', 'Description', 'Catégorie', 'Date de création']],
      body: this.activities.map(activity => [
        activity.id,
        activity.title,
        activity.description,
        activity.category,
        new Date(activity.createdAt).toLocaleString()
      ]),
      startY: 20,
      theme: 'striped',
      headStyles: { fillColor: [0, 123, 255] },
      styles: { fontSize: 10 }
    });

    // Ajouter un titre au PDF
    doc.text('Liste des Activités', 14, 10);

    // Télécharger le PDF
    doc.save('activities.pdf');
  }
}