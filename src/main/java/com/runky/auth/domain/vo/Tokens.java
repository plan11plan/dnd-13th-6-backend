package com.runky.auth.domain.vo;

import com.runky.auth.exception.infra.TokenBlankException;

public record Tokens(String accessToken, String refreshToken) {
	public Tokens {
		if (accessToken == null || accessToken.isBlank()) {
			throw new TokenBlankException();
		}
		if (refreshToken == null || refreshToken.isBlank()) {
			throw new TokenBlankException();
		}
	}
}
