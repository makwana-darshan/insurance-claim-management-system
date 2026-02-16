package com.insurance.icms.controller;

import com.insurance.icms.claim.service.ClaimService;
import com.insurance.icms.security.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/surveyor")
public class SurveyorDashboardController {

	private final ClaimService claimService;

	public SurveyorDashboardController(ClaimService claimService) {
		this.claimService = claimService;
	}

	@GetMapping("/dashboard")
	public String dashboard(Authentication authentication, Model model) {

		User surveyor = (User) authentication.getPrincipal();
		model.addAttribute("claims", claimService.getClaimsBySurveyor(surveyor));

		return "surveyor/dashboard";
	}

	@PostMapping("/inspect/{id}")
	public String inspect(@PathVariable Long id, Authentication authentication) {

		User surveyor = (User) authentication.getPrincipal();
		claimService.markInspected(id, surveyor);

		return "redirect:/surveyor/dashboard";
	}
}
