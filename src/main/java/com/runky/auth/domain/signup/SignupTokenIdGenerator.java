package com.runky.auth.domain.signup;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class SignupTokenIdGenerator {

	public String generate() {
		return UUID.randomUUID().toString();
	}
}
