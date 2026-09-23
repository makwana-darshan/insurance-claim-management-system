import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClaimService } from '../../../core/services/claim.service';
import { AdminService } from '../../../core/services/admin.service';
import { ClaimTimelineResponse } from '../../../core/models/claim.model';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-admin-claim-detail',
  standalone: true,
  imports: [DatePipe, DecimalPipe, FormsModule, RouterLink],
  templateUrl: './claim-detail.html',
  styleUrl: './claim-detail.css',
})
export class ClaimDetail implements OnInit {
  data = signal<ClaimTimelineResponse | null>(null);
  loading = signal(true);
  acting = signal(false);
  remarks = '';

  claimId!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private claimService: ClaimService,
    private adminService: AdminService,
    private toastService: ToastService,
  ) {}

  ngOnInit(): void {
    this.claimId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadClaim();
  }

  loadClaim(): void {
    this.loading.set(true);

    this.claimService.getTimeline(this.claimId).subscribe({
      next: (response) => {
        this.data.set(response);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  approve(): void {
    this.acting.set(true);

    this.adminService.approveClaim(this.claimId, { remarks: this.remarks }).subscribe({
      next: () => {
        this.acting.set(false);
        this.remarks = '';
        this.toastService.success('Claim approved.');
        this.loadClaim();
      },
      error: () => {
        this.acting.set(false);
      },
    });
  }

  reject(): void {
    this.acting.set(true);

    this.adminService.rejectClaim(this.claimId, { remarks: this.remarks }).subscribe({
      next: () => {
        this.acting.set(false);
        this.remarks = '';
        this.toastService.success('Claim rejected.');
        this.loadClaim();
      },
      error: () => {
        this.acting.set(false);
      },
    });
  }

  back(): void {
    this.router.navigate(['/admin/dashboard']);
  }
}
