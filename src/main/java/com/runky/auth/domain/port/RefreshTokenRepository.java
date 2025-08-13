package com.runky.auth.domain.port;

import java.time.Instant;

import org.springframework.stereotype.Repository;

import com.runky.auth.domain.RefreshToken;

@Repository
public interface RefreshTokenRepository {
	void deleteByMemberId(Long memberId);

	void save(RefreshToken issue);

	boolean existsByMemberIdAndTokenHash(Long memberId, String tokenHash);

	int deleteExpiredBefore(Instant now);
}
