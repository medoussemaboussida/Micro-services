import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EventsService {
  private apiUrl = 'http://localhost:8060/Events'; // URL de ton microservice Events

  constructor(private http: HttpClient) { }

  // Ajouter un événement
  createEvent(event: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, event, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Mettre à jour un événement
  updateEvent(id: number, event: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, event, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Supprimer un événement
  deleteEvent(id: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${id}`, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Récupérer tous les événements
  getAllEvents(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  // Récupérer un événement par ID
  getEventById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  // Rechercher des événements par titre
  getEventsByTitle(title: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/by-title/${title}`);
  }

  // Rechercher des événements par type
  getByEventType(eventType: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/by-event-type/${eventType}`);
  }

  // Rechercher des événements par localisation
  getByLocalisation(localisation: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/by-localisation/${localisation}`);
  }

  // Rechercher des événements par statut
  getByStatus(status: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/by-status/${status}`);
  }

  // Rechercher des événements par date de début
  getByStartDate(startDate: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/by-start-date?startDate=${startDate}`);
  }

  // Rechercher des événements par plage de dates
  getByDateRange(startDate: string, endDate: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/by-date-range?startDate=${startDate}&endDate=${endDate}`);
  }

  // Rechercher des événements par mot-clé dans le titre
  searchByTitleContaining(keyword: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search?keyword=${keyword}`);
  }

  // Rechercher des événements par localisation et type
  getByLocalisationAndEventType(localisation: string, eventType: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/by-localisation-and-type?localisation=${localisation}&eventType=${eventType}`);
  }

  // Récupérer les données météo pour un événement
  getWeatherForEvent(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/weather`);
  }

  // Récupérer les coordonnées d'un événement
  getEventCoordinates(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/coordinates`);
  }

  // Exporter un événement en PDF
  exportEventToPdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, { responseType: 'blob' });
  }
}