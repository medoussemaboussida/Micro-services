import { Component, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  currentRoute: string = '';
  isMenuOpen: boolean = false;
  isScrolled: boolean = false; // Pour détecter le défilement

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.currentRoute = this.router.url.split('/')[1] || '';
    this.router.events.subscribe(() => {
      this.currentRoute = this.router.url.split('/')[1] || '';
      this.isMenuOpen = false;
    });
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    // Détecter si l'utilisateur a défilé vers le bas
    this.isScrolled = window.pageYOffset > 50;
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  // Navigation vers la page de profil
  goToProfile(): void {
    this.router.navigate(['profile']);
    this.isMenuOpen = false;
  }

  // Navigation vers la page des paramètres
  goToSettings(): void {
    this.router.navigate(['settings']);
    this.isMenuOpen = false;
  }
}