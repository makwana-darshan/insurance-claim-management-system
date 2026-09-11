package com.insurance.icms.security.dto;

import com.insurance.icms.security.entity.User;

public class UserSummaryDto {

	private Long id;
	private String fullName;
	private String email;

	public static UserSummaryDto fromEntity(User user) {
		UserSummaryDto dto = new UserSummaryDto();
		dto.id = user.getId();
		dto.fullName = user.getFullName();
		dto.email = user.getEmail();
		return dto;
	}

	public Long getId() {
		return id;
	}

	public String getFullName() {
		return fullName;
	}

	public String getEmail() {
		return email;
	}
}