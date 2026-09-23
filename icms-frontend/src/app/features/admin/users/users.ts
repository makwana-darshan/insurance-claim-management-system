import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { UserResponse } from '../../../core/models/admin.model';
import { ToastService } from '../../../core/services/toast.service';
import { EmptyState } from '../../../shared/empty-state/empty-state';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [FormsModule,EmptyState,],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users implements OnInit {
  users = signal<UserResponse[]>([]);
  loading = signal(true);

  showForm = signal(false);
  creating = signal(false);
  formError = signal('');

  togglingId = signal<number | null>(null);

  availableRoles = ['CLAIM_OFFICER', 'SURVEYOR', 'FINANCE_OFFICER', 'MEDICAL_REVIEWER'];

  fullName = '';
  email = '';
  password = '';
  role = '';

  constructor(
    private adminService: AdminService,
    private toastService: ToastService,
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);

    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users.set(users);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  toggleForm(): void {
    this.showForm.update((v) => !v);
    this.formError.set('');
  }

  createUser(): void {
    this.formError.set('');

    if (!this.fullName || !this.email || !this.password || !this.role) {
      this.formError.set('All fields are required.');
      return;
    }

    if (this.password.length < 6) {
      this.formError.set('Password must be at least 6 characters.');
      return;
    }

    this.creating.set(true);

    this.adminService
      .createUser({
        fullName: this.fullName,
        email: this.email,
        password: this.password,
        role: this.role,
      })
      .subscribe({
        next: (user) => {
          this.users.update((list) => [...list, user]);
          this.creating.set(false);
          this.showForm.set(false);
          this.fullName = '';
          this.email = '';
          this.password = '';
          this.role = '';
          this.toastService.success('User account created.');
        },
        error: () => {
          this.creating.set(false);
        },
      });
  }

  toggleStatus(user: UserResponse): void {
    const newStatus = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    this.togglingId.set(user.id);

    this.adminService.updateUserStatus(user.id, newStatus).subscribe({
      next: (updated) => {
        this.users.update((list) => list.map((u) => (u.id === updated.id ? updated : u)));
        this.togglingId.set(null);
        this.toastService.success('User status updated.');
      },
      error: () => {
        this.togglingId.set(null);
      },
    });
  }
}
