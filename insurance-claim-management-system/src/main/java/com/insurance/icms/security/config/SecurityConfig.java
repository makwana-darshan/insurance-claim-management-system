package com.insurance.icms.security.config;

import com.insurance.icms.security.handler.CustomAuthenticationSuccessHandler;
import com.insurance.icms.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomUserDetailsService userDetailsService;
	private final CustomAuthenticationSuccessHandler successHandler;

	public SecurityConfig(CustomUserDetailsService userDetailsService,
			CustomAuthenticationSuccessHandler successHandler) {
		this.userDetailsService = userDetailsService;
		this.successHandler = successHandler;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())

				// 🔹 UserDetailsService
				.userDetailsService(userDetailsService)

				// 🔹 AUTHORIZATION RULES 
				.authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/css/**", "/js/**").permitAll()

						.requestMatchers("/admin/**").hasRole("SUPER_ADMIN").requestMatchers("/customer/**")
						.hasRole("CUSTOMER").requestMatchers("/claim/**").hasRole("CLAIM_OFFICER")
						.requestMatchers("/surveyor/**").hasRole("SURVEYOR")

						.anyRequest().authenticated())

				// 🔹 LOGIN
				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").successHandler(successHandler)
						.failureUrl("/login?error").permitAll())

				// 🔹 LOGOUT
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout"));

		return http.build();
	}
}
