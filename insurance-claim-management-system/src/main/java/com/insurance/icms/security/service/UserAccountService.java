package com.insurance.icms.security.service;

import com.insurance.icms.security.dto.RegisterRequest;
import com.insurance.icms.security.entity.Role;
import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.entity.UserStatus;
import com.insurance.icms.security.repository.RoleRepository;
import com.insurance.icms.security.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserAccountService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserAccountService(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

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
		user.setRoles(Set.of(customerRole));

		return userRepository.save(user);
	}

	public void changePassword(User user, String currentPassword, String newPassword) {

		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new RuntimeException("Current password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}
}