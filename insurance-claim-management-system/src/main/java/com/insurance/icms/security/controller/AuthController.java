package com.insurance.icms.security.controller;

import com.insurance.icms.security.dto.LoginRequest;
import com.insurance.icms.security.dto.LoginResponse;
import com.insurance.icms.security.dto.RegisterRequest;
import com.insurance.icms.security.entity.User;
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
	public LoginResponse register(@Valid @RequestBody RegisterRequest request) {

		User user = userAccountService.registerCustomer(request);

		CustomUserDetails userDetails = new CustomUserDetails(user);
		String token = jwtService.generateToken(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		return new LoginResponse(token, user.getEmail(), user.getFullName(), roles);
	}
}