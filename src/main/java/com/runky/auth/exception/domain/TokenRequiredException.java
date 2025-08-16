package com.runky.auth.exception.domain;

import com.runky.global.error.GlobalException;

public class TokenRequiredException extends GlobalException {
	public TokenRequiredException() {
		super(AuthErrorCode.TOKEN_REQUIRED);
	}
}
