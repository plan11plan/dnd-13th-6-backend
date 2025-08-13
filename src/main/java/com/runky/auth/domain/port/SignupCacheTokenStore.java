package com.runky.auth.domain.port;

import com.runky.auth.domain.vo.OAuthUserInfo;

public interface SignupCacheTokenStore {

	String issueAndSave(OAuthUserInfo info);

	OAuthUserInfo get(String token);

	void delete(String token);
}
