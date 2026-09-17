package com.insurance.icms.admin.controller;

import com.insurance.icms.security.dto.CreateUserRequest;
import com.insurance.icms.security.dto.UserResponse;
import com.insurance.icms.security.entity.User;
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
}