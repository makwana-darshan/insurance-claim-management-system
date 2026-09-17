import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent implements OnInit {
  email = '';
  password = '';
  errorMessage = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    this.titleService.setTitle('ICMS - Login');

    if (this.route.snapshot.queryParamMap.get('sessionExpired') === 'true') {
      this.errorMessage = 'Your session has expired. Please log in again.';
    }
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.loading = true;

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: () => {
        this.loading = false;
        const route = this.authService.getDefaultRouteForUser();
        this.router.navigate([route]);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err.status === 401 || err.status === 403
            ? 'Invalid email or password'
            : 'Something went wrong. Please try again.';
      },
    });
  }
}
