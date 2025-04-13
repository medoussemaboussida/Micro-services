// src/app/services/appointment.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private apiUrl = 'http://localhost:8057/appointment'; // URL de ton API Spring Boot pour les rendez-vous

  constructor(private http: HttpClient) { }

  // Récupérer tous les rendez-vous
  getAllAppointments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/appointments`);
  }

  // Récupérer un rendez-vous par ID
  getAppointmentById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  // Récupérer les rendez-vous par psychiatre
  getAppointmentsByPsychiatrist(psychiatrist: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/by-psychiatrist/${psychiatrist}`);
  }

  // Récupérer les rendez-vous triés par date
  getAppointmentsByDateOrder(order: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/filter-by-date?order=${order}`);
  }

  // Ajouter un rendez-vous
  createAppointment(appointment: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, appointment, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Mettre à jour un rendez-vous
  updateAppointment(id: number, appointment: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, appointment, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Mettre à jour le statut d'un rendez-vous
  updateAppointmentStatus(id: number, status: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/status`, { status }, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Supprimer un rendez-vous
  deleteAppointment(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }

  // Exporter les rendez-vous en PDF
  exportAppointmentsToPdf(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/export-pdf`, { responseType: 'blob' });
  }
  // Exporter les rendez-vous en CSV
  exportAppointmentsToCsv(): Observable<string> {
    return this.http.get(`${this.apiUrl}/export-csv`, { responseType: 'text' });
  }
}