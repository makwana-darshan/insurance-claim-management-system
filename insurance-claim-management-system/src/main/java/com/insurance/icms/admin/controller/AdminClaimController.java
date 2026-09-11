package com.insurance.icms.admin.controller;

import com.insurance.icms.claim.dto.ClaimDecisionRequestDto;
import com.insurance.icms.claim.dto.ClaimResponseDto;
import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.repository.ClaimRepository;
import com.insurance.icms.claim.service.ClaimService;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/claims")
public class AdminClaimController {

	private final ClaimRepository claimRepository;
	private final ClaimService claimService;

	public AdminClaimController(ClaimRepository claimRepository, ClaimService claimService) {
		this.claimRepository = claimRepository;
		this.claimService = claimService;
	}

	@GetMapping
	public List<ClaimResponseDto> getAllClaims() {

		List<Claim> claims = claimRepository.findAll();

		return claims.stream().map(ClaimResponseDto::fromEntity).collect(Collectors.toList());
	}

	@PostMapping("/{id}/approve")
	public ClaimResponseDto approveClaim(@PathVariable Long id, @RequestBody ClaimDecisionRequestDto request,
			Authentication authentication) {

		User admin = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		claimService.approveClaim(id, admin, request.getRemarks());

		return ClaimResponseDto.fromEntity(claimService.getClaimById(id));
	}

	@PostMapping("/{id}/reject")
	public ClaimResponseDto rejectClaim(@PathVariable Long id, @RequestBody ClaimDecisionRequestDto request,
			Authentication authentication) {

		User admin = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		claimService.rejectClaim(id, admin, request.getRemarks());

		return ClaimResponseDto.fromEntity(claimService.getClaimById(id));
	}
}