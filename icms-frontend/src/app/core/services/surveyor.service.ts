import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ClaimResponse } from '../models/claim.model';

@Injectable({ providedIn: 'root' })
export class SurveyorService {
  private baseUrl = `${environment.apiUrl}/surveyor`;

  constructor(private http: HttpClient) {}

  getAssignedClaims(): Observable<ClaimResponse[]> {
    return this.http.get<ClaimResponse[]>(`${this.baseUrl}/dashboard`);
  }

  inspectClaim(id: number): Observable<ClaimResponse> {
    return this.http.post<ClaimResponse>(`${this.baseUrl}/claims/${id}/inspect`, {});
  }
}
