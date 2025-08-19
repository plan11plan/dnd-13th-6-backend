package com.runky.auth.exception.domain;

import com.runky.global.error.GlobalException;

public class ExpiredTokenException extends GlobalException {
	public ExpiredTokenException() {
		super(AuthErrorCode.EXPIRED_TOKEN);
	}
}
