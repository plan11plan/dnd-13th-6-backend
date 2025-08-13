package com.runky.auth.infrastructure.persistence;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.runky.auth.domain.RefreshToken;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

	@Modifying
	void deleteByMemberId(Long memberId);

	@Modifying
	int deleteByExpiresAtBefore(Instant now);

	boolean existsByMemberIdAndTokenHash(Long memberId, String tokenHash);
}
