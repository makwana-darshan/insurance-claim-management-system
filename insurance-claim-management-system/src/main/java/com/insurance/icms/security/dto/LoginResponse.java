package com.insurance.icms.security.dto;

import java.util.List;

public class LoginResponse {

	private String token;
	private String email;
	private String fullName;
	private List<String> roles;

	public LoginResponse(String token, String email, String fullName, List<String> roles) {
		this.token = token;
		this.email = email;
		this.fullName = fullName;
		this.roles = roles;
	}

	public String getToken() {
		return token;
	}

	public String getEmail() {
		return email;
	}

	public String getFullName() {
		return fullName;
	}

	public List<String> getRoles() {
		return roles;
	}
}