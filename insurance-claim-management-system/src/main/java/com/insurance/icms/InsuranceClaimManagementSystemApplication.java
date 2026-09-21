package com.insurance.icms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InsuranceClaimManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsuranceClaimManagementSystemApplication.class, args);
	}

}
