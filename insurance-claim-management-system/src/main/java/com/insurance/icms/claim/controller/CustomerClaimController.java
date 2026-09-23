package com.insurance.icms.claim.controller;

import com.insurance.icms.claim.dto.CancelClaimRequestDto;
import com.insurance.icms.claim.dto.ClaimRequestDto;
import com.insurance.icms.claim.dto.ClaimResponseDto;
import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.service.ClaimService;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.service.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/claims")
public class CustomerClaimController {

	private final ClaimService claimService;

	public CustomerClaimController(ClaimService claimService) {
		this.claimService = claimService;
	}

	@GetMapping("/{id}")
	public ClaimResponseDto getClaim(@PathVariable Long id, Authentication authentication) {

		User customer = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		Claim claim = claimService.getClaimById(id);

		if (!claim.getCustomer().getId().equals(customer.getId())) {
			throw new RuntimeException("You are not authorized to view this claim");
		}

		return ClaimResponseDto.fromEntity(claim);
	}

	@PostMapping
	public ClaimResponseDto createClaim(@Valid @RequestBody ClaimRequestDto request, Authentication authentication) {

		User customer = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		Claim claim = claimService.createDraftClaim(request, customer);

		return ClaimResponseDto.fromEntity(claim);
	}

	@PutMapping("/{id}")
	public ClaimResponseDto updateClaim(@PathVariable Long id, @Valid @RequestBody ClaimRequestDto request,
			Authentication authentication) {

		User customer = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		Claim claim = claimService.updateDraftClaim(id, request, customer);

		return ClaimResponseDto.fromEntity(claim);
	}

	@PostMapping("/{id}/submit")
	public ClaimResponseDto submitClaim(@PathVariable Long id, Authentication authentication) {

		User customer = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		claimService.submitClaim(id, customer);

		Claim claim = claimService.getClaimById(id);

		return ClaimResponseDto.fromEntity(claim);
	}

	@PostMapping("/{id}/cancel")
	public ClaimResponseDto cancelClaim(@PathVariable Long id,
			@RequestBody(required = false) CancelClaimRequestDto request, Authentication authentication) {

		User customer = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		String remarks = request != null ? request.getRemarks() : null;

		claimService.cancelClaim(id, customer, remarks);

		Claim claim = claimService.getClaimById(id);

		return ClaimResponseDto.fromEntity(claim);
	}
}