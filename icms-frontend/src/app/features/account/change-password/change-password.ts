import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
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

  errorMessage = signal('');
  successMessage = signal('');
  loading = signal(false);

  constructor(private accountService: AccountService) {}

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
          this.successMessage.set(response.message);
          this.currentPassword = '';
          this.newPassword = '';
          this.confirmNewPassword = '';
        },
        error: (err) => {
          this.loading.set(false);
          this.errorMessage.set(err.error?.error || 'Failed to change password. Please try again.');
        },
      });
  }
}
