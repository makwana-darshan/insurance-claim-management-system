package com.insurance.icms.security.repository;

import com.insurance.icms.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	@Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :role")
	List<User> findByRole(@Param("role") String role);

	List<User> findByRoles_RoleName(String roleName);

}
