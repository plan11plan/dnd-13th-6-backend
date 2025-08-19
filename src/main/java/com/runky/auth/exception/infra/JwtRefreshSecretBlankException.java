package com.runky.auth.exception.infra;

import com.runky.global.error.GlobalException;

public class JwtRefreshSecretBlankException extends GlobalException {
	public JwtRefreshSecretBlankException() {
		super(AuthInfraErrorCode.JWT_REFRESH_SECRET_BLANK);
	}
}
