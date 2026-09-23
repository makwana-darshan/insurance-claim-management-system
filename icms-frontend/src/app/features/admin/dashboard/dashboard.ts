import { Component, OnInit, computed, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../../core/services/admin.service';
import { ClaimResponse, ClaimStatus } from '../../../core/models/claim.model';
import { AdminSummary } from '../../../core/models/admin.model';
import { ToastService } from '../../../core/services/toast.service';
import { EmptyState } from '../../../shared/empty-state/empty-state';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [DecimalPipe, FormsModule, RouterLink,EmptyState],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  summary = signal<AdminSummary | null>(null);
  claims = signal<ClaimResponse[]>([]);
  remarks = signal<Record<number, string>>({});
  loading = signal(true);
  actingId = signal<number | null>(null);

  searchTerm = signal('');
  statusFilter = signal<ClaimStatus | 'ALL'>('ALL');

  statusOptions: (ClaimStatus | 'ALL')[] = [
    'ALL',
    'DRAFT',
    'SUBMITTED',
    'UNDER_REVIEW',
    'SURVEYOR_ASSIGNED',
    'INSPECTED',
    'APPROVED',
    'REJECTED',
    'CANCELLED',
  ];

  filteredClaims = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const status = this.statusFilter();

    return this.claims().filter((claim) => {
      const matchesStatus = status === 'ALL' || claim.status === status;

      const matchesSearch =
        !term ||
        claim.policyNumber.toLowerCase().includes(term) ||
        (claim.customerName || '').toLowerCase().includes(term);

      return matchesStatus && matchesSearch;
    });
  });

  constructor(
    private adminService: AdminService,
    private toastService: ToastService,
  ) {}

  ngOnInit(): void {
    this.loadSummary();
    this.loadClaims();
  }

  loadSummary(): void {
    this.adminService.getSummary().subscribe({
      next: (summary) => this.summary.set(summary),
      error: () => {},
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
        this.loading.set(false);
      },
    });
  }

  onSearchChange(value: string): void {
    this.searchTerm.set(value);
  }

  onStatusFilterChange(value: string): void {
    this.statusFilter.set(value as ClaimStatus | 'ALL');
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
        this.toastService.success('Claim approved.');
      },
      error: () => {
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
        this.toastService.success('Claim rejected.');
      },
      error: () => {
        this.actingId.set(null);
      },
    });
  }
}
