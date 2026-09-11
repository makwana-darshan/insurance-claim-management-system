package com.insurance.icms.claim.dto;

import com.insurance.icms.claim.entity.ClaimAuditLog;
import com.insurance.icms.claim.enums.ClaimStatus;

import java.time.LocalDateTime;

public class ClaimAuditLogDto {

	private Long id;
	private ClaimStatus fromStatus;
	private ClaimStatus toStatus;
	private String actionByName;
	private String remarks;
	private LocalDateTime actionAt;

	public static ClaimAuditLogDto fromEntity(ClaimAuditLog log) {
		ClaimAuditLogDto dto = new ClaimAuditLogDto();
		dto.id = log.getId();
		dto.fromStatus = log.getFromStatus();
		dto.toStatus = log.getToStatus();
		dto.actionByName = log.getActionBy() != null ? log.getActionBy().getFullName() : null;
		dto.remarks = log.getRemarks();
		dto.actionAt = log.getActionAt();
		return dto;
	}

	public Long getId() {
		return id;
	}

	public ClaimStatus getFromStatus() {
		return fromStatus;
	}

	public ClaimStatus getToStatus() {
		return toStatus;
	}

	public String getActionByName() {
		return actionByName;
	}

	public String getRemarks() {
		return remarks;
	}

	public LocalDateTime getActionAt() {
		return actionAt;
	}
}