import { Component, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { ClaimService } from '../../../core/services/claim.service';
import { ClaimResponse } from '../../../core/models/claim.model';
import { EmptyState } from '../../../shared/empty-state/empty-state';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [DatePipe, DecimalPipe, RouterLink, EmptyState],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {

  claims = signal<ClaimResponse[]>([]);
  loading = signal(true);
  submittingId = signal<number | null>(null);
  cancellingId = signal<number | null>(null);
  confirmingCancelId = signal<number | null>(null);

  constructor(
    private claimService: ClaimService,
    private router: Router,
    private toastService: ToastService,
  ) {}

  ngOnInit(): void {
    this.loadClaims();
  }

  loadClaims(): void {
    this.loading.set(true);

    this.claimService.getMyClaims().subscribe({
      next: (claims) => {
        this.claims.set(claims);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  editClaim(claim: ClaimResponse): void {
    this.router.navigate(['/customer/claims', claim.id, 'edit']);
  }

  submitClaim(claim: ClaimResponse): void {
    this.submittingId.set(claim.id);

    this.claimService.submitClaim(claim.id).subscribe({
      next: (updated) => {
        this.claims.update(list => list.map(c => c.id === updated.id ? updated : c));
        this.submittingId.set(null);
        this.toastService.success('Claim submitted successfully.');
      },
      error: () => {
        this.submittingId.set(null);
      }
    });
  }

  startCancel(claim: ClaimResponse): void {
    this.confirmingCancelId.set(claim.id);
  }

  dismissCancel(): void {
    this.confirmingCancelId.set(null);
  }

  confirmCancel(claim: ClaimResponse): void {
    this.cancellingId.set(claim.id);

    this.claimService.cancelClaim(claim.id).subscribe({
      next: (updated) => {
        this.claims.update(list => list.map(c => c.id === updated.id ? updated : c));
        this.cancellingId.set(null);
        this.confirmingCancelId.set(null);
        this.toastService.success('Claim cancelled.');
      },
      error: () => {
        this.cancellingId.set(null);
        this.confirmingCancelId.set(null);
      }
    });
  }
}