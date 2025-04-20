import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivityService } from '../services/activity.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import * as FileSaver from 'file-saver';
import emailjs from '@emailjs/browser'; // Importer EmailJS

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
  categories: string[] = ['WORKSHOP', 'SUPPORT_GROUP', 'THERAPY', 'EXERCISE', 'MEDITATION'];
  showAddForm: boolean = false;
  sortColumn: string = '';
  sortDirection: 'asc' | 'desc' = 'asc';

  constructor(private activityService: ActivityService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadActivities();
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.newActivity = { title: '', description: '', category: '' };
    }
  }

  loadActivities(): void {
    this.activityService.getAllActivities().subscribe({
      next: (activities) => {
        this.activities = activities.map(activity => ({
          ...activity,
          qrCodeUrl: this.generateQRCodeUrl(activity)
        }));
        this.errorMessage = null;
        this.sortData();
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors du chargement des activités.';
        console.error(error);
      }
    });
  }

  generateQRCodeUrl(activity: any): string {
    const activityInfo = `ID: ${activity.id}\nTitre: ${activity.title}\nDescription: ${activity.description}\nCatégorie: ${activity.category}\nDate de création: ${new Date(activity.createdAt).toLocaleString()}`;
    const encodedData = encodeURIComponent(activityInfo);
    return `https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=${encodedData}`;
  }

  searchByTitle(): void {
    if (this.searchTitle.trim()) {
      this.activityService.getActivityByTitle(this.searchTitle).subscribe({
        next: (activity) => {
          this.activities = activity ? [activity] : [];
          this.activities = this.activities.map(act => ({
            ...act,
            qrCodeUrl: this.generateQRCodeUrl(act)
          }));
          this.errorMessage = activity ? null : 'Aucune activité trouvée.';
          this.sortData();
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

  addActivity(): void {
    if (this.newActivity.title && this.newActivity.description && this.newActivity.category) {
      this.activityService.createActivity(this.newActivity).subscribe({
        next: (activity) => {
          this.activities.push({ ...activity, qrCodeUrl: this.generateQRCodeUrl(activity) });

          // Envoyer un email avec EmailJS
          this.sendEmail(activity);

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

  // Méthode pour envoyer un email avec EmailJS
  sendEmail(activity: any): void {
    const templateParams = {
      title: activity.title,
      description: activity.description,
      category: activity.category
    };

    emailjs.send('service_1cls9im', 'template_xp3y9lf', templateParams)
      .then((response) => {
        console.log('Email envoyé avec succès !', response.status, response.text);
      }, (error) => {
        console.error('Erreur lors de l\'envoi de l\'email :', error);
        this.errorMessage = 'Activité ajoutée, mais erreur lors de l\'envoi de l\'email.';
      });
  }

  startEdit(activity: any): void {
    this.editActivity = { ...activity };
  }

  cancelEdit(): void {
    this.editActivity = null;
  }

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

    doc.text('Liste des Activités', 14, 10);
    doc.save('activities.pdf');
  }

  exportToExcel(): void {
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(
      this.activities.map(activity => ({
        ID: activity.id,
        Titre: activity.title,
        Description: activity.description,
        Catégorie: activity.category,
        'Date de création': new Date(activity.createdAt).toLocaleString()
      }))
    );

    worksheet['!cols'] = [
      { wch: 10 },
      { wch: 20 },
      { wch: 40 },
      { wch: 15 },
      { wch: 25 }
    ];

    const workbook: XLSX.WorkBook = { Sheets: { 'Activités': worksheet }, SheetNames: ['Activités'] };
    const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const data: Blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8' });
    FileSaver.saveAs(data, 'activities.xlsx');
  }

  printList(): void {
    window.print();
  }

  sortData(column: string = this.sortColumn): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }

    this.activities.sort((a, b) => {
      let valueA = a[column];
      let valueB = b[column];

      if (column === 'createdAt') {
        valueA = new Date(valueA).getTime();
        valueB = new Date(valueB).getTime();
      }

      if (typeof valueA === 'string') {
        valueA = valueA.toLowerCase();
        valueB = valueB.toLowerCase();
      }

      if (valueA < valueB) {
        return this.sortDirection === 'asc' ? -1 : 1;
      }
      if (valueA > valueB) {
        return this.sortDirection === 'asc' ? 1 : -1;
      }
      return 0;
    });

    this.cdr.detectChanges();
  }
}