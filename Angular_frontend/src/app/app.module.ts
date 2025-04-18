import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http'; // Ajoute ceci
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ForumListComponent } from './forum-list/forum-list.component';
import { AddForumComponent } from './add-forum/add-forum.component';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from './navbar/navbar.component';
import { ReclamationComponent } from './reclamation/reclamation.component';
import { ActivityComponent } from './activity/activity.component';
import { PublicationComponent } from './publication/publication.component';
import { AppointmentComponent } from './appointment/appointment.component';
import { EventsComponent } from './events/events.component';
const routes: Routes = [
  { path: '', component: ForumListComponent },
  { path: 'add-forum', component: AddForumComponent },
  { path: 'add-forum/:id', component: AddForumComponent }, // Route pour l'édition
  { path: 'reclamations', component: ReclamationComponent }, // Nouvelle route pour les réclamations
  { path: 'activities', component: ActivityComponent }, // Nouvelle route pour les activités
  { path: 'publications', component: PublicationComponent }, // Nouvelle route pour les publications
  { path: 'appointments', component: AppointmentComponent }, // Nouvelle route pour les rendez-vous
  { path: 'events', component: EventsComponent }, // Nouvelle route pour Events
  { path: '**', redirectTo: ''}
];
@NgModule({
  declarations: [
    AppComponent,
    ForumListComponent,
    AddForumComponent,
    AddForumComponent,
    NavbarComponent,
    ReclamationComponent,
    ActivityComponent,
    PublicationComponent,
    AppointmentComponent,
    EventsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule, // Assurez-vous que ceci est présent
    RouterModule.forRoot(routes)  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
