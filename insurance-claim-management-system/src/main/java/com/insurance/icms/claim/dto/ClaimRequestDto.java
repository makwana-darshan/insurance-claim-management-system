package com.insurance.icms.claim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ClaimRequestDto {

	@NotBlank(message = "Policy number is required")
	private String policyNumber;

	@NotBlank(message = "Claim type is required")
	private String claimType;

	@NotNull(message = "Claim amount is required")
	@Positive(message = "Claim amount must be greater than zero")
	private Double claimAmount;

	private String description;

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getClaimType() {
		return claimType;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	public Double getClaimAmount() {
		return claimAmount;
	}

	public void setClaimAmount(Double claimAmount) {
		this.claimAmount = claimAmount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}