package com.insurance.icms;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Demo {

	public static void main(String[] args) {
		 BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	        System.out.println(encoder.encode("customer123"));
	}
}
