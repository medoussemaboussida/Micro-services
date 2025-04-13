// src/app/appointment/appointment.component.ts
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { AppointmentService } from '../services/appointment.service';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-appointment',
  templateUrl: './appointment.component.html',
  styleUrls: ['./appointment.component.css']
})
export class AppointmentComponent implements OnInit {
  appointments: any[] = [];
  newAppointment: any = { student: '', psychiatrist: '', date: '', startTime: '', endTime: '', status: 'PENDING' };
  editAppointment: any = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  searchPsychiatrist: string = '';
  sortOrder: string = 'recent';
  statuses: string[] = ['PENDING', 'CONFIRMED', 'CANCELED', 'COMPLETED'];

  constructor(private appointmentService: AppointmentService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments(): void {
    const observable = this.sortOrder === 'recent'
      ? this.appointmentService.getAppointmentsByDateOrder('recent')
      : this.appointmentService.getAppointmentsByDateOrder('old');

    observable.subscribe({
      next: (appointments) => {
        this.appointments = appointments;
        this.errorMessage = null;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors du chargement des rendez-vous.';
        console.error(error);
      }
    });
  }

  searchByPsychiatrist(): void {
    if (this.searchPsychiatrist.trim()) {
      this.appointmentService.getAppointmentsByPsychiatrist(this.searchPsychiatrist).subscribe({
        next: (appointments) => {
          this.appointments = appointments;
          this.errorMessage = appointments.length ? null : 'Aucun rendez-vous trouvé.';
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de la recherche par psychiatre.';
          console.error(error);
        }
      });
    } else {
      this.loadAppointments();
    }
  }

  addAppointment(): void {
    if (this.newAppointment.student && this.newAppointment.psychiatrist && this.newAppointment.date && this.newAppointment.startTime && this.newAppointment.endTime) {
      this.appointmentService.createAppointment(this.newAppointment).subscribe({
        next: (appointment) => {
          this.appointments.push(appointment);
          this.newAppointment = { student: '', psychiatrist: '', date: '', startTime: '', endTime: '', status: 'PENDING' };
          this.successMessage = 'Rendez-vous ajouté avec succès !';
          this.errorMessage = null;
          setTimeout(() => this.successMessage = null, 3000);
          this.loadAppointments();
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de l\'ajout du rendez-vous.';
          console.error(error);
        }
      });
    } else {
      this.errorMessage = 'Tous les champs sont requis.';
    }
  }

  startEdit(appointment: any): void {
    this.editAppointment = {
      ...appointment,
      date: new Date(appointment.date).toISOString().split('T')[0] // Format YYYY-MM-DD pour input type="date"
    };
  }

  cancelEdit(): void {
    this.editAppointment = null;
  }

  updateAppointment(): void {
    if (this.editAppointment) {
      this.appointmentService.updateAppointment(this.editAppointment.id, this.editAppointment).subscribe({
        next: (updatedAppointment) => {
          const index = this.appointments.findIndex(a => a.id === updatedAppointment.id);
          if (index !== -1) {
            this.appointments[index] = updatedAppointment;
          }
          this.editAppointment = null;
          this.successMessage = 'Rendez-vous mis à jour avec succès !';
          this.errorMessage = null;
          setTimeout(() => this.successMessage = null, 3000);
          this.loadAppointments();
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de la mise à jour du rendez-vous.';
          console.error(error);
        }
      });
    }
  }

  updateStatus(appointment: any, newStatus: string): void {
    this.appointmentService.updateAppointmentStatus(appointment.id, newStatus).subscribe({
      next: (updatedAppointment) => {
        const index = this.appointments.findIndex(a => a.id === updatedAppointment.id);
        if (index !== -1) {
          this.appointments[index] = updatedAppointment;
        }
        this.successMessage = 'Statut mis à jour avec succès !';
        this.errorMessage = null;
        setTimeout(() => this.successMessage = null, 3000);
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors de la mise à jour du statut.';
        console.error(error);
      }
    });
  }

  deleteAppointment(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce rendez-vous ?')) {
      this.appointmentService.deleteAppointment(id).subscribe({
        next: () => {
          this.appointments = this.appointments.filter(a => a.id !== id);
          this.successMessage = 'Rendez-vous supprimé avec succès !';
          this.errorMessage = null;
          this.cdr.detectChanges();
          setTimeout(() => this.successMessage = null, 3000);
        },
        error: (error) => {
          this.errorMessage = 'Erreur lors de la suppression du rendez-vous.';
          console.error(error);
        }
      });
    }
  }

  changeSortOrder(order: string): void {
    this.sortOrder = order;
    this.loadAppointments();
  }

  exportToPDF(): void {
    this.appointmentService.exportAppointmentsToPdf().subscribe({
      next: (blob) => {
        saveAs(blob, 'appointments.pdf');
        this.successMessage = 'PDF exporté avec succès !';
        setTimeout(() => this.successMessage = null, 3000);
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors de l\'exportation en PDF.';
        console.error(error);
      }
    });
  }
  exportToCSV(): void {
    this.appointmentService.exportAppointmentsToCsv().subscribe({
      next: (csvContent) => {
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8' });
        saveAs(blob, 'appointments.csv');
        this.successMessage = 'CSV exporté avec succès !';
        setTimeout(() => this.successMessage = null, 3000);
      },
      error: (error) => {
        this.errorMessage = 'Erreur lors de l\'exportation en CSV.';
        console.error(error);
      }
    });
  }
}