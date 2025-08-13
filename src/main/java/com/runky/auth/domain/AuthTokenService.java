package com.runky.auth.domain;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.runky.auth.domain.port.RefreshTokenRepository;
import com.runky.auth.domain.port.TokenDecoder;
import com.runky.auth.domain.port.TokenHasher;
import com.runky.auth.domain.port.TokenIssuer;
import com.runky.auth.domain.vo.DecodedToken;
import com.runky.auth.domain.vo.Tokens;
import com.runky.auth.exception.domain.ExpiredTokenException;
import com.runky.auth.exception.domain.TokenMismatchException;
import com.runky.auth.exception.domain.TokenRequiredException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

	private final TokenIssuer tokenIssuer;
	private final TokenDecoder tokenDecoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final TokenHasher refreshTokenHasher;

	@Transactional
	public AuthInfo.TokenPair issue(Long memberId, String role) {
		Tokens tokens = tokenIssuer.issue(memberId, role);

		DecodedToken decoded = tokenDecoder.decodeRefresh(tokens.refreshToken());

		String hash = refreshTokenHasher.hash(tokens.refreshToken());
		refreshTokenRepository.deleteByMemberId(memberId);
		refreshTokenRepository.save(RefreshToken.issue(memberId, hash, decoded.expiresAt()));

		return new AuthInfo.TokenPair(tokens.accessToken(), tokens.refreshToken());
	}

	/**
	 * RT로 AT/RT 재발급
	 */
	@Transactional
	public AuthInfo.TokenPair rotate(String refreshToken) {
		if (refreshToken == null || refreshToken.isBlank()) {
			throw new TokenRequiredException();
		}

		DecodedToken decoded = tokenDecoder.decodeRefresh(refreshToken);

		if (decoded.expiresAt().isBefore(Instant.now())) {
			throw new ExpiredTokenException();
		}

		String providedHash = refreshTokenHasher.hash(refreshToken);
		boolean valid = refreshTokenRepository.existsByMemberIdAndTokenHash(decoded.memberId(), providedHash);
		if (!valid) {
			throw new TokenMismatchException();
		}

		Tokens tokens = tokenIssuer.issue(decoded.memberId(), decoded.role());
		DecodedToken newDecoded = tokenDecoder.decodeRefresh(tokens.refreshToken());
		String newHash = refreshTokenHasher.hash(tokens.refreshToken());

		refreshTokenRepository.deleteByMemberId(decoded.memberId());
		refreshTokenRepository.save(RefreshToken.issue(decoded.memberId(), newHash, newDecoded.expiresAt()));

		return new AuthInfo.TokenPair(tokens.accessToken(), tokens.refreshToken());
	}

	@Transactional
	public void logoutByMemberId(Long memberId) {
		refreshTokenRepository.deleteByMemberId(memberId);
	}
}
