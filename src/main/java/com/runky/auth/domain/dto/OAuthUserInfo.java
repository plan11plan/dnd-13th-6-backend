package com.runky.auth.domain.dto;

public record OAuthUserInfo(
	String provider,
	String providerId
) {
}
