package com.runky.auth.infrastructure.token;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.runky.auth.config.props.JwtProperties;
import com.runky.auth.domain.port.TokenIssuer;
import com.runky.auth.domain.vo.Tokens;
import com.runky.auth.exception.infra.JwtAlgorithmUnsupportedException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.RequiredArgsConstructor;

/**
 * AT와 RT를 발급합니다.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenIssuer implements TokenIssuer {

	private final JwtProperties jwtProperties;
	private final JwtSigningKeyProvider jwtSigningKeyProvider;

	@Override
	public Tokens issue(Long memberId, String role) {
		String access = generateAccessToken(memberId, role);
		String refresh = generateRefreshToken(memberId, role);

		return new Tokens(access, refresh);
	}

	private String generateAccessToken(Long memberId, String role) {
		Instant now = Instant.now();
		Instant expiry = now.plus(jwtProperties.access().ttl());

		SecretKey secretKey = jwtSigningKeyProvider.accessKey();
		MacAlgorithm algorithm = resolveAlg(jwtProperties.access().algorithm());

		return Jwts.builder()
			.subject(String.valueOf(memberId))
			.issuedAt(Date.from(now))
			.expiration(Date.from(expiry))
			.claim("role", role)
			.signWith(secretKey, algorithm)
			.compact();
	}

	private String generateRefreshToken(Long memberId, String role) {
		Instant now = Instant.now();
		Instant expiry = now.plus(jwtProperties.refresh().ttl());

		SecretKey secretKey = jwtSigningKeyProvider.refreshKey();
		MacAlgorithm algorithm = resolveAlg(jwtProperties.refresh().algorithm());

		return Jwts.builder()
			.subject(String.valueOf(memberId))
			.issuedAt(Date.from(now))
			.expiration(Date.from(expiry))
			.claim("role", role)
			.signWith(secretKey, algorithm)
			.compact();
	}

	private MacAlgorithm resolveAlg(String name) {
		return switch (name) {
			case "HS256" -> Jwts.SIG.HS256;
			case "HS384" -> Jwts.SIG.HS384;
			case "HS512" -> Jwts.SIG.HS512;
			default -> throw new JwtAlgorithmUnsupportedException();
		};
	}
}
