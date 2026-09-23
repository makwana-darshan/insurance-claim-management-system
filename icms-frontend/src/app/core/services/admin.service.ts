import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ClaimResponse } from '../models/claim.model';
import {
  AdminSummary,
  ClaimDecisionRequest,
  UserResponse,
  CreateUserRequest,
  UpdateUserStatusRequest,
} from '../models/admin.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private baseUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  getSummary(): Observable<AdminSummary> {
    return this.http.get<AdminSummary>(`${this.baseUrl}/dashboard`);
  }

  getAllClaims(): Observable<ClaimResponse[]> {
    return this.http.get<ClaimResponse[]>(`${this.baseUrl}/claims`);
  }

  approveClaim(id: number, request: ClaimDecisionRequest): Observable<ClaimResponse> {
    return this.http.post<ClaimResponse>(`${this.baseUrl}/claims/${id}/approve`, request);
  }

  rejectClaim(id: number, request: ClaimDecisionRequest): Observable<ClaimResponse> {
    return this.http.post<ClaimResponse>(`${this.baseUrl}/claims/${id}/reject`, request);
  }

  getAllUsers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.baseUrl}/users`);
  }

  createUser(request: CreateUserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.baseUrl}/users`, request);
  }

  updateUserStatus(
    id: number,
    status: 'ACTIVE' | 'INACTIVE' | 'BLOCKED',
  ): Observable<UserResponse> {
    return this.http.patch<UserResponse>(`${this.baseUrl}/users/${id}/status`, { status });
  }
}
