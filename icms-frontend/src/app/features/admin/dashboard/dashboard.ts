import { Component, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { ClaimResponse } from '../../../core/models/claim.model';
import { AdminSummary } from '../../../core/models/admin.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [DatePipe, DecimalPipe, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  summary = signal<AdminSummary | null>(null);
  claims = signal<ClaimResponse[]>([]);
  remarks = signal<Record<number, string>>({});
  loading = signal(true);
  errorMessage = signal('');
  actingId = signal<number | null>(null);

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadSummary();
    this.loadClaims();
  }

  loadSummary(): void {
    this.adminService.getSummary().subscribe({
      next: (summary) => this.summary.set(summary),
      error: () => this.errorMessage.set('Failed to load summary.'),
    });
  }

  loadClaims(): void {
    this.loading.set(true);

    this.adminService.getAllClaims().subscribe({
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

  onRemarksChange(claimId: number, value: string): void {
    this.remarks.update((map) => ({ ...map, [claimId]: value }));
  }

  approve(claim: ClaimResponse): void {
    this.actingId.set(claim.id);
    const remarksText = this.remarks()[claim.id] || '';

    this.adminService.approveClaim(claim.id, { remarks: remarksText }).subscribe({
      next: (updated) => {
        this.claims.update((list) => list.map((c) => (c.id === updated.id ? updated : c)));
        this.actingId.set(null);
        this.loadSummary();
      },
      error: () => {
        this.errorMessage.set('Failed to approve claim.');
        this.actingId.set(null);
      },
    });
  }

  reject(claim: ClaimResponse): void {
    this.actingId.set(claim.id);
    const remarksText = this.remarks()[claim.id] || '';

    this.adminService.rejectClaim(claim.id, { remarks: remarksText }).subscribe({
      next: (updated) => {
        this.claims.update((list) => list.map((c) => (c.id === updated.id ? updated : c)));
        this.actingId.set(null);
        this.loadSummary();
      },
      error: () => {
        this.errorMessage.set('Failed to reject claim.');
        this.actingId.set(null);
      },
    });
  }
}
