package com.insurance.icms.controller;

import com.insurance.icms.claim.service.ClaimService;
import com.insurance.icms.security.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerDashboardController {

	private final ClaimService claimService;

	public CustomerDashboardController(ClaimService claimService) {
		this.claimService = claimService;
	}

	@GetMapping("/customer/dashboard")
	public String dashboard(Model model, Authentication authentication) {

		User user = (User) authentication.getPrincipal();

		model.addAttribute("claims", claimService.getClaimsByCustomer(user));

		return "customer/dashboard";
	}

}
