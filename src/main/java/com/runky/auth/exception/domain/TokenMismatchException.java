package com.runky.auth.exception.domain;

import com.runky.global.error.GlobalException;

public class TokenMismatchException extends GlobalException {
	public TokenMismatchException() {
		super(AuthErrorCode.TOKEN_MISMATCH);
	}
}
