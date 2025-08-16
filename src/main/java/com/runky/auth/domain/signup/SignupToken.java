package com.runky.auth.domain.signup;

import java.time.Instant;

import com.runky.auth.exception.domain.AuthErrorCode;
import com.runky.global.error.GlobalException;

import lombok.Builder;

public class SignupToken {
	private final SignupTokenId id;
	private final Instant expiresAt;

	@Builder
	private SignupToken(final SignupTokenId id, final Instant expiresAt) {
		if (id == null)
			throw new GlobalException(AuthErrorCode.SIGNUP_TOKEN_ID_BLANK);
		if (expiresAt == null)
			throw new GlobalException(AuthErrorCode.SIGNUP_TOKEN_TTL_BLANK);

		this.id = id;
		this.expiresAt = expiresAt;
	}

	public static SignupToken issue(SignupTokenId id, Instant expiresAt) {
		return SignupToken.builder()
			.id(id)
			.expiresAt(expiresAt)
			.build();
	}

	public String idValue() {
		return id.value();
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}
}
