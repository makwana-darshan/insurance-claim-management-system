import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ClaimResponse } from '../models/claim.model';
import { AdminSummary, ClaimDecisionRequest } from '../models/admin.model';

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
}