package com.insurance.icms.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		for (GrantedAuthority authority : authentication.getAuthorities()) {
			String role = authority.getAuthority();

			if (role.equals("ROLE_ADMIN")) {
				response.sendRedirect("/admin/dashboard");
				return;
			}

			if (role.equals("ROLE_CLAIM_OFFICER")) {
				response.sendRedirect("/claim/dashboard");
				return;
			}

			if (role.equals("ROLE_SURVEYOR")) {
				response.sendRedirect("/surveyor/dashboard");
				return;
			}

			if (role.equals("ROLE_CUSTOMER")) {
				response.sendRedirect("/customer/dashboard");
				return;
			}
		}

		response.sendRedirect("/login?error");
	}
}
