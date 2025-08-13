package com.runky.auth.domain;

public sealed interface AuthCommand {

	record OauthUserInfo(String provider, String providerId) implements AuthCommand {
	}

	record AdditionalSignUpData(String nickname) implements AuthCommand {
	}
}
