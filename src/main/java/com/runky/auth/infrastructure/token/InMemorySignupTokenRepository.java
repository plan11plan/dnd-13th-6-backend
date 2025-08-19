package com.runky.auth.infrastructure.token;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.runky.auth.domain.signup.SignupToken;
import com.runky.auth.domain.signup.SignupTokenRepository;
import com.runky.auth.domain.vo.OAuthUserInfo;
import com.runky.auth.exception.domain.SignupTokenExpiredException;

import lombok.RequiredArgsConstructor;

/**
 * 추가 유저 정보를 받기 위한 임시토큰을 발행 및 관리합니다.
 */
@Component
@RequiredArgsConstructor
public class InMemorySignupTokenRepository implements SignupTokenRepository {

	private final Map<String, TokenData> store = new ConcurrentHashMap<>();

	@Override
	public String save(final SignupToken signupToken, final OAuthUserInfo info) {
		String id = signupToken.idValue();
		store.put(id, new TokenData(info, signupToken.getExpiresAt().toEpochMilli()));
		return id;
	}

	//TODO: get + remove 분리 리팩토링 필요
	@Override
	public OAuthUserInfo get(String token) {
		TokenData data = store.get(token);
		if (data == null || data.expired()) {
			store.remove(token);
			throw new SignupTokenExpiredException();
		}
		return data.user;
	}

	@Override
	public void delete(String token) {
		store.remove(token);
	}

	private record TokenData(OAuthUserInfo user, long expireAt) {
		boolean expired() {
			return Instant.now().toEpochMilli() > expireAt;
		}
	}
}
