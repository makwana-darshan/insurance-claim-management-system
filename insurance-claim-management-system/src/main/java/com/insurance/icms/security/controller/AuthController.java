package com.insurance.icms.security.controller;

import com.insurance.icms.security.dto.*;
import com.insurance.icms.security.jwt.JwtService;
import com.insurance.icms.security.service.CustomUserDetails;
import com.insurance.icms.security.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserAccountService userAccountService;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
			UserAccountService userAccountService) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userAccountService = userAccountService;
	}

	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest request) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		String token = jwtService.generateToken(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		return new LoginResponse(token, userDetails.getUsername(), userDetails.getUser().getFullName(), roles);
	}

	@PostMapping("/register")
	public Map<String, String> register(@Valid @RequestBody RegisterRequest request) {

		userAccountService.registerCustomer(request);

		return Map.of("message", "Account created. Please check your email to verify your account before logging in.");
	}

	@PostMapping("/verify-email")
	public Map<String, String> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {

		userAccountService.verifyEmail(request.getToken());

		return Map.of("message", "Email verified successfully. You can now log in.");
	}

	@PostMapping("/forgot-password")
	public Map<String, String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

		userAccountService.requestPasswordReset(request.getEmail());

		return Map.of("message", "If an account with that email exists, a reset link has been sent.");
	}

	@PostMapping("/reset-password")
	public Map<String, String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

		userAccountService.resetPassword(request.getToken(), request.getNewPassword());

		return Map.of("message", "Password reset successfully. You can now log in.");
	}
}