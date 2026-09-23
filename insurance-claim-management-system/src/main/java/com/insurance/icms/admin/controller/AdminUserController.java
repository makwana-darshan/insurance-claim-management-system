package com.insurance.icms.admin.controller;

import com.insurance.icms.security.dto.CreateUserRequest;
import com.insurance.icms.security.dto.UpdateUserStatusRequest;
import com.insurance.icms.security.dto.UserResponse;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.entity.UserStatus;
import com.insurance.icms.security.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

	private final UserAccountService userAccountService;

	public AdminUserController(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	@GetMapping
	public List<UserResponse> getAllUsers() {
		return userAccountService.getAllUsers().stream().map(UserResponse::fromEntity).collect(Collectors.toList());
	}

	@PostMapping
	public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
		User user = userAccountService.createStaffUser(request);
		return UserResponse.fromEntity(user);
	}

	@PatchMapping("/{id}/status")
	public UserResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateUserStatusRequest request) {

		UserStatus newStatus;
		try {
			newStatus = UserStatus.valueOf(request.getStatus());
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Invalid status. Must be ACTIVE, INACTIVE, or BLOCKED.");
		}

		User user = userAccountService.updateUserStatus(id, newStatus);
		return UserResponse.fromEntity(user);
	}
}