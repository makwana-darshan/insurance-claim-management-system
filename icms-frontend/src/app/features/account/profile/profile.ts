import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AccountService } from '../../../core/services/account.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  fullName = '';
  email = '';
  role = '';

  loading = signal(true);
  saving = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  constructor(
    private accountService: AccountService,
    private authService: AuthService,
    private router: Router,
    private toastService: ToastService,
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading.set(true);

    this.accountService.getProfile().subscribe({
      next: (user) => {
        this.fullName = user.fullName;
        this.email = user.email;
        this.role = user.roles[0] || '';
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  onSubmit(): void {
    this.errorMessage.set('');
    this.successMessage.set('');

    if (!this.fullName.trim()) {
      this.errorMessage.set('Full name cannot be empty.');
      return;
    }

    this.saving.set(true);

    this.accountService.updateProfile({ fullName: this.fullName }).subscribe({
      next: (updated) => {
        this.saving.set(false);
        this.successMessage.set('Profile updated successfully.');
        this.toastService.success('Profile updated successfully.');

        const user = this.authService.getUser();
        if (user) {
          localStorage.setItem(
            'icms_user',
            JSON.stringify({
              ...user,
              fullName: updated.fullName,
            }),
          );
        }

        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 1000);
      },
      error: () => {
        this.saving.set(false);
      },
    });
  }
}
