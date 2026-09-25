import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './verify-email.html',
  styleUrl: './verify-email.css',
})
export class VerifyEmail implements OnInit {
  status = signal<'verifying' | 'success' | 'error'>('verifying');
  errorMessage = signal('');

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private titleService: Title,
  ) {
    this.titleService.setTitle('ICMS - Verify Email');
  }

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.status.set('error');
      this.errorMessage.set('No verification token provided.');
      return;
    }

    this.authService.verifyEmail({ token }).subscribe({
      next: () => {
        this.status.set('success');
      },
      error: (err) => {
        this.status.set('error');
        this.errorMessage.set(
          err.error?.error || 'Verification failed. The link may be invalid or expired.',
        );
      },
    });
  }
}
