package com.runky.auth.infrastructure.persistence;

import java.time.Instant;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.runky.auth.domain.RefreshToken;
import com.runky.auth.domain.port.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
	private final RefreshTokenJpaRepository jpa;

	@Override
	public void deleteByMemberId(Long memberId) {
		jpa.deleteByMemberId(memberId);
	}

	@Override
	public void save(RefreshToken token) {
		jpa.save(token);
	}

	@Override
	public boolean existsByMemberIdAndTokenHash(Long memberId, String tokenHash) {
		return jpa.existsByMemberIdAndTokenHash(memberId, tokenHash);
	}

	@Override
	@Transactional
	public int deleteExpiredBefore(Instant now) {
		return jpa.deleteByExpiresAtBefore(now);
	}
}
