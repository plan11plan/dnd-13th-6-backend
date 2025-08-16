package com.runky.auth.exception.infra;

import com.runky.global.error.GlobalException;

public class TokenHashPepperBlankException extends GlobalException {
	public TokenHashPepperBlankException() {
		super(AuthInfraErrorCode.TOKEN_HASH_PEPPER_BLANK);
	}
}
