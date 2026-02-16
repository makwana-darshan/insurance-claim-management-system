package com.insurance.icms.claim.entity;

import com.insurance.icms.claim.enums.ClaimStatus;
import com.insurance.icms.security.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "claim_audit_logs")
public class ClaimAuditLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "claim_id")
	private Claim claim;

	@Enumerated(EnumType.STRING)
	private ClaimStatus fromStatus;

	@Enumerated(EnumType.STRING)
	private ClaimStatus toStatus;

	@ManyToOne
	@JoinColumn(name = "action_by")
	private User actionBy;

	private String remarks;

	private LocalDateTime actionAt;

	@PrePersist
	public void onCreate() {
		actionAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public ClaimStatus getFromStatus() {
		return fromStatus;
	}

	public void setFromStatus(ClaimStatus fromStatus) {
		this.fromStatus = fromStatus;
	}

	public ClaimStatus getToStatus() {
		return toStatus;
	}

	public void setToStatus(ClaimStatus toStatus) {
		this.toStatus = toStatus;
	}

	public User getActionBy() {
		return actionBy;
	}

	public void setActionBy(User actionBy) {
		this.actionBy = actionBy;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDateTime getActionAt() {
		return actionAt;
	}

	public void setActionAt(LocalDateTime actionAt) {
		this.actionAt = actionAt;
	}

}
