package com.insurance.icms.security.service;

import com.insurance.icms.security.dto.CreateUserRequest;
import com.insurance.icms.security.dto.RegisterRequest;
import com.insurance.icms.security.entity.EmailVerificationToken;
import com.insurance.icms.security.entity.PasswordResetToken;
import com.insurance.icms.security.entity.Role;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.entity.UserStatus;
import com.insurance.icms.security.repository.EmailVerificationTokenRepository;
import com.insurance.icms.security.repository.PasswordResetTokenRepository;
import com.insurance.icms.security.repository.RoleRepository;
import com.insurance.icms.security.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserAccountService {

	private static final Set<String> ASSIGNABLE_STAFF_ROLES = Set.of("CLAIM_OFFICER", "SURVEYOR", "FINANCE_OFFICER",
			"MEDICAL_REVIEWER");

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final PasswordResetTokenRepository resetTokenRepository;
	private final EmailVerificationTokenRepository verificationTokenRepository;
	private final EmailService emailService;

	public UserAccountService(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder, PasswordResetTokenRepository resetTokenRepository,
			EmailVerificationTokenRepository verificationTokenRepository, EmailService emailService) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.resetTokenRepository = resetTokenRepository;
		this.verificationTokenRepository = verificationTokenRepository;
		this.emailService = emailService;
	}

	@Transactional
	public User registerCustomer(RegisterRequest request) {

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new RuntimeException("An account with this email already exists");
		}

		Role customerRole = roleRepository.findByRoleName("CUSTOMER")
				.orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));

		User user = new User();
		user.setFullName(request.getFullName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setStatus(UserStatus.ACTIVE);
		user.setEmailVerified(false);
		user.setRoles(Set.of(customerRole));

		User savedUser = userRepository.save(user);

		sendVerificationEmail(savedUser);

		return savedUser;
	}

	private void sendVerificationEmail(User user) {

		String token = UUID.randomUUID().toString();

		EmailVerificationToken verificationToken = new EmailVerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

		verificationTokenRepository.save(verificationToken);

		emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), token);
	}

	@Transactional
	public void verifyEmail(String token) {

		EmailVerificationToken verificationToken = verificationTokenRepository.findByToken(token)
				.orElseThrow(() -> new RuntimeException("Invalid or expired verification link"));

		if (verificationToken.isUsed()) {
			throw new RuntimeException("This verification link has already been used");
		}

		if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("This verification link has expired");
		}

		User user = verificationToken.getUser();
		user.setEmailVerified(true);
		userRepository.save(user);

		verificationToken.setUsed(true);
		verificationTokenRepository.save(verificationToken);
	}

	public void changePassword(User user, String currentPassword, String newPassword) {

		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new RuntimeException("Current password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	@Transactional
	public User createStaffUser(CreateUserRequest request) {

		if (!ASSIGNABLE_STAFF_ROLES.contains(request.getRole())) {
			throw new RuntimeException("Role must be one of: " + ASSIGNABLE_STAFF_ROLES);
		}

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new RuntimeException("An account with this email already exists");
		}

		Role role = roleRepository.findByRoleName(request.getRole())
				.orElseThrow(() -> new RuntimeException(request.getRole() + " role not found"));

		User user = new User();
		user.setFullName(request.getFullName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setStatus(UserStatus.ACTIVE);
		user.setEmailVerified(true);
		user.setRoles(Set.of(role));

		return userRepository.save(user);
	}

	@Transactional
	public User updateUserStatus(Long userId, UserStatus newStatus) {

		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRoleName().equals("SUPER_ADMIN"));

		if (isAdmin) {
			throw new RuntimeException("Admin accounts cannot be deactivated");
		}

		user.setStatus(newStatus);

		return userRepository.save(user);
	}

	public User updateProfile(User user, String fullName) {

		user.setFullName(fullName);

		return userRepository.save(user);
	}

	@Transactional
	public void requestPasswordReset(String email) {

		userRepository.findByEmail(email).ifPresent(user -> {

			String token = UUID.randomUUID().toString();

			PasswordResetToken resetToken = new PasswordResetToken();
			resetToken.setToken(token);
			resetToken.setUser(user);
			resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));

			resetTokenRepository.save(resetToken);

			emailService.sendPasswordResetEmail(user.getEmail(), token);
		});
	}

	@Transactional
	public void resetPassword(String token, String newPassword) {

		PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
				.orElseThrow(() -> new RuntimeException("Invalid or expired reset link"));

		if (resetToken.isUsed()) {
			throw new RuntimeException("This reset link has already been used");
		}

		if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("This reset link has expired");
		}

		User user = resetToken.getUser();
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);

		resetToken.setUsed(true);
		resetTokenRepository.save(resetToken);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
}