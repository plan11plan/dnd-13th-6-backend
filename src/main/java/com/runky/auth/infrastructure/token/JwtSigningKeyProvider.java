package com.runky.auth.infrastructure.token;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.runky.auth.config.props.JwtProperties;
import com.runky.auth.exception.infra.JwtAccessSecretBlankException;
import com.runky.auth.exception.infra.JwtKeyInitializationFailedException;
import com.runky.auth.exception.infra.JwtRefreshSecretBlankException;
import com.runky.auth.exception.infra.JwtSecretInvalidLengthException;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtSigningKeyProvider {

	private final JwtProperties props;
	private SecretKey accessKey;
	private SecretKey refreshKey;

	@PostConstruct
	void init() {
		try {
			String access = props.access().secretKey();
			String refresh = props.refresh().secretKey();

			if (access == null || access.isBlank())
				throw new JwtAccessSecretBlankException();
			if (refresh == null || refresh.isBlank())
				throw new JwtRefreshSecretBlankException();

			this.accessKey = Keys.hmacShaKeyFor(access.getBytes(StandardCharsets.UTF_8));
			this.refreshKey = Keys.hmacShaKeyFor(refresh.getBytes(StandardCharsets.UTF_8));
		} catch (IllegalArgumentException e) {
			throw new JwtSecretInvalidLengthException();
		} catch (Exception e) {
			throw new JwtKeyInitializationFailedException();
		}
	}

	public SecretKey accessKey() {
		return accessKey;
	}

	public SecretKey refreshKey() {
		return refreshKey;
	}
}
