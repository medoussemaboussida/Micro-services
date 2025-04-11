// forum.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Forum {
  id?: number; // Ajout de la propriété id (optionnelle car absente lors de la création)
  title: string;
  subject: string;
  tags: 'ANXIETY' | 'DEPRESSION' | 'BIPOLAR' | 'SCHIZOPHRENIA' | 'OCD';
}

@Injectable({
  providedIn: 'root'
})
export class ForumService {
  private apiUrl = 'http://localhost:8055/forum';

  constructor(private http: HttpClient) {}

  //get forums list
  getForums(): Observable<Forum[]> {
    return this.http.get<Forum[]>(`${this.apiUrl}/forums`);
  }

  //add a topic
  createForum(forum: Forum): Observable<Forum> {
    return this.http.post<Forum>(`${this.apiUrl}`, forum);
  }

  //update a topic
  updateForum(id: number, forum: Forum): Observable<Forum> {
    return this.http.put<Forum>(`${this.apiUrl}/${id}`, forum);
  }

  //delete a topic
  deleteForum(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  //dynamic search multi criteria
  searchForums(query: string): Observable<Forum[]> {
    let params = new HttpParams();
    if (query) params = params.set('query', query);
    return this.http.get<Forum[]>(`${this.apiUrl}/search`, { params });
  }
  // Nouvelle méthode pour filtrer par tags
  filterByTags(tags: string): Observable<Forum[]> {
    let params = new HttpParams();
    if (tags) params = params.set('tags', tags);
    return this.http.get<Forum[]>(`${this.apiUrl}/filter`, { params });
  }
}