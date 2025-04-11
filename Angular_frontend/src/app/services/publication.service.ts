// src/app/services/publication.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PublicationService {
  private apiUrl = 'http://localhost:8056/publication'; // URL de ton API Spring Boot pour les publications

  constructor(private http: HttpClient) { }

  // Récupérer toutes les publications
  getAllPublications(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/publications`);
  }

  // Récupérer une publication par ID
  getPublicationById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  // Récupérer des publications par titre
  getPublicationsByTitle(title: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/by-title/${title}`);
  }

  // Récupérer des publications par auteur
  getPublicationsByAuthor(author: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/by-author/${author}`);
  }

  // Récupérer toutes les publications triées par date (plus récent d'abord)
  getPublicationsRecentFirst(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/publications/recent-first`);
  }

  // Récupérer toutes les publications triées par date (plus ancien d'abord)
  getPublicationsOldestFirst(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/publications/oldest-first`);
  }

  // Ajouter une publication
  createPublication(publication: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, publication, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Mettre à jour une publication
  updatePublication(id: number, publication: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, publication, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Supprimer une publication
  deletePublication(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}