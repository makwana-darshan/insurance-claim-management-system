package com.insurance.icms.security.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateUserStatusRequest {

	@NotNull(message = "Status is required")
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}