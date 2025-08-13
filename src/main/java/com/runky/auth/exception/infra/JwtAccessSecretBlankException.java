package com.runky.auth.exception.infra;

import com.runky.global.error.GlobalException;

public class JwtAccessSecretBlankException extends GlobalException {
	public JwtAccessSecretBlankException() {
		super(AuthInfraErrorCode.JWT_ACCESS_SECRET_BLANK);
	}
}
