import { Component, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { SurveyorService } from '../../../core/services/surveyor.service';
import { ClaimResponse } from '../../../core/models/claim.model';
import { EmptyState } from '../../../shared/empty-state/empty-state';

@Component({
  selector: 'app-surveyor-dashboard',
  standalone: true,
  imports: [DecimalPipe,EmptyState],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  claims = signal<ClaimResponse[]>([]);
  loading = signal(true);
  errorMessage = signal('');
  inspectingId = signal<number | null>(null);

  constructor(private surveyorService: SurveyorService) {}

  ngOnInit(): void {
    this.loadClaims();
  }

  loadClaims(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.surveyorService.getAssignedClaims().subscribe({
      next: (claims) => {
        this.claims.set(claims);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load assigned claims.');
        this.loading.set(false);
      },
    });
  }

  inspect(claim: ClaimResponse): void {
    this.inspectingId.set(claim.id);

    this.surveyorService.inspectClaim(claim.id).subscribe({
      next: (updated) => {
        this.claims.update((list) => list.map((c) => (c.id === updated.id ? updated : c)));
        this.inspectingId.set(null);
      },
      error: () => {
        this.errorMessage.set('Failed to mark claim as inspected.');
        this.inspectingId.set(null);
      },
    });
  }
}
