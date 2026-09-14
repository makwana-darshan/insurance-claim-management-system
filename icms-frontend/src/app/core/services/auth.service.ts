import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse } from '../models/auth.model';

const TOKEN_KEY = 'icms_token';
const USER_KEY = 'icms_user';

@Injectable({ providedIn: 'root' })
export class AuthService {

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(response => {
          localStorage.setItem(TOKEN_KEY, response.token);
          localStorage.setItem(USER_KEY, JSON.stringify({
            email: response.email,
            fullName: response.fullName,
            roles: response.roles
          }));
        })
      );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  getUser(): { email: string; fullName: string; roles: string[] } | null {
    const user = localStorage.getItem(USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  hasRole(role: string): boolean {
    const user = this.getUser();
    return user ? user.roles.includes(role) : false;
  }

  // Role -> default landing route, mirrors old CustomAuthenticationSuccessHandler logic
  getDefaultRouteForUser(): string {
    const user = this.getUser();
    if (!user) return '/login';

    if (user.roles.includes('SUPER_ADMIN')) return '/admin/dashboard';
    if (user.roles.includes('CLAIM_OFFICER')) return '/claim-officer/dashboard';
    if (user.roles.includes('SURVEYOR')) return '/surveyor/dashboard';
    if (user.roles.includes('CUSTOMER')) return '/customer/dashboard';

    return '/login';
  }
}