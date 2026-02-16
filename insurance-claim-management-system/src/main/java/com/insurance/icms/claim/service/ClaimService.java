package com.insurance.icms.claim.service;

import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.enums.ClaimStatus;
import com.insurance.icms.claim.repository.ClaimRepository;
import com.insurance.icms.security.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClaimService {

	private final ClaimRepository claimRepository;
	private final ClaimAuditService auditService;

	public ClaimService(ClaimRepository claimRepository, ClaimAuditService auditService) {
		this.claimRepository = claimRepository;
		this.auditService = auditService;
	}

	// ---------------- CUSTOMER ----------------

	public void createDraftClaim(Claim claim, User customer) {

		claim.setCustomer(customer);
		claim.setStatus(ClaimStatus.DRAFT);
		claim.setCreatedAt(LocalDateTime.now());

		claimRepository.save(claim);
	}

	public void submitClaim(Long claimId, User customer) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		ClaimStatus oldStatus = claim.getStatus();

		claim.setStatus(ClaimStatus.SUBMITTED);
		claim.setUpdatedAt(LocalDateTime.now());

		claimRepository.save(claim);

		auditService.logStatusChange(claim, oldStatus, ClaimStatus.SUBMITTED, customer, "Claim submitted by customer");
	}

	

	// ---------------- ADMIN ----------------

	public void assignSurveyor(Long claimId, User admin, User surveyor) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		ClaimStatus oldStatus = claim.getStatus();

		claim.setSurveyor(surveyor);
		claim.setStatus(ClaimStatus.UNDER_REVIEW);

		claimRepository.save(claim);

		auditService.logStatusChange(claim, oldStatus, ClaimStatus.UNDER_REVIEW, admin, "Surveyor assigned");
	}

	public List<Claim> getClaimsByStatus(ClaimStatus status) {
		return claimRepository.findByStatus(status);
	}

	// ---------------- SURVEYOR ----------------

	public List<Claim> getClaimsBySurveyor(User surveyor) {
		return claimRepository.findBySurveyor(surveyor);
	}

	public void markInspected(Long claimId, User surveyor) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		ClaimStatus oldStatus = claim.getStatus();

		claim.setStatus(ClaimStatus.INSPECTED);
		claim.setUpdatedAt(LocalDateTime.now());

		claimRepository.save(claim);

		auditService.logStatusChange(claim, oldStatus, ClaimStatus.INSPECTED, surveyor, "Claim inspected by surveyor");
	}

	// ---------------- COMMON ----------------

	public List<Claim> getClaimsByCustomer(User customer) {
		return claimRepository.findByCustomer(customer);
	}

	public Claim getClaimById(Long id) {
		return claimRepository.findById(id).orElseThrow(() -> new RuntimeException("Claim not found"));
	}
}
