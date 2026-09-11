package com.insurance.icms.claim.controller;

import com.insurance.icms.claim.dto.ClaimRequestDto;
import com.insurance.icms.claim.dto.ClaimResponseDto;
import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.service.ClaimService;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/claims")
public class CustomerClaimController {

	private final ClaimService claimService;

	public CustomerClaimController(ClaimService claimService) {
		this.claimService = claimService;
	}

	// Create DRAFT claim
	@PostMapping
	public ClaimResponseDto createClaim(@RequestBody ClaimRequestDto request, Authentication authentication) {

		User customer = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		Claim claim = claimService.createDraftClaim(request, customer);

		return ClaimResponseDto.fromEntity(claim);
	}

	// Submit existing claim
	@PostMapping("/{id}/submit")
	public ClaimResponseDto submitClaim(@PathVariable Long id, Authentication authentication) {

		User customer = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		claimService.submitClaim(id, customer);

		Claim claim = claimService.getClaimById(id);

		return ClaimResponseDto.fromEntity(claim);
	}
}