import { Component, OnDestroy, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register implements OnDestroy {
  fullName = '';
  email = '';
  password = '';
  confirmPassword = '';

  errorMessage = signal('');
  loading = signal(false);
  submitted = signal(false);

  showPassword = signal(false);
  showConfirmPassword = signal(false);
  passwordMatchMessage = signal<'match' | 'mismatch' | null>(null);
  private passwordMatchTimeout?: ReturnType<typeof setTimeout>;

  constructor(
    private authService: AuthService,
    private titleService: Title,
  ) {
    this.titleService.setTitle('ICMS - Register');
  }

  togglePassword(): void {
    this.showPassword.update((v) => !v);
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword.update((v) => !v);
  }

  onPasswordsChanged(): void {
    clearTimeout(this.passwordMatchTimeout);

    if (!this.password || !this.confirmPassword) {
      this.passwordMatchMessage.set(null);
      return;
    }

    this.passwordMatchMessage.set(
      this.password === this.confirmPassword ? 'match' : 'mismatch',
    );
    this.passwordMatchTimeout = setTimeout(() => {
      this.passwordMatchMessage.set(null);
      this.passwordMatchTimeout = undefined;
    }, 3500);
  }

  passwordsMismatch(): boolean {
    return !!this.password && !!this.confirmPassword && this.password !== this.confirmPassword;
  }

  ngOnDestroy(): void {
    clearTimeout(this.passwordMatchTimeout);
  }

  onSubmit(): void {
    this.errorMessage.set('');

    if (this.password !== this.confirmPassword) {
      this.errorMessage.set('Passwords do not match');
      return;
    }

    if (this.password.length < 6) {
      this.errorMessage.set('Password must be at least 6 characters');
      return;
    }

    this.loading.set(true);

    this.authService
      .register({
        fullName: this.fullName,
        email: this.email,
        password: this.password,
      })
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.submitted.set(true);
        },
        error: (err) => {
          this.loading.set(false);
          this.errorMessage.set(
            err.error?.error ||
              err.error?.email ||
              err.error?.fullName ||
              err.error?.password ||
              'Registration failed. Please try again.',
          );
        },
      });
  }
}
