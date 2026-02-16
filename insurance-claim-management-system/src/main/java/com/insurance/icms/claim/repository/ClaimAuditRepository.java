package com.insurance.icms.claim.repository;

import com.insurance.icms.claim.entity.Claim;
import com.insurance.icms.claim.entity.ClaimAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimAuditRepository extends JpaRepository<ClaimAuditLog, Long> {

	List<ClaimAuditLog> findByClaimOrderByActionAtAsc(Claim claim);
}
