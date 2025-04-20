import { Component, OnInit, OnDestroy } from '@angular/core';
import { ForumService, Forum } from '../services/forum.service';
import { Router } from '@angular/router';
import { interval, Subscription, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-forum-list',
  templateUrl: './forum-list.component.html',
  styleUrls: ['./forum-list.component.css']
})
export class ForumListComponent implements OnInit, OnDestroy {
  forums: Forum[] = [];
  private refreshSubscription!: Subscription;
  searchQuery: string = '';
  selectedTag: string = '';
  mentalIssues = ['ANXIETY', 'DEPRESSION', 'BIPOLAR', 'SCHIZOPHRENIA', 'OCD', ''];
  private searchSubject = new Subject<string>();
  // Propriétés pour le tri
  sortColumn: string = ''; // Colonne actuellement triée ('title', 'subject', 'tags')
  sortDirection: string = ''; // Direction du tri ('asc', 'desc', ou '' pour aucun tri)

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
      this.filterByTags();
    } else if (this.searchQuery.trim()) {
      this.searchForums(this.searchQuery);
    } else {
      this.forumService.getForums().subscribe({
        next: (data) => {
          this.forums = data;
          this.applySort(); // Appliquer le tri après le chargement des données
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
      this.loadForums();
    });
  }

  onSearchChange(query: string): void {
    this.searchSubject.next(query);
  }

  onTagChange(): void {
    this.loadForums();
  }

  filterByTags(): void {
    this.forumService.filterByTags(this.selectedTag).subscribe({
      next: (data) => {
        if (this.searchQuery.trim()) {
          this.forums = data.filter(forum =>
            forum.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
            forum.subject.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
            forum.tags.toLowerCase().includes(this.searchQuery.toLowerCase())
          );
        } else {
          this.forums = data;
        }
        this.applySort(); // Appliquer le tri après le filtrage
      },
      error: (error) => {
        console.error('Erreur lors du filtrage des forums par tags', error);
      }
    });
  }

  searchForums(query: string): void {
    this.forumService.searchForums(query).subscribe({
      next: (data) => {
        if (this.selectedTag) {
          this.forums = data.filter(forum => forum.tags === this.selectedTag);
        } else {
          this.forums = data;
        }
        this.applySort(); // Appliquer le tri après la recherche
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

  exportToPDF(): void {
    const doc = new jsPDF();
    autoTable(doc, {
      head: [['Titre', 'Sujet', 'Tags']],
      body: this.forums.map(forum => [
        forum.title,
        forum.subject,
        forum.tags
      ]),
      startY: 20,
      theme: 'striped',
      headStyles: { fillColor: [0, 123, 255] },
      styles: { fontSize: 10 }
    });

    doc.text('Liste des Forums', 14, 10);
    doc.save('forums.pdf');
  }

  exportToExcel(): void {
    const exportData = this.forums.map(forum => ({
      Titre: forum.title,
      Sujet: forum.subject,
      Tags: forum.tags,
    }));

    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(exportData);
    const workbook: XLSX.WorkBook = { Sheets: { 'Forums': worksheet }, SheetNames: ['Forums'] };
    const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const data: Blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    saveAs(data, 'forums.xlsx');
  }

  // Méthode pour gérer le tri
  sortData(column: string): void {
    if (this.sortColumn === column) {
      // Si la colonne est déjà triée, changer la direction
      if (this.sortDirection === 'asc') {
        this.sortDirection = 'desc';
      } else if (this.sortDirection === 'desc') {
        this.sortDirection = ''; // Réinitialiser le tri
      } else {
        this.sortDirection = 'asc';
      }
    } else {
      // Nouvelle colonne sélectionnée, trier par ordre ascendant par défaut
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }

    this.applySort();
  }

  // Appliquer le tri sur la liste des forums
  private applySort(): void {
    if (!this.sortColumn || !this.sortDirection) {
      return; // Pas de tri à appliquer
    }

    this.forums.sort((a, b) => {
      let valueA = a[this.sortColumn as keyof Forum]?.toString().toLowerCase() || '';
      let valueB = b[this.sortColumn as keyof Forum]?.toString().toLowerCase() || '';

      if (this.sortDirection === 'asc') {
        return valueA.localeCompare(valueB);
      } else {
        return valueB.localeCompare(valueA);
      }
    });
  }
}