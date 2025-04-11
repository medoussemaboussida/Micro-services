// src/app/services/activity.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ActivityService {
  private apiUrl = 'http://localhost:8058/api/activities'; // Mise à jour du préfixe

  constructor(private http: HttpClient) { }

  // Récupérer toutes les activités
  getAllActivities(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  // Récupérer une activité par ID
  getActivityById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  // Récupérer une activité par titre
  getActivityByTitle(title: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/title/${title}`);
  }

  // Ajouter une activité
  createActivity(activity: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, activity, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Mettre à jour une activité
  updateActivity(id: number, activity: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, activity, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Supprimer une activité
  deleteActivity(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}