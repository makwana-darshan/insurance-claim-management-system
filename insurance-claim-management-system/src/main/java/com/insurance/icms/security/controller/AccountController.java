package com.insurance.icms.security.controller;

import com.insurance.icms.security.dto.ChangePasswordRequest;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.service.CustomUserDetails;
import com.insurance.icms.security.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

	private final UserAccountService userAccountService;

	public AccountController(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	@PostMapping("/change-password")
	public Map<String, String> changePassword(@Valid @RequestBody ChangePasswordRequest request,
			Authentication authentication) {

		User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		userAccountService.changePassword(user, request.getCurrentPassword(), request.getNewPassword());

		return Map.of("message", "Password changed successfully");
	}
}