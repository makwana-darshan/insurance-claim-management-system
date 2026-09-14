export type ClaimStatus =
  | 'DRAFT'
  | 'SUBMITTED'
  | 'UNDER_REVIEW'
  | 'SURVEYOR_ASSIGNED'
  | 'INSPECTED'
  | 'APPROVED'
  | 'REJECTED';

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
