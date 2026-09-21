package com.insurance.icms.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private final JavaMailSender mailSender;

	@Value("${app.frontend-url}")
	private String frontendUrl;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Async
	public void sendPasswordResetEmail(String toEmail, String token) {

		String resetLink = frontendUrl + "/reset-password?token=" + token;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("ICMS - Password Reset Request");
		message.setText("You requested a password reset.\n\n"
				+ "Click the link below to set a new password (valid for 30 minutes):\n" + resetLink + "\n\n"
				+ "If you didn't request this, you can safely ignore this email.");

		mailSender.send(message);
	}
}