package com.runky.auth.api;

public sealed interface AuthResponse {

	/**
	 * 신규 가입 유저 응답
	 */
	record NewUser(String nextAction) implements AuthResponse {
		public NewUser() {
			this("COMPLETE_SIGNUP");
		}
	}

	/**
	 * 기존 가입 유저 응답
	 */
	record ExistingUser(String nextAction) implements AuthResponse {
		public ExistingUser() {
			this("LOGIN_DONE");
		}
	}
}
