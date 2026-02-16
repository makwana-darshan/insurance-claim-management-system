package com.insurance.icms.claim.controller;

import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.service.ClaimAuditService;
import com.insurance.icms.claim.service.ClaimService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/claims")
public class ClaimTimelineController {

	private final ClaimService claimService;
	private final ClaimAuditService auditService;

	public ClaimTimelineController(ClaimService claimService, ClaimAuditService auditService) {
		this.claimService = claimService;
		this.auditService = auditService;
	}

	@GetMapping("/{id}/timeline")
	public String timeline(@PathVariable Long id, Model model) {

		Claim claim = claimService.getClaimById(id);

		model.addAttribute("claim", claim);
		model.addAttribute("timeline", auditService.getTimeline(claim));

		return "claim/timeline";
	}
}
