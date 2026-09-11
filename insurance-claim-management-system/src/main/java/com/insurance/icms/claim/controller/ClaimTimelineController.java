package com.insurance.icms.claim.controller;

import com.insurance.icms.claim.dto.ClaimAuditLogDto;
import com.insurance.icms.claim.dto.ClaimResponseDto;
import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.service.ClaimAuditService;
import com.insurance.icms.claim.service.ClaimService;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/claims")
public class ClaimTimelineController {

	private final ClaimService claimService;
	private final ClaimAuditService auditService;

	public ClaimTimelineController(ClaimService claimService, ClaimAuditService auditService) {
		this.claimService = claimService;
		this.auditService = auditService;
	}

	@GetMapping("/{id}/timeline")
	public Map<String, Object> timeline(@PathVariable Long id, Authentication authentication) {

		Claim claim = claimService.getClaimById(id);

		User currentUser = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		boolean isOwner = claim.getCustomer() != null && claim.getCustomer().getId().equals(currentUser.getId());

		boolean isStaff = currentUser.getRoles().stream().anyMatch(role -> !role.getRoleName().equals("CUSTOMER"));

		if (!isOwner && !isStaff) {
			throw new RuntimeException("Not authorized to view this claim's timeline");
		}

		List<ClaimAuditLogDto> timeline = auditService.getTimeline(claim).stream().map(ClaimAuditLogDto::fromEntity)
				.collect(Collectors.toList());

		Map<String, Object> response = new HashMap<>();
		response.put("claim", ClaimResponseDto.fromEntity(claim));
		response.put("timeline", timeline);

		return response;
	}
}