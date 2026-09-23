package com.insurance.icms.security.controller;

import com.insurance.icms.security.dto.ChangePasswordRequest;
import com.insurance.icms.security.dto.UpdateProfileRequest;
import com.insurance.icms.security.dto.UserResponse;
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

	@GetMapping("/me")
	public UserResponse getProfile(Authentication authentication) {

		User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		return UserResponse.fromEntity(user);
	}

	@PutMapping("/me")
	public UserResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request, Authentication authentication) {

		User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		User updated = userAccountService.updateProfile(user, request.getFullName());

		return UserResponse.fromEntity(updated);
	}

	@PostMapping("/change-password")
	public Map<String, String> changePassword(@Valid @RequestBody ChangePasswordRequest request,
			Authentication authentication) {

		User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

		userAccountService.changePassword(user, request.getCurrentPassword(), request.getNewPassword());

		return Map.of("message", "Password changed successfully");
	}
}