import { Component, OnInit, signal } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClaimService } from '../../../core/services/claim.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-create-claim',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './create-claim.html',
  styleUrl: './create-claim.css',
})
export class CreateClaim implements OnInit {
  claimTypes = ['HEALTH', 'VEHICLE', 'LIFE'];

  form: FormGroup;

  submitting = signal(false);
  loading = signal(false);
  errorMessage = signal('');

  editingId: number | null = null;
  isEditMode = signal(false);

  constructor(
    private fb: FormBuilder,
    private claimService: ClaimService,
    private router: Router,
    private route: ActivatedRoute,
    private toastService: ToastService,
  ) {
    this.form = this.createForm();
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.editingId = Number(idParam);
      this.isEditMode.set(true);
      this.loadClaim(this.editingId);
    }
  }

  private loadClaim(id: number): void {
    this.loading.set(true);

    this.claimService.getClaim(id).subscribe({
      next: (claim) => {
        this.form.patchValue({
          policyNumber: claim.policyNumber,
          claimType: claim.claimType,
          claimAmount: claim.claimAmount,
          description: claim.description || '',
        });
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  private createForm() {
    return this.fb.group({
      policyNumber: ['', [Validators.required]],
      claimType: ['', [Validators.required]],
      claimAmount: [null as number | null, [Validators.required, Validators.min(0.01)]],
      description: [''],
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);

    const value = this.form.value;
    const payload = {
      policyNumber: value.policyNumber!,
      claimType: value.claimType!,
      claimAmount: value.claimAmount!,
      description: value.description || undefined,
    };

    const request$ = this.isEditMode()
      ? this.claimService.updateClaim(this.editingId!, payload)
      : this.claimService.createClaim(payload);

    request$.subscribe({
      next: () => {
        this.submitting.set(false);
        this.toastService.success('Claim saved successfully.');
        this.router.navigate(['/customer/dashboard']);
      },
      error: () => {
        this.submitting.set(false);
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/customer/dashboard']);
  }
}
