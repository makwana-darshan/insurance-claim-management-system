import { Component, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClaimOfficerService } from '../../../core/services/claim-officer.service';
import { ClaimResponse } from '../../../core/models/claim.model';
import { Surveyor } from '../../../core/models/claim-officer.model';

@Component({
  selector: 'app-claim-officer-dashboard',
  standalone: true,
  imports: [DatePipe, DecimalPipe, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  claims = signal<ClaimResponse[]>([]);
  surveyors = signal<Surveyor[]>([]);
  selectedSurveyor = signal<Record<number, number | null>>({});
  loading = signal(true);
  errorMessage = signal('');
  assigningId = signal<number | null>(null);

  constructor(private claimOfficerService: ClaimOfficerService) {}

  ngOnInit(): void {
    this.loadClaims();
    this.loadSurveyors();
  }

  loadClaims(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.claimOfficerService.getSubmittedClaims().subscribe({
      next: (claims) => {
        this.claims.set(claims);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load claims.');
        this.loading.set(false);
      },
    });
  }

  loadSurveyors(): void {
    this.claimOfficerService.getSurveyors().subscribe({
      next: (surveyors) => this.surveyors.set(surveyors),
      error: () => this.errorMessage.set('Failed to load surveyors.'),
    });
  }

  onSurveyorSelect(claimId: number, surveyorId: string): void {
    this.selectedSurveyor.update((map) => ({
      ...map,
      [claimId]: surveyorId ? Number(surveyorId) : null,
    }));
  }

  assignSurveyor(claim: ClaimResponse): void {
    const surveyorId = this.selectedSurveyor()[claim.id];
    if (!surveyorId) {
      this.errorMessage.set('Please select a surveyor first.');
      return;
    }

    this.assigningId.set(claim.id);

    this.claimOfficerService.assignSurveyor(claim.id, { surveyorId }).subscribe({
      next: (updated) => {
        this.claims.update((list) => list.filter((c) => c.id !== updated.id));
        this.assigningId.set(null);
      },
      error: () => {
        this.errorMessage.set('Failed to assign surveyor.');
        this.assigningId.set(null);
      },
    });
  }
}
