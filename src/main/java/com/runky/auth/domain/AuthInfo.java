package com.runky.auth.domain;

public sealed interface AuthInfo {
	record TokenPair(String accessToken, String refreshToken) implements AuthInfo {
	}

}
