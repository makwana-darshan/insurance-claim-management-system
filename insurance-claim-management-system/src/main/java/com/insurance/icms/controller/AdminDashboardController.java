package com.insurance.icms.controller;

import com.insurance.icms.claim.enums.ClaimStatus;
import com.insurance.icms.claim.repository.ClaimRepository;
import com.insurance.icms.security.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

	private final ClaimRepository claimRepository;
	private final UserRepository userRepository;

	public AdminDashboardController(ClaimRepository claimRepository, UserRepository userRepository) {
		this.claimRepository = claimRepository;
		this.userRepository = userRepository;
	}

	@GetMapping("/dashboard")
	public Map<String, Object> dashboard() {

		Map<String, Object> summary = new HashMap<>();

		summary.put("totalClaims", claimRepository.count());
		summary.put("submittedClaims", claimRepository.findByStatus(ClaimStatus.SUBMITTED).size());
		summary.put("underReviewClaims", claimRepository.findByStatus(ClaimStatus.UNDER_REVIEW).size());
		summary.put("approvedClaims", claimRepository.findByStatus(ClaimStatus.APPROVED).size());
		summary.put("rejectedClaims", claimRepository.findByStatus(ClaimStatus.REJECTED).size());
		summary.put("totalUsers", userRepository.count());

		return summary;
	}
}