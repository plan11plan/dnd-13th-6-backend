package com.runky.auth.exception.infra;

import com.runky.global.error.GlobalException;

public class TokenHashFailureException extends GlobalException {
	public TokenHashFailureException() {
		super(AuthInfraErrorCode.TOKEN_HASH_FAILURE);
	}
}
