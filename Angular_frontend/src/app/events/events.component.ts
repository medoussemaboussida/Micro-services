import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { EventsService } from '../services/events.service';
import { saveAs } from 'file-saver';
import * as XLSX from 'xlsx'; // Importer la bibliothèque xlsx

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {
  events: any[] = [];
  allEvents: any[] = [];
  newEvent: any = { title: '', description: '', eventType: '', localisation: '', startDate: '', endDate: '', status: 'PLANNED' };
  editEvent: any = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  searchTerm: string = '';
  weatherData: any = null;
  coordinates: any = null;
  statuses: string[] = ['PLANNED', 'ONGOING', 'COMPLETED', 'CANCELLED'];
  showAddForm: boolean = false;

  constructor(private eventsService: EventsService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.eventsService.getAllEvents().subscribe({
      next: (events) => {
        this.allEvents = events;
        this.events = [...this.allEvents];
        this.errorMessage = null;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors du chargement des événements.';
        console.error(error);
      }
    });
  }

  addEvent(): void {
    if (this.newEvent.title && this.newEvent.eventType && this.newEvent.localisation && this.newEvent.startDate && this.newEvent.endDate) {
      this.eventsService.createEvent(this.newEvent).subscribe({
        next: (event) => {
          this.allEvents.push(event);
          this.events = [...this.allEvents];
          this.filterEvents();
          this.newEvent = { title: '', description: '', eventType: '', localisation: '', startDate: '', endDate: '', status: 'PLANNED' };
          this.showAddForm = false;
          this.successMessage = 'Événement ajouté avec succès !';
          this.errorMessage = null;
          setTimeout(() => this.successMessage = null, 3000);
        },
        error: (error) => {
          this.errorMessage = "Erreur lors de l'ajout de l'événement.";
          console.error(error);
        }
      });
    } else {
      this.errorMessage = 'Tous les champs obligatoires sont requis.';
    }
  }

  startEdit(event: any): void {
    this.editEvent = {
      ...event,
      startDate: new Date(event.startDate).toISOString().slice(0, 16),
      endDate: new Date(event.endDate).toISOString().slice(0, 16)
    };
  }

  cancelEdit(): void {
    this.editEvent = null;
  }

  updateEvent(): void {
    if (this.editEvent) {
      this.eventsService.updateEvent(this.editEvent.id, this.editEvent).subscribe({
        next: (updatedEvent) => {
          const index = this.allEvents.findIndex(e => e.id === updatedEvent.id);
          if (index !== -1) {
            this.allEvents[index] = updatedEvent;
            this.events = [...this.allEvents];
            this.filterEvents();
          }
          this.editEvent = null;
          this.successMessage = 'Événement mis à jour avec succès !';
          this.errorMessage = null;
          setTimeout(() => this.successMessage = null, 3000);
        },
        error: (error) => {
          this.errorMessage = "Erreur lors de la mise à jour de l'événement.";
          console.error(error);
        }
      });
    }
  }

  deleteEvent(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet événement ?')) {
      this.eventsService.deleteEvent(id).subscribe({
        next: () => {
          this.allEvents = this.allEvents.filter(e => e.id !== id);
          this.events = [...this.allEvents];
          this.filterEvents();
          this.successMessage = 'Événement supprimé avec succès !';
          this.errorMessage = null;
          setTimeout(() => this.successMessage = null, 3000);
        },
        error: (error) => {
          this.errorMessage = "Erreur lors de la suppression de l'événement.";
          console.error(error);
        }
      });
    }
  }

  filterEvents(): void {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) {
      this.events = [...this.allEvents];
      this.errorMessage = null;
      this.cdr.detectChanges();
      return;
    }

    this.events = this.allEvents.filter(event => {
      const titleMatch = event.title?.toLowerCase().includes(term);
      const typeMatch = event.eventType?.toLowerCase().includes(term);
      const localisationMatch = event.localisation?.toLowerCase().includes(term);
      const statusMatch = event.status?.toLowerCase().includes(term);
      const keywordMatch = event.title?.toLowerCase().includes(term);
      const localisationAndTypeMatch = 
        event.localisation?.toLowerCase().includes(term) || 
        event.eventType?.toLowerCase().includes(term);

      return titleMatch || typeMatch || localisationMatch || statusMatch || keywordMatch || localisationAndTypeMatch;
    });

    this.errorMessage = this.events.length ? null : 'Aucun événement trouvé.';
    this.cdr.detectChanges();
  }

  getWeather(id: number): void {
    this.eventsService.getWeatherForEvent(id).subscribe({
      next: (weather) => {
        this.weatherData = weather;
        this.successMessage = 'Données météo récupérées avec succès !';
        this.errorMessage = null;
        setTimeout(() => this.successMessage = null, 3000);
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors de la récupération des données météo.';
        console.error(error);
      }
    });
  }

  getCoordinates(id: number): void {
    this.eventsService.getEventCoordinates(id).subscribe({
      next: (coords) => {
        this.coordinates = coords;
        this.successMessage = 'Coordonnées récupérées avec succès !';
        this.errorMessage = null;
        setTimeout(() => this.successMessage = null, 3000);
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors de la récupération des coordonnées.';
        console.error(error);
      }
    });
  }

  exportToPDF(id: number): void {
    this.eventsService.exportEventToPdf(id).subscribe({
      next: (blob) => {
        saveAs(blob, `event_${id}.pdf`);
        this.successMessage = 'PDF exporté avec succès !';
        setTimeout(() => this.successMessage = null, 3000);
      },
      error: (error) => {
        this.errorMessage = "Erreur lors de l'exportation en PDF.";
        console.error(error);
      }
    });
  }

  exportToExcel(): void {
    // Préparer les données pour l'exportation
    const exportData = this.events.map(event => ({
      ID: event.id,
      Titre: event.title,
      Description: event.description,
      Type: event.eventType,
      Localisation: event.localisation,
      'Date de début': new Date(event.startDate).toLocaleString(),
      'Date de fin': new Date(event.endDate).toLocaleString(),
      Statut: event.status
    }));

    // Créer une feuille de calcul
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(exportData);

    // Définir des largeurs de colonnes personnalisées
    worksheet['!cols'] = [
      { wch: 10 }, // ID
      { wch: 20 }, // Titre
      { wch: 40 }, // Description
      { wch: 15 }, // Type
      { wch: 20 }, // Localisation
      { wch: 25 }, // Date de début
      { wch: 25 }, // Date de fin
      { wch: 15 }  // Statut
    ];

    // Créer un classeur et ajouter la feuille
    const workbook: XLSX.WorkBook = { Sheets: { 'Événements': worksheet }, SheetNames: ['Événements'] };

    // Générer le fichier Excel
    const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });

    // Créer un Blob et sauvegarder le fichier
    const data: Blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8' });
    saveAs(data, 'events.xlsx');

    this.successMessage = 'Liste exportée en Excel avec succès !';
    setTimeout(() => this.successMessage = null, 3000);
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.newEvent = { title: '', description: '', eventType: '', localisation: '', startDate: '', endDate: '', status: 'PLANNED' };
    }
  }

  getQrCodeUrl(event: any): string {
    const data = `ID: ${event.id}\nTitre: ${event.title}\nDescription: ${event.description}\nType: ${event.eventType}\nLocalisation: ${event.localisation}\nDate de début: ${new Date(event.startDate).toLocaleString()}\nDate de fin: ${new Date(event.endDate).toLocaleString()}\nStatut: ${event.status}`;
    const encodedData = encodeURIComponent(data);
    return `https://api.qrserver.com/v1/create-qr-code/?size=70x70&data=${encodedData}`;
  }
}