package com.insurance.icms.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class EmailService {

	private final RestClient restClient;

	@Value("${app.frontend-url}")
	private String frontendUrl;

	@Value("${brevo.api-key}")
	private String brevoApiKey;

	@Value("${brevo.sender-email}")
	private String senderEmail;

	public EmailService() {
		this.restClient = RestClient.builder().baseUrl("https://api.brevo.com/v3").build();
	}

	@Async
	public void sendPasswordResetEmail(String toEmail, String token) {

		String resetLink = frontendUrl + "/reset-password?token=" + token;

		String body = "You requested a password reset.<br><br>"
				+ "Click the link below to set a new password (valid for 30 minutes):<br>" + "<a href=\"" + resetLink
				+ "\">" + resetLink + "</a><br><br>" + "If you didn't request this, you can safely ignore this email.";

		sendViaBrevo(toEmail, "ICMS - Password Reset Request", body);
	}

	@Async
	public void sendVerificationEmail(String toEmail, String fullName, String token) {

		String verifyLink = frontendUrl + "/verify-email?token=" + token;

		String body = "Hi " + fullName + ",<br><br>"
				+ "Welcome to ICMS! Please verify your email address to activate your account:<br>" + "<a href=\""
				+ verifyLink + "\">" + verifyLink + "</a><br><br>" + "This link is valid for 24 hours.<br><br>"
				+ "If you didn't create this account, you can safely ignore this email.";

		sendViaBrevo(toEmail, "ICMS - Verify Your Email", body);
	}

	private void sendViaBrevo(String toEmail, String subject, String htmlBody) {

		Map<String, Object> payload = Map.of("sender", Map.of("name", "ICMS", "email", senderEmail), "to",
				new Object[] { Map.of("email", toEmail) }, "subject", subject, "htmlContent", htmlBody);

		restClient.post().uri("/smtp/email").header("api-key", brevoApiKey)
				.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON)
				.body(payload).retrieve().toBodilessEntity();
	}
}