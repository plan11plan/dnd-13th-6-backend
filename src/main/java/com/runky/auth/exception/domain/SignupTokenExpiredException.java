package com.runky.auth.exception.domain;

import com.runky.global.error.GlobalException;

public class SignupTokenExpiredException extends GlobalException {
	public SignupTokenExpiredException() {
		super(AuthErrorCode.SIGNUP_TOKEN_EXPIRED);
	}
}
