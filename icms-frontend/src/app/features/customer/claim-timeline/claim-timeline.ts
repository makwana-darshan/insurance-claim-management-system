import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
import { ClaimService } from '../../../core/services/claim.service';
import { ClaimTimelineResponse } from '../../../core/models/claim.model';
import { EmptyState } from '../../../shared/empty-state/empty-state';

@Component({
  selector: 'app-claim-timeline',
  standalone: true,
  imports: [DatePipe, DecimalPipe, RouterLink,EmptyState],
  templateUrl: './claim-timeline.html',
  styleUrl: './claim-timeline.css',
})
export class ClaimTimeline implements OnInit {

  data = signal<ClaimTimelineResponse | null>(null);
  loading = signal(true);
  errorMessage = signal('');

  constructor(
    private route: ActivatedRoute,
    private claimService: ClaimService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.claimService.getTimeline(id).subscribe({
      next: (response) => {
        this.data.set(response);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load claim timeline.');
        this.loading.set(false);
      }
    });
  }
}