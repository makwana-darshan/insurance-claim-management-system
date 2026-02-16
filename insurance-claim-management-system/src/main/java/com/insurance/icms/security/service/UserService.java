package com.insurance.icms.security.service;

import com.insurance.icms.security.entity.User;
import com.insurance.icms.security.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> getAllSurveyors() {
		return userRepository.findByRoles_RoleName("SURVEYOR");
	}

	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
	}
}
