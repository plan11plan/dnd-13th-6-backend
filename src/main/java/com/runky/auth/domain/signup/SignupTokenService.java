package com.runky.auth.domain.signup;

import org.springframework.stereotype.Service;

import com.runky.auth.domain.vo.OAuthUserInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignupTokenService {
	private final SignupTokenRepository signupTokenRepository;
	private final SignupTokenFactory factory;

	public String issue(OAuthUserInfo oAuthUserInfo) {
		SignupToken token = factory.create();
		signupTokenRepository.save(token, oAuthUserInfo);
		return token.idValue();

	}

	public OAuthUserInfo get(String tokenId) {
		return signupTokenRepository.get(tokenId);
	}

	public void delete(String tokenId) {
		signupTokenRepository.delete(tokenId);
	}
}
