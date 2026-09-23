package com.insurance.icms.security.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRequest {

	@NotBlank(message = "Full name is required")
	private String fullName;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}