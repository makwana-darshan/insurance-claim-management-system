package com.insurance.icms.security.dto;

import com.insurance.icms.security.entity.Role;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.entity.UserStatus;

import java.util.List;
import java.util.stream.Collectors;

public class UserResponse {

	private Long id;
	private String fullName;
	private String email;
	private UserStatus status;
	private List<String> roles;

	public static UserResponse fromEntity(User user) {
		UserResponse dto = new UserResponse();
		dto.id = user.getId();
		dto.fullName = user.getFullName();
		dto.email = user.getEmail();
		dto.status = user.getStatus();
		dto.roles = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList());
		return dto;
	}

	public Long getId() {
		return id;
	}

	public String getFullName() {
		return fullName;
	}

	public String getEmail() {
		return email;
	}

	public UserStatus getStatus() {
		return status;
	}

	public List<String> getRoles() {
		return roles;
	}
}