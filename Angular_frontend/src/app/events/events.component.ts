import { Component, OnInit, ChangeDetectorRef, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { EventsService } from '../services/events.service';
import { saveAs } from 'file-saver';
import * as L from 'leaflet';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit, AfterViewInit {
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
  showAddForm: boolean = false; // Ajout de la propriété pour contrôler la visibilité du formulaire

  // Propriétés pour la carte
  private map!: L.Map;
  private marker!: L.Marker;
  @ViewChild('map') mapContainer!: ElementRef;

  constructor(private eventsService: EventsService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadEvents();
  }

  ngAfterViewInit(): void {
    this.initializeMap();
  }

  private initializeMap(): void {
    if (this.mapContainer && this.mapContainer.nativeElement) {
      this.map = L.map(this.mapContainer.nativeElement).setView([48.8566, 2.3522], 13);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
        maxZoom: 18
      }).addTo(this.map);
    }
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
          this.showAddForm = false; // Masquer le formulaire après l'ajout
          this.successMessage = 'Événement ajouté avec succès !';
          this.errorMessage = null;
          setTimeout(() => this.successMessage = null, 3000);
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de l\'ajout de l\'événement.';
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
          this.errorMessage = 'Erreur lors de la mise à jour de l\'événement.';
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
          this.errorMessage = 'Erreur lors de la suppression de l\'événement.';
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

        if (this.coordinates && this.coordinates.latitude && this.coordinates.longitude) {
          const lat = this.coordinates.latitude;
          const lng = this.coordinates.longitude;

          this.map.setView([lat, lng], 13);

          if (this.marker) {
            this.map.removeLayer(this.marker);
          }

          this.marker = L.marker([lat, lng]).addTo(this.map)
            .bindPopup(`Événement ID: ${id}<br>Latitude: ${lat}<br>Longitude: ${lng}`)
            .openPopup();
        }
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
        this.errorMessage = 'Erreur lors de l\'exportation en PDF.';
        console.error(error);
      }
    });
  }

  // Méthode pour basculer la visibilité du formulaire
  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      // Réinitialiser le formulaire lorsqu'il est masqué
      this.newEvent = { title: '', description: '', eventType: '', localisation: '', startDate: '', endDate: '', status: 'PLANNED' };
    }
  }

  // Générer l'URL du QR code pour un événement
  getQrCodeUrl(event: any): string {
    const data = `ID: ${event.id}\nTitre: ${event.title}\nDescription: ${event.description}\nType: ${event.eventType}\nLocalisation: ${event.localisation}\nDate de début: ${new Date(event.startDate).toLocaleString()}\nDate de fin: ${new Date(event.endDate).toLocaleString()}\nStatut: ${event.status}`;
    const encodedData = encodeURIComponent(data);
    return `https://api.qrserver.com/v1/create-qr-code/?size=70x70&data=${encodedData}`;
  }
}