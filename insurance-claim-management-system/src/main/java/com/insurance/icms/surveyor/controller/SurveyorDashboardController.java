package com.insurance.icms.surveyor.controller;

import com.insurance.icms.claim.dto.ClaimResponseDto;
import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.service.ClaimService;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/surveyor")
public class SurveyorDashboardController {

	private final ClaimService claimService;

	public SurveyorDashboardController(ClaimService claimService) {
		this.claimService = claimService;
	}

	@GetMapping("/dashboard")
	public List<ClaimResponseDto> dashboard(Authentication authentication) {

		User surveyor = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		List<Claim> claims = claimService.getClaimsBySurveyor(surveyor);

		return claims.stream().map(ClaimResponseDto::fromEntity).collect(Collectors.toList());
	}

	@PostMapping("/claims/{id}/inspect")
	public ClaimResponseDto inspectClaim(@PathVariable Long id, Authentication authentication) {

		User surveyor = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		claimService.markInspected(id, surveyor);

		Claim claim = claimService.getClaimById(id);

		return ClaimResponseDto.fromEntity(claim);
	}
}