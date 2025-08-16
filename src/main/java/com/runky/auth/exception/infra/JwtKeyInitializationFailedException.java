package com.runky.auth.exception.infra;

import com.runky.global.error.GlobalException;

public class JwtKeyInitializationFailedException extends GlobalException {
	public JwtKeyInitializationFailedException() {
		super(AuthInfraErrorCode.JWT_KEY_INIT_FAILED);
	}
}
