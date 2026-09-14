import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ClaimResponse } from '../models/claim.model';
import { Surveyor, AssignSurveyorRequest } from '../models/claim-officer.model';

@Injectable({ providedIn: 'root' })
export class ClaimOfficerService {
  private baseUrl = `${environment.apiUrl}/claim-officer`;

  constructor(private http: HttpClient) {}

  getSubmittedClaims(): Observable<ClaimResponse[]> {
    return this.http.get<ClaimResponse[]>(`${this.baseUrl}/dashboard`);
  }

  getSurveyors(): Observable<Surveyor[]> {
    return this.http.get<Surveyor[]>(`${this.baseUrl}/surveyors`);
  }

  assignSurveyor(claimId: number, request: AssignSurveyorRequest): Observable<ClaimResponse> {
    return this.http.post<ClaimResponse>(`${this.baseUrl}/claims/${claimId}/assign`, request);
  }
}
