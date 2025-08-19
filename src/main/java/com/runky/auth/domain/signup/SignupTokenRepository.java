package com.runky.auth.domain.signup;

import com.runky.auth.domain.vo.OAuthUserInfo;

public interface SignupTokenRepository {
	String save(SignupToken signupToken, OAuthUserInfo info);

	OAuthUserInfo get(String token);

	void delete(String token);

}
