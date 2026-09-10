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
			Permission claimCreate = createPermission("CLAIM_CREATE", permRepo);
			Permission claimInspect = createPermission("CLAIM_INSPECT", permRepo);

			/* ---------- ROLES ---------- */
			Role superAdmin = createRole("SUPER_ADMIN",
					Set.of(claimView, claimApprove, claimReject, claimReview, paymentProcess), roleRepo);

			Role claimOfficer = createRole("CLAIM_OFFICER", Set.of(claimView, claimApprove, claimReject), roleRepo);

			Role medicalReviewer = createRole("MEDICAL_REVIEWER", Set.of(claimView, claimReview), roleRepo);

			Role financeOfficer = createRole("FINANCE_OFFICER", Set.of(claimView, paymentProcess), roleRepo);

			Role customer = createRole("CUSTOMER", Set.of(claimCreate, claimView), roleRepo);

			Role surveyor = createRole("SURVEYOR", Set.of(claimView, claimInspect), roleRepo);

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
			
			/* ---------- CUSTOMER TEST USER ---------- */
			if (userRepo.findByEmail("customer@icms.com").isEmpty()) {
				User customerUser = new User();
				customerUser.setFullName("Test Customer");
				customerUser.setEmail("customer@icms.com");
				customerUser.setPassword(encoder.encode("customer123"));
				customerUser.setStatus(UserStatus.ACTIVE);
				customerUser.setRoles(Set.of(customer));
				userRepo.save(customerUser);
			}

			/* ---------- SURVEYOR TEST USER ---------- */
			if (userRepo.findByEmail("surveyor@icms.com").isEmpty()) {
				User surveyorUser = new User();
				surveyorUser.setFullName("Test Surveyor");
				surveyorUser.setEmail("surveyor@icms.com");
				surveyorUser.setPassword(encoder.encode("surveyor123"));
				surveyorUser.setStatus(UserStatus.ACTIVE);
				surveyorUser.setRoles(Set.of(surveyor));
				userRepo.save(surveyorUser);
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
