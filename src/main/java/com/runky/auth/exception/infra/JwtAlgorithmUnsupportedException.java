package com.runky.auth.exception.infra;

import com.runky.global.error.GlobalException;

public class JwtAlgorithmUnsupportedException extends GlobalException {
	public JwtAlgorithmUnsupportedException() {
		super(AuthInfraErrorCode.JWT_ALGORITHM_UNSUPPORTED);
	}
}
