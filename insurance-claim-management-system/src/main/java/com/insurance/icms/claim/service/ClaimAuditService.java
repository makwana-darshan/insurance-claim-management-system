package com.insurance.icms.claim.service;

import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.entity.ClaimAuditLog;
import com.insurance.icms.claim.enums.ClaimStatus;
import com.insurance.icms.claim.repository.ClaimAuditRepository;
import com.insurance.icms.security.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimAuditService {

	private final ClaimAuditRepository auditRepository;

	public ClaimAuditService(ClaimAuditRepository auditRepository) {
		this.auditRepository = auditRepository;
	}

	public void logStatusChange(Claim claim, ClaimStatus from, ClaimStatus to, User actionBy, String remarks) {

		ClaimAuditLog log = new ClaimAuditLog();
		log.setClaim(claim);
		log.setFromStatus(from);
		log.setToStatus(to);
		log.setActionBy(actionBy);
		log.setRemarks(remarks);

		auditRepository.save(log);
	}

	public List<ClaimAuditLog> getTimeline(Claim claim) {
		return auditRepository.findByClaimOrderByActionAtAsc(claim);
	}
}
