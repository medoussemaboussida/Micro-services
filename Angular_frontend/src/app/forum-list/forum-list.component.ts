// forum-list.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ForumService, Forum } from '../services/forum.service';
import { Router } from '@angular/router';
import { interval, Subscription, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-forum-list',
  templateUrl: './forum-list.component.html',
  styleUrls: ['./forum-list.component.css']
})
export class ForumListComponent implements OnInit, OnDestroy {
  forums: Forum[] = [];
  private refreshSubscription!: Subscription;
  searchQuery: string = '';
  selectedTag: string = ''; // Tag sélectionné pour le filtrage
  mentalIssues = ['ANXIETY', 'DEPRESSION', 'BIPOLAR', 'SCHIZOPHRENIA', 'OCD', '']; // Liste des tags ('' pour "Tous")
  private searchSubject = new Subject<string>();

  constructor(private forumService: ForumService, private router: Router) {}

  ngOnInit(): void {
    this.loadForums();
    this.startAutoRefresh();
    this.setupSearch();
  }

  ngOnDestroy(): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }
    this.searchSubject.complete();
  }

  loadForums(): void {
    if (this.selectedTag) {
      // Si un tag est sélectionné, appliquer le filtre
      this.filterByTags();
    } else if (this.searchQuery.trim()) {
      // Si une recherche est active, appliquer la recherche
      this.searchForums(this.searchQuery);
    } else {
      // Sinon, charger tous les forums
      this.forumService.getForums().subscribe({
        next: (data) => {
          this.forums = data;
        },
        error: (error) => {
          console.error('Erreur lors de la récupération des forums', error);
        }
      });
    }
  }

  setupSearch(): void {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(query => {
      this.searchQuery = query;
      this.loadForums(); // Recharger les forums avec les filtres actuels
    });
  }

  onSearchChange(query: string): void {
    this.searchSubject.next(query);
  }

  onTagChange(): void {
    this.loadForums(); // Recharger les forums avec le nouveau tag
  }

  filterByTags(): void {
    this.forumService.filterByTags(this.selectedTag).subscribe({
      next: (data) => {
        // Appliquer la recherche sur les résultats filtrés par tag
        if (this.searchQuery.trim()) {
          this.forums = data.filter(forum =>
            forum.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
            forum.subject.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
            forum.tags.toLowerCase().includes(this.searchQuery.toLowerCase())
          );
        } else {
          this.forums = data;
        }
      },
      error: (error) => {
        console.error('Erreur lors du filtrage des forums par tags', error);
      }
    });
  }

  searchForums(query: string): void {
    this.forumService.searchForums(query).subscribe({
      next: (data) => {
        // Appliquer le filtre par tag sur les résultats de la recherche
        if (this.selectedTag) {
          this.forums = data.filter(forum => forum.tags === this.selectedTag);
        } else {
          this.forums = data;
        }
      },
      error: (error) => {
        console.error('Erreur lors de la recherche des forums', error);
      }
    });
  }

  deleteForum(id: number | undefined): void {
    if (id === undefined) {
      console.error('ID du forum non défini, suppression impossible');
      return;
    }
    if (confirm('Voulez-vous vraiment supprimer ce forum ?')) {
      this.forumService.deleteForum(id).subscribe({
        next: () => {
          this.loadForums();
          console.log('Forum supprimé avec succès');
        },
        error: (error) => {
          console.error('Erreur lors de la suppression', error);
        }
      });
    }
  }

  editForum(forum: Forum): void {
    this.router.navigate(['/add-forum', { id: forum.id }]);
  }

  addForum(): void {
    this.router.navigate(['/add-forum']);
  }

  getQrCodeUrl(forum: Forum): string {
    const qrData = `Titre: ${forum.title}\nSujet: ${forum.subject}\nTags: ${forum.tags}`;
    const encodedData = encodeURIComponent(qrData);
    return `https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=${encodedData}`;
  }

  private startAutoRefresh(): void {
    this.refreshSubscription = interval(5000).subscribe(() => {
      this.loadForums();
      console.log('Liste des forums rafraîchie automatiquement');
    });
  }
}