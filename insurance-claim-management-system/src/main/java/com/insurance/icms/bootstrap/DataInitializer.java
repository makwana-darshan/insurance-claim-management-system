package com.insurance.icms.bootstrap;

import com.insurance.icms.security.entity.*;
import com.insurance.icms.security.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

	@Bean
	CommandLineRunner initData(RoleRepository roleRepo, PermissionRepository permRepo, UserRepository userRepo,
			PasswordEncoder encoder) {
		return args -> {

			/* ---------- PERMISSIONS ---------- */
			Permission claimView = createPermission("CLAIM_VIEW", permRepo);
			Permission claimApprove = createPermission("CLAIM_APPROVE", permRepo);
			Permission claimReject = createPermission("CLAIM_REJECT", permRepo);
			Permission claimReview = createPermission("CLAIM_REVIEW", permRepo);
			Permission paymentProcess = createPermission("PAYMENT_PROCESS", permRepo);

			/* ---------- ROLES ---------- */
			Role superAdmin = createRole("SUPER_ADMIN",
					Set.of(claimView, claimApprove, claimReject, claimReview, paymentProcess), roleRepo);

			Role claimOfficer = createRole("CLAIM_OFFICER", Set.of(claimView, claimApprove, claimReject), roleRepo);

			Role medicalReviewer = createRole("MEDICAL_REVIEWER", Set.of(claimView, claimReview), roleRepo);

			Role financeOfficer = createRole("FINANCE_OFFICER", Set.of(claimView, paymentProcess), roleRepo);

			/* ---------- SUPER ADMIN USER ---------- */
			if (userRepo.findByEmail("admin@icms.com").isEmpty()) {

				User admin = new User();
				admin.setFullName("System Admin");
				admin.setEmail("admin@icms.com");
				admin.setPassword(encoder.encode("admin123"));
				admin.setStatus(UserStatus.ACTIVE);
				admin.setRoles(Set.of(superAdmin));

				userRepo.save(admin);
			}
		};
	}

	/* ---------- Helper Methods ---------- */

	private Permission createPermission(String key, PermissionRepository repo) {
		return repo.findByPermissionKey(key).orElseGet(() -> repo.save(new Permission(key)));
	}

	private Role createRole(String roleName, Set<Permission> permissions, RoleRepository repo) {
		return repo.findByRoleName(roleName).orElseGet(() -> {
			Role role = new Role();
			role.setRoleName(roleName);
			role.setPermissions(permissions);
			return repo.save(role);
		});
	}
}
