import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  email = '';
  loading = signal(false);
  submitted = signal(false);
  errorMessage = signal('');

  constructor(
    private authService: AuthService,
    private titleService: Title,
  ) {
    this.titleService.setTitle('ICMS - Forgot Password');
  }

  onSubmit(): void {
    this.errorMessage.set('');
    this.loading.set(true);

    this.authService.forgotPassword({ email: this.email }).subscribe({
      next: () => {
        this.loading.set(false);
        this.submitted.set(true);
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Something went wrong. Please try again.');
      },
    });
  }
}
