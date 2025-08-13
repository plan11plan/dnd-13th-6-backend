package com.runky.auth.infrastructure.token;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.runky.auth.config.props.SignupTokenProperties;
import com.runky.auth.domain.dto.OAuthUserInfo;
import com.runky.auth.domain.port.SignupCacheTokenStore;
import com.runky.auth.exception.domain.SignupTokenExpiredException;

import lombok.RequiredArgsConstructor;

/**
 * 추가 유저 정보를 받기 위한 임시토큰을 발행 및 관리합니다.
 */
@Component
@RequiredArgsConstructor
public class SignupCacheTokenMemoryStore implements SignupCacheTokenStore {

	private final SignupTokenProperties props;
	private final Map<String, TokenData> store = new ConcurrentHashMap<>();

	@Override
	public String issueAndSave(OAuthUserInfo info) {
		String token = UUID.randomUUID().toString();
		long expireAtMillis = Instant.now().plus(props.ttl()).toEpochMilli();
		store.put(token, new TokenData(info, expireAtMillis));
		return token;
	}

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
