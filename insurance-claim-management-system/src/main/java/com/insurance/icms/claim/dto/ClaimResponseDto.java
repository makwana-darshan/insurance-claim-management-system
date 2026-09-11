package com.insurance.icms.claim.dto;

import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.enums.ClaimStatus;

import java.time.LocalDateTime;

public class ClaimResponseDto {

	private Long id;
	private String policyNumber;
	private String claimType;
	private Double claimAmount;
	private String description;
	private ClaimStatus status;
	private String customerName;
	private String surveyorName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static ClaimResponseDto fromEntity(Claim claim) {
		ClaimResponseDto dto = new ClaimResponseDto();
		dto.id = claim.getId();
		dto.policyNumber = claim.getPolicyNumber();
		dto.claimType = claim.getClaimType();
		dto.claimAmount = claim.getClaimAmount();
		dto.description = claim.getDescription();
		dto.status = claim.getStatus();
		dto.customerName = claim.getCustomer() != null ? claim.getCustomer().getFullName() : null;
		dto.surveyorName = claim.getSurveyor() != null ? claim.getSurveyor().getFullName() : null;
		dto.createdAt = claim.getCreatedAt();
		dto.updatedAt = claim.getUpdatedAt();
		return dto;
	}

	public Long getId() {
		return id;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public String getClaimType() {
		return claimType;
	}

	public Double getClaimAmount() {
		return claimAmount;
	}

	public String getDescription() {
		return description;
	}

	public ClaimStatus getStatus() {
		return status;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getSurveyorName() {
		return surveyorName;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}