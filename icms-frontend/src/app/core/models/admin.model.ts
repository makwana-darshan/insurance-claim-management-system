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

export interface UserResponse {
  id: number;
  fullName: string;
  email: string;
  status: 'ACTIVE' | 'INACTIVE' | 'BLOCKED';
  roles: string[];
}

export interface CreateUserRequest {
  fullName: string;
  email: string;
  password: string;
  role: string;
}

export interface UpdateUserStatusRequest {
  status: 'ACTIVE' | 'INACTIVE' | 'BLOCKED';
}
