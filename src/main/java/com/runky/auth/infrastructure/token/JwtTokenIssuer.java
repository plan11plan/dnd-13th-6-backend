package com.runky.auth.infrastructure.token;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.runky.auth.config.props.JwtProperties;
import com.runky.auth.domain.port.TokenIssuer;
import com.runky.auth.domain.vo.IssuedTokens;
import com.runky.auth.domain.vo.RawAccessToken;
import com.runky.auth.domain.vo.RawRefreshToken;
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
	public IssuedTokens issue(Long memberId, String role) {
		RawAccessToken access = generateAccessToken(memberId, role);
		RawRefreshToken refresh = generateRefreshToken(memberId, role);

		return new IssuedTokens(access, refresh);
	}

	private RawAccessToken generateAccessToken(Long memberId, String role) {
		Instant now = Instant.now();
		Instant expiry = now.plus(jwtProperties.access().ttl());

		SecretKey secretKey = jwtSigningKeyProvider.accessKey();
		MacAlgorithm algorithm = resolveAlg(jwtProperties.access().algorithm());

		String token = Jwts.builder()
			.id(UUID.randomUUID().toString())
			.subject(String.valueOf(memberId))
			.issuedAt(Date.from(now))
			.expiration(Date.from(expiry))
			.claim("role", role)
			.signWith(secretKey, algorithm)
			.compact();
		return new RawAccessToken(token, now, expiry);
	}

	private RawRefreshToken generateRefreshToken(Long memberId, String role) {
		Instant now = Instant.now();
		Instant expiry = now.plus(jwtProperties.refresh().ttl());

		SecretKey secretKey = jwtSigningKeyProvider.refreshKey();
		MacAlgorithm algorithm = resolveAlg(jwtProperties.refresh().algorithm());

		String token = Jwts.builder()
			.id(UUID.randomUUID().toString())
			.subject(String.valueOf(memberId))
			.issuedAt(Date.from(now))
			.expiration(Date.from(expiry))
			.claim("role", role)
			.signWith(secretKey, algorithm)
			.compact();
		return new RawRefreshToken(token, now, expiry);
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
