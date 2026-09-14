export interface AdminSummary {
  totalClaims: number;
  submittedClaims: number;
  underReviewClaims: number;
  approvedClaims: number;
  rejectedClaims: number;
  totalUsers: number;
}

export interface ClaimDecisionRequest {
  remarks: string;
}
