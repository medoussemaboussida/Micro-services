import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ReclamationService {
  private apiUrl = 'http://localhost:8500/reclamation'; // URL de ton API backend

  constructor(private http: HttpClient) { }

  // Ajouter une réclamation
  addReclamation(reclamation: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/add`, reclamation)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Récupérer toutes les réclamations
  getAllReclamations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/getall`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Mettre à jour une réclamation
  updateReclamation(id: string, reclamation: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/update/${id}`, reclamation)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Supprimer une réclamation
  deleteReclamation(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deleteReclamatiion/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Gestion des erreurs
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Une erreur est survenue';
    if (error.error instanceof ErrorEvent) {
      // Erreur côté client
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      // Erreur côté serveur
      errorMessage = `Code d'erreur: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}