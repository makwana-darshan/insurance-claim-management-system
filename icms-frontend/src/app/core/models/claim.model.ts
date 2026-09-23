export type ClaimStatus =
  | 'DRAFT'
  | 'SUBMITTED'
  | 'UNDER_REVIEW'
  | 'SURVEYOR_ASSIGNED'
  | 'INSPECTED'
  | 'APPROVED'
  | 'REJECTED'
  | 'CANCELLED';

export interface CancelClaimRequest {
  remarks?: string;
}

export interface ClaimRequest {
  policyNumber: string;
  claimType: string;
  claimAmount: number;
  description?: string;
}

export interface ClaimResponse {
  id: number;
  policyNumber: string;
  claimType: string;
  claimAmount: number;
  description: string | null;
  status: ClaimStatus;
  customerName: string | null;
  surveyorName: string | null;
  createdAt: string;
  updatedAt: string | null;
}
export interface ClaimAuditLog {
  id: number;
  fromStatus: ClaimStatus | null;
  toStatus: ClaimStatus;
  actionByName: string | null;
  remarks: string | null;
  actionAt: string;
}

export interface ClaimTimelineResponse {
  claim: ClaimResponse;
  timeline: ClaimAuditLog[];
}
