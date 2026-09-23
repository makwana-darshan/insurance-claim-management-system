package com.insurance.icms.claim.service;

import com.insurance.icms.claim.dto.ClaimRequestDto;
import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.enums.ClaimStatus;
import com.insurance.icms.claim.repository.ClaimRepository;
import com.insurance.icms.security.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public void approveClaim(Long claimId, User admin, String remarks) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		ClaimStatus oldStatus = claim.getStatus();

		claim.setStatus(ClaimStatus.APPROVED);
		claim.setUpdatedAt(LocalDateTime.now());

		claimRepository.save(claim);

		auditService.logStatusChange(claim, oldStatus, ClaimStatus.APPROVED, admin, remarks);
	}

	@Transactional
	public void rejectClaim(Long claimId, User admin, String remarks) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		ClaimStatus oldStatus = claim.getStatus();

		claim.setStatus(ClaimStatus.REJECTED);
		claim.setUpdatedAt(LocalDateTime.now());

		claimRepository.save(claim);

		auditService.logStatusChange(claim, oldStatus, ClaimStatus.REJECTED, admin, remarks);
	}

	@Transactional
	public void submitClaim(Long claimId, User customer) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		ClaimStatus oldStatus = claim.getStatus();

		claim.setStatus(ClaimStatus.SUBMITTED);
		claim.setUpdatedAt(LocalDateTime.now());

		claimRepository.save(claim);

		auditService.logStatusChange(claim, oldStatus, ClaimStatus.SUBMITTED, customer, "Claim submitted by customer");
	}

	// ---------------- ADMIN ----------------

	@Transactional
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

	@Transactional
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

	public Claim createDraftClaim(ClaimRequestDto dto, User customer) {

		Claim claim = new Claim();
		claim.setPolicyNumber(dto.getPolicyNumber());
		claim.setClaimType(dto.getClaimType());
		claim.setClaimAmount(dto.getClaimAmount());
		claim.setDescription(dto.getDescription());
		claim.setCustomer(customer);
		claim.setStatus(ClaimStatus.DRAFT);
		claim.setCreatedAt(LocalDateTime.now());

		return claimRepository.save(claim);
	}

	@Transactional
	public Claim updateDraftClaim(Long claimId, ClaimRequestDto dto, User customer) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		if (!claim.getCustomer().getId().equals(customer.getId())) {
			throw new RuntimeException("You are not authorized to edit this claim");
		}

		if (claim.getStatus() != ClaimStatus.DRAFT) {
			throw new RuntimeException("Only draft claims can be edited");
		}

		claim.setPolicyNumber(dto.getPolicyNumber());
		claim.setClaimType(dto.getClaimType());
		claim.setClaimAmount(dto.getClaimAmount());
		claim.setDescription(dto.getDescription());
		claim.setUpdatedAt(LocalDateTime.now());

		return claimRepository.save(claim);
	}

	@Transactional
	public void cancelClaim(Long claimId, User customer, String remarks) {

		Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

		if (!claim.getCustomer().getId().equals(customer.getId())) {
			throw new RuntimeException("You are not authorized to cancel this claim");
		}

		if (claim.getStatus() == ClaimStatus.APPROVED || claim.getStatus() == ClaimStatus.REJECTED
				|| claim.getStatus() == ClaimStatus.CANCELLED) {
			throw new RuntimeException("This claim can no longer be cancelled");
		}

		ClaimStatus oldStatus = claim.getStatus();

		claim.setStatus(ClaimStatus.CANCELLED);
		claim.setUpdatedAt(LocalDateTime.now());

		claimRepository.save(claim);

		auditService.logStatusChange(claim, oldStatus, ClaimStatus.CANCELLED, customer,
				remarks != null && !remarks.isBlank() ? remarks : "Cancelled by customer");
	}
}
