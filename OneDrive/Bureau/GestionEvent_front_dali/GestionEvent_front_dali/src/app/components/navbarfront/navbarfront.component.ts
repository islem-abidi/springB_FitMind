import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbarfront',
  templateUrl: './navbarfront.component.html',
  styleUrls: ['./navbarfront.component.css']
})
export class NavbarfrontComponent {
  constructor(private router: Router) {}

  goToEvenements(event: Event) {
    event.preventDefault();

    // Si on est déjà sur /evenements
    if (this.router.url === '/evenements') {
      const section = document.getElementById('evenement-section');
      if (section) {
        section.scrollIntoView({ behavior: 'smooth' });
      }
    } else {
      // Sinon on navigue vers /evenements et attend le rendu pour scroller
      this.router.navigate(['/evenements']).then(() => {
        setTimeout(() => {
          const section = document.getElementById('evenement-section');
          if (section) {
            section.scrollIntoView({ behavior: 'smooth' });
          }
        }, 300); // délai pour attendre que la page se charge
      });
    }
  }
}
