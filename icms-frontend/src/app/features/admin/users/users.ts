import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { UserResponse } from '../../../core/models/admin.model';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users implements OnInit {
  users = signal<UserResponse[]>([]);
  loading = signal(true);
  errorMessage = signal('');

  showForm = signal(false);
  creating = signal(false);
  formError = signal('');

  availableRoles = ['CLAIM_OFFICER', 'SURVEYOR', 'FINANCE_OFFICER', 'MEDICAL_REVIEWER'];

  fullName = '';
  email = '';
  password = '';
  role = '';

  constructor(private adminService: AdminService) {}

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
        this.errorMessage.set('Failed to load users.');
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
        },
        error: (err) => {
          this.creating.set(false);
          this.formError.set(err.error?.error || 'Failed to create user.');
        },
      });
  }
}
