import { Component, OnInit } from '@angular/core';
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
  claims: ClaimResponse[] = [];
  loading = true;
  errorMessage = '';
  submittingId: number | null = null;

  constructor(private claimService: ClaimService) {}

  ngOnInit(): void {
    this.loadClaims();
  }

  loadClaims(): void {
    this.loading = true;
    this.errorMessage = '';

    this.claimService.getMyClaims().subscribe({
      next: (claims) => {
        console.log('DEBUG: claims received', claims);
        this.claims = claims;
        this.loading = false;
        console.log('DEBUG: loading set to false');
      },
      error: (err) => {
        console.log('DEBUG: error occurred', err);
        this.errorMessage = 'Failed to load claims. Please try again.';
        this.loading = false;
      },
    });
  }

  submitClaim(claim: ClaimResponse): void {
    this.submittingId = claim.id;

    this.claimService.submitClaim(claim.id).subscribe({
      next: (updated) => {
        const index = this.claims.findIndex((c) => c.id === updated.id);
        if (index !== -1) {
          this.claims[index] = updated;
        }
        this.submittingId = null;
      },
      error: () => {
        this.errorMessage = 'Failed to submit claim. Please try again.';
        this.submittingId = null;
      },
    });
  }
}
