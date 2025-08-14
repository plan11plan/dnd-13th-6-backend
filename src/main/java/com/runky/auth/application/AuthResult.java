package com.runky.auth.application;

public sealed interface AuthResult {
	enum AuthStatus {NEW_USER, EXISTING_USER}

	record SigninComplete(String accessToken, String refreshToken) implements AuthResult {
	}

	record OAuthLogin(boolean isNewUser, String signupToken, String accessToken, String refreshToken,
					  AuthStatus authStatus
	) implements AuthResult {
		public static OAuthLogin newUser(String signupToken) {
			return new OAuthLogin(true, signupToken, null, null, AuthStatus.NEW_USER);
		}

		public static OAuthLogin existing(String accessToken, String refreshToken) {
			return new OAuthLogin(false, null, accessToken, refreshToken, AuthStatus.EXISTING_USER);
		}
	}

	record rotatedToken(String accessToken, String refreshToken) {
	}

}
