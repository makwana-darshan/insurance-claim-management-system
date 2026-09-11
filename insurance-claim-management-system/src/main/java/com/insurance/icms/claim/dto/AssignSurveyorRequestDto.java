package com.insurance.icms.claim.dto;

import jakarta.validation.constraints.NotNull;

public class AssignSurveyorRequestDto {

	@NotNull(message = "Surveyor ID is required")
	private Long surveyorId;

	public Long getSurveyorId() {
		return surveyorId;
	}

	public void setSurveyorId(Long surveyorId) {
		this.surveyorId = surveyorId;
	}
}