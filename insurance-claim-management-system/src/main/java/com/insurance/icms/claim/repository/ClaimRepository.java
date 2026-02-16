package com.insurance.icms.claim.repository;

import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.enums.ClaimStatus;
import com.insurance.icms.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

	List<Claim> findByCustomer(User customer);

	List<Claim> findByStatus(ClaimStatus status);

	List<Claim> findBySurveyor(User surveyor);

}
