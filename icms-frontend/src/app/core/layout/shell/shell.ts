import { Component, computed, effect, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { TitleCasePipe } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TitleCasePipe],
  templateUrl: './shell.html',
  styleUrl: './shell.css',
})
export class Shell {
  menuOpen = signal(false);

  constructor(
    public authService: AuthService,
    private titleService: Title,
  ) {
    effect(() => {
      const role = this.primaryRole();
      const roleName = role ? role.charAt(0) + role.slice(1).toLowerCase().replace('_', ' ') : '';
      this.titleService.setTitle(roleName ? `ICMS - ${roleName}` : 'ICMS');
    });
  }

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

  toggleMenu(): void {
    this.menuOpen.update((v) => !v);
  }

  closeMenu(): void {
    this.menuOpen.set(false);
  }

  logout(): void {
    this.authService.logout();
  }
}
