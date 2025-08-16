package com.runky.auth.domain.signup;

import com.runky.auth.exception.infra.TokenBlankException;

public record SignupTokenId(String value) {
	public SignupTokenId {
		if (value == null || value.isBlank()) {
			throw new TokenBlankException();
		}
	}
}
