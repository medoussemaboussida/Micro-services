// add-forum.component.ts
import { Component, OnInit } from '@angular/core';
import { ForumService, Forum } from '../services/forum.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-add-forum',
  templateUrl: './add-forum.component.html',
  styleUrls: ['./add-forum.component.css']
})
export class AddForumComponent implements OnInit {
  forum: Forum = { title: '', subject: '', tags: 'ANXIETY' };
  mentalIssues = ['ANXIETY', 'DEPRESSION', 'BIPOLAR', 'SCHIZOPHRENIA', 'OCD'];
  isEditMode = false; // Indique si on est en mode édition
  forumId: number | null = null; // ID du forum à modifier

  constructor(
    private forumService: ForumService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Vérifie si un ID est passé dans les paramètres de la route
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.forumId = +id; // Convertit l'ID en nombre
        this.loadForum(this.forumId);
      }
    });
  }

  loadForum(id: number): void {
    // Récupère les données du forum à modifier
    this.forumService.getForums().subscribe({
      next: (forums) => {
        const forum = forums.find(f => f.id === id);
        if (forum) {
          this.forum = { ...forum }; // Pré-remplit le formulaire
        }
      },
      error: (error) => {
        console.error('Erreur lors du chargement du forum', error);
      }
    });
  }

  saveForum(): void {
    if (this.isEditMode && this.forumId !== null) {
      // Mode édition : met à jour le forum existant
      this.forumService.updateForum(this.forumId, this.forum).subscribe({
        next: () => {
          console.log('Forum modifié avec succès');
          this.router.navigate(['/']);
        },
        error: (error) => {
          console.error('Erreur lors de la modification du forum', error);
        }
      });
    } else {
      // Mode ajout : crée un nouveau forum
      this.forumService.createForum(this.forum).subscribe({
        next: (response) => {
          console.log('Forum ajouté avec succès', response);
          this.router.navigate(['/']);
        },
        error: (error) => {
          console.error('Erreur lors de l\'ajout du forum', error);
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/']);
  }
}