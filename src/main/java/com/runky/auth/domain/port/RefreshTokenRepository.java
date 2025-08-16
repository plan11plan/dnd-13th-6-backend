package com.runky.auth.domain.port;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.runky.auth.domain.RefreshToken;

@Repository
public interface RefreshTokenRepository {
	void deleteByMemberId(Long memberId);

	void save(RefreshToken issue);
	
	int deleteExpiredBefore(Instant now);

	Optional<RefreshToken> findByMemberIdAndTokenHash(Long memberId, String tokenHash);
}
