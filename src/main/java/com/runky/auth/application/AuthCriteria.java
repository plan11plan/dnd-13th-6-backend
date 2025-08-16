package com.runky.auth.application;

public sealed interface AuthCriteria {

	record AdditionalSignUpData(String nickname) implements AuthCriteria {
	}
}
