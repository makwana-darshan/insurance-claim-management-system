package com.insurance.icms.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		String redirectUrl = "/dashboard"; // fallback

		for (GrantedAuthority authority : authentication.getAuthorities()) {

			String role = authority.getAuthority();

			if (role.equals("ROLE_SUPER_ADMIN")) {
				redirectUrl = "/admin/dashboard";
				break;

			} else if (role.equals("ROLE_CLAIM_OFFICER")) {
				redirectUrl = "/claim/dashboard";
				break;

			} else if (role.equals("ROLE_FINANCE_OFFICER")) {
				redirectUrl = "/finance/dashboard";
				break;

			} else if (role.equals("ROLE_MEDICAL_REVIEWER")) {
				redirectUrl = "/review/dashboard";
				break;

			} else if (role.equals("ROLE_CUSTOMER")) {
				redirectUrl = "/customer/dashboard";
				break;
			}
		}

		response.sendRedirect(redirectUrl);
	}
}
