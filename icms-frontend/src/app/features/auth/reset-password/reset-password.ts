import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword implements OnInit {
  token = '';
  newPassword = '';
  confirmPassword = '';

  loading = signal(false);
  success = signal(false);
  errorMessage = signal('');
  tokenMissing = signal(false);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private titleService: Title,
  ) {
    this.titleService.setTitle('ICMS - Reset Password');
  }

  ngOnInit(): void {
    const tokenParam = this.route.snapshot.queryParamMap.get('token');
    if (!tokenParam) {
      this.tokenMissing.set(true);
    } else {
      this.token = tokenParam;
    }
  }

  onSubmit(): void {
    this.errorMessage.set('');

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage.set('Passwords do not match');
      return;
    }

    if (this.newPassword.length < 6) {
      this.errorMessage.set('Password must be at least 6 characters');
      return;
    }

    this.loading.set(true);

    this.authService.resetPassword({ token: this.token, newPassword: this.newPassword }).subscribe({
      next: () => {
        this.loading.set(false);
        this.success.set(true);
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(
          err.error?.error || 'Failed to reset password. The link may be invalid or expired.',
        );
      },
    });
  }
}
