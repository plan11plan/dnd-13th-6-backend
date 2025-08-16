package com.runky.auth.exception.infra;

import com.runky.global.error.GlobalException;

public class JwtSecretInvalidLengthException extends GlobalException {
	public JwtSecretInvalidLengthException() {
		super(AuthInfraErrorCode.JWT_SECRET_INVALID_LENGTH);
	}
}
