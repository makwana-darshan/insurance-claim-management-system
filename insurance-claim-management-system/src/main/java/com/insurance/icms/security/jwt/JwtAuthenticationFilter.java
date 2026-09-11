package com.insurance.icms.security.jwt;

import com.insurance.icms.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;

	@Autowired
	public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");

		// No header, or not a Bearer token -> skip, let request continue
		// unauthenticated
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String token = authHeader.substring(7); // strip "Bearer "
		final String username;

		try {
			username = jwtService.extractUsername(token);
			System.out.println("DEBUG: extracted username = " + username);
		} catch (Exception e) {
			System.out.println(
					"DEBUG: token extraction failed -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
			filterChain.doFilter(request, response);
			return;
		}

		// Only authenticate if not already authenticated in this request
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			System.out.println("DEBUG: loaded userDetails = " + userDetails.getUsername() + ", authorities = "
					+ userDetails.getAuthorities());

			boolean valid = jwtService.isTokenValid(token, userDetails);
			System.out.println("DEBUG: token valid = " + valid);

			if (valid) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
				System.out.println("DEBUG: authentication set in SecurityContext");
			}
		}

		filterChain.doFilter(request, response);
	}
}