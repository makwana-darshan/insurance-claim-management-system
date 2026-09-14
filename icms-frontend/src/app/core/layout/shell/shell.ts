import { Component, computed } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { TitleCasePipe } from '@angular/common';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TitleCasePipe],
  templateUrl: './shell.html',
  styleUrl: './shell.css',
})
export class Shell {
  constructor(public authService: AuthService) {}

  user = computed(() => this.authService.getUser());

  initials = computed(() => {
    const name = this.user()?.fullName || '';
    return name
      .split(' ')
      .map((part) => part[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  });

  primaryRole = computed(() => this.user()?.roles[0] || '');

  navLinks = computed(() => {
    const role = this.primaryRole();

    switch (role) {
      case 'CUSTOMER':
        return [
          { label: 'My Claims', path: '/customer/dashboard' },
          { label: 'New Claim', path: '/customer/claims/new' },
        ];
      case 'CLAIM_OFFICER':
        return [{ label: 'Submitted Claims', path: '/claim-officer/dashboard' }];
      case 'SUPER_ADMIN':
        return [{ label: 'Overview', path: '/admin/dashboard' }];
      case 'SURVEYOR':
        return [{ label: 'Assigned Claims', path: '/surveyor/dashboard' }];
      default:
        return [];
    }
  });

  logout(): void {
    this.authService.logout();
  }
}
