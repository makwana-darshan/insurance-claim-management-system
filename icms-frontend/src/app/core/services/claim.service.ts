import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ClaimRequest, ClaimResponse } from '../models/claim.model';

@Injectable({ providedIn: 'root' })
export class ClaimService {

  private baseUrl = `${environment.apiUrl}/customer`;

  constructor(private http: HttpClient) {}

  getMyClaims(): Observable<ClaimResponse[]> {
    return this.http.get<ClaimResponse[]>(`${this.baseUrl}/dashboard`);
  }

  createClaim(request: ClaimRequest): Observable<ClaimResponse> {
    return this.http.post<ClaimResponse>(`${this.baseUrl}/claims`, request);
  }

  submitClaim(id: number): Observable<ClaimResponse> {
    return this.http.post<ClaimResponse>(`${this.baseUrl}/claims/${id}/submit`, {});
  }
}