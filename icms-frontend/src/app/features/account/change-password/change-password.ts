import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AccountService } from '../../../core/services/account.service';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './change-password.html',
  styleUrl: './change-password.css',
})
export class ChangePassword {
  currentPassword = '';
  newPassword = '';
  confirmNewPassword = '';

  showCurrentPassword = signal(false);
  showNewPassword = signal(false);
  showConfirmPassword = signal(false);

  errorMessage = signal('');
  successMessage = signal('');
  loading = signal(false);

  constructor(
    private accountService: AccountService,
    private router: Router,
  ) {}

  toggleCurrentPassword(): void {
    this.showCurrentPassword.update((v) => !v);
  }

  toggleNewPassword(): void {
    this.showNewPassword.update((v) => !v);
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword.update((v) => !v);
  }

  onSubmit(): void {
    this.errorMessage.set('');
    this.successMessage.set('');

    if (this.newPassword !== this.confirmNewPassword) {
      this.errorMessage.set('New passwords do not match');
      return;
    }

    if (this.newPassword.length < 6) {
      this.errorMessage.set('New password must be at least 6 characters');
      return;
    }

    this.loading.set(true);

    this.accountService
      .changePassword({
        currentPassword: this.currentPassword,
        newPassword: this.newPassword,
      })
      .subscribe({
        next: (response) => {
          this.loading.set(false);
          this.successMessage.set(response.message + ' Redirecting...');

          setTimeout(() => {
            this.router.navigate(['/dashboard']);
          }, 1200);
        },
        error: (err) => {
          this.loading.set(false);
          this.errorMessage.set(err.error?.error || 'Failed to change password. Please try again.');
        },
      });
  }
}
