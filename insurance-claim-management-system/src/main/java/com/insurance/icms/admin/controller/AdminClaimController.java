package com.insurance.icms.admin.controller;

import com.insurance.icms.claim.enums.ClaimStatus;
import com.insurance.icms.claim.service.ClaimService;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/claims")
public class AdminClaimController {

	private final ClaimService claimService;
	private final UserService userService;

	public AdminClaimController(ClaimService claimService, UserService userService) {
		this.claimService = claimService;
		this.userService = userService;
	}

	@GetMapping
	public String viewClaims(Model model) {

		model.addAttribute("claims", claimService.getClaimsByStatus(ClaimStatus.SUBMITTED));

		model.addAttribute("surveyors", userService.getAllSurveyors());

		return "admin/claims";
	}

	@PostMapping("/assign")
	public String assignSurveyor(@RequestParam Long claimId, @RequestParam Long surveyorId,
			Authentication authentication) {

		// Logged-in admin
		User admin = (User) authentication.getPrincipal();

		// Selected surveyor
		User surveyor = userService.getUserById(surveyorId);

		// ✅ CORRECT METHOD CALL
		claimService.assignSurveyor(claimId, admin, surveyor);

		return "redirect:/admin/claims";
	}
}
