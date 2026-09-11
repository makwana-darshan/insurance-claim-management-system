package com.insurance.icms.claim.controller;

import com.insurance.icms.claim.dto.AssignSurveyorRequestDto;
import com.insurance.icms.claim.dto.ClaimResponseDto;
import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.enums.ClaimStatus;
import com.insurance.icms.claim.service.ClaimService;
import com.insurance.icms.security.dto.UserSummaryDto;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.repository.UserRepository;
import com.insurance.icms.security.service.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/claim-officer")
public class ClaimOfficerController {

	private final ClaimService claimService;
	private final UserRepository userRepository;

	public ClaimOfficerController(ClaimService claimService, UserRepository userRepository) {
		this.claimService = claimService;
		this.userRepository = userRepository;
	}

	@GetMapping("/dashboard")
	public List<ClaimResponseDto> dashboard() {

		List<Claim> claims = claimService.getClaimsByStatus(ClaimStatus.SUBMITTED);

		return claims.stream().map(ClaimResponseDto::fromEntity).collect(Collectors.toList());
	}

	@GetMapping("/surveyors")
	public List<UserSummaryDto> getSurveyors() {

		return userRepository.findByRole("SURVEYOR").stream().map(UserSummaryDto::fromEntity)
				.collect(Collectors.toList());
	}

	@PostMapping("/claims/{claimId}/assign")
	public ClaimResponseDto assignSurveyor(@PathVariable Long claimId,
			@Valid @RequestBody AssignSurveyorRequestDto request, Authentication authentication) {

		User officer = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		User surveyor = userRepository.findById(request.getSurveyorId())
				.orElseThrow(() -> new RuntimeException("Surveyor not found"));

		claimService.assignSurveyor(claimId, officer, surveyor);

		Claim claim = claimService.getClaimById(claimId);

		return ClaimResponseDto.fromEntity(claim);
	}
}