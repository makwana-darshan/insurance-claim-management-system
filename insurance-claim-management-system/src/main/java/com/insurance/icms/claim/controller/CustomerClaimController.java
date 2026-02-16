package com.insurance.icms.claim.controller;

import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.service.ClaimService;
import com.insurance.icms.security.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer/claims")
public class CustomerClaimController {

	private final ClaimService claimService;

	public CustomerClaimController(ClaimService claimService) {
		this.claimService = claimService;
	}

	// Show create form
	@GetMapping("/new")
	public String showClaimForm(Model model) {
		model.addAttribute("claim", new Claim());
		return "customer/create-claim";
	}

	// Create DRAFT claim
	@PostMapping("/create")
	public String createClaim(@ModelAttribute Claim claim, Authentication authentication) {

		User customer = (User) authentication.getPrincipal();
		claimService.createDraftClaim(claim, customer);

		return "redirect:/customer/dashboard";
	}

	// Submit existing claim
	@PostMapping("/submit/{id}")
	public String submitClaim(@PathVariable Long id, Authentication authentication) {

		User customer = (User) authentication.getPrincipal();
		claimService.submitClaim(id, customer);

		return "redirect:/customer/dashboard";
	}
}
