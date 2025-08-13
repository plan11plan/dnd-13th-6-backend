package com.runky.auth.exception.domain;

import com.runky.global.error.GlobalException;

public class InvalidTokenException extends GlobalException {
	public InvalidTokenException() {
		super(AuthErrorCode.INVALID_TOKEN);
	}
}
