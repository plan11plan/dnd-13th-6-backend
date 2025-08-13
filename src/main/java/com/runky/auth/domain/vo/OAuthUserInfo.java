package com.runky.auth.domain.vo;

public record OAuthUserInfo(
	String provider,
	String providerId
) {
}
