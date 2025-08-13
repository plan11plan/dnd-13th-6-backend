package com.runky.auth.infrastructure.erxternal.dto;

public record OAuthUserInfo(
	String provider,
	String providerId
) {
}
