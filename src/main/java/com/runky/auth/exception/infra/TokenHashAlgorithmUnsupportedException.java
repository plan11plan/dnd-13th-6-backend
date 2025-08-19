package com.runky.auth.exception.infra;

import com.runky.global.error.GlobalException;

public class TokenHashAlgorithmUnsupportedException extends GlobalException {
	public TokenHashAlgorithmUnsupportedException() {
		super(AuthInfraErrorCode.TOKEN_HASH_ALGORITHM_UNSUPPORTED);
	}
}
