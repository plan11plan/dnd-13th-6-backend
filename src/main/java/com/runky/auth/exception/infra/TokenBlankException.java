package com.runky.auth.exception.infra;

import com.runky.global.error.GlobalException;

public class TokenBlankException extends GlobalException {
	public TokenBlankException() {
		super(AuthInfraErrorCode.TOKEN_BLANK);
	}
}
