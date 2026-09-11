package com.insurance.icms.controller;

import com.insurance.icms.claim.dto.ClaimResponseDto;
import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.service.ClaimService;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer")
public class CustomerDashboardController {

	private final ClaimService claimService;

	public CustomerDashboardController(ClaimService claimService) {
		this.claimService = claimService;
	}

	@GetMapping("/dashboard")
	public List<ClaimResponseDto> dashboard(Authentication authentication) {

		User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		List<Claim> claims = claimService.getClaimsByCustomer(user);

		return claims.stream().map(ClaimResponseDto::fromEntity).collect(Collectors.toList());
	}
}