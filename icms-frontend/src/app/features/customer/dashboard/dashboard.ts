import { Component, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ClaimService } from '../../../core/services/claim.service';
import { ClaimResponse } from '../../../core/models/claim.model';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [DatePipe, DecimalPipe, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  claims = signal<ClaimResponse[]>([]);
  loading = signal(true);
  errorMessage = signal('');
  submittingId = signal<number | null>(null);

  constructor(private claimService: ClaimService) {}

  ngOnInit(): void {
    this.loadClaims();
  }

  loadClaims(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.claimService.getMyClaims().subscribe({
      next: (claims) => {
        this.claims.set(claims);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load claims. Please try again.');
        this.loading.set(false);
      },
    });
  }

  submitClaim(claim: ClaimResponse): void {
    this.submittingId.set(claim.id);

    this.claimService.submitClaim(claim.id).subscribe({
      next: (updated) => {
        this.claims.update((list) => list.map((c) => (c.id === updated.id ? updated : c)));
        this.submittingId.set(null);
      },
      error: () => {
        this.errorMessage.set('Failed to submit claim. Please try again.');
        this.submittingId.set(null);
      },
    });
  }
}
