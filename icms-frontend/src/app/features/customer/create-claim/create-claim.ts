import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ClaimService } from '../../../core/services/claim.service';

@Component({
  selector: 'app-create-claim',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './create-claim.html',
  styleUrl: './create-claim.css',
})
export class CreateClaim {
  claimTypes = ['HEALTH', 'VEHICLE', 'LIFE'];

  form: FormGroup;

  submitting = signal(false);
  errorMessage = signal('');

  constructor(
    private fb: FormBuilder,
    private claimService: ClaimService,
    private router: Router,
  ) {
    this.form = this.createForm();
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
    this.errorMessage.set('');

    const value = this.form.value;

    this.claimService
      .createClaim({
        policyNumber: value.policyNumber!,
        claimType: value.claimType!,
        claimAmount: value.claimAmount!,
        description: value.description || undefined,
      })
      .subscribe({
        next: () => {
          this.submitting.set(false);
          this.router.navigate(['/customer/dashboard']);
        },
        error: (err) => {
          this.submitting.set(false);
          this.errorMessage.set(
            err.error?.policyNumber ||
              err.error?.claimAmount ||
              err.error?.claimType ||
              'Failed to create claim. Please check your input.',
          );
        },
      });
  }

  cancel(): void {
    this.router.navigate(['/customer/dashboard']);
  }
}
