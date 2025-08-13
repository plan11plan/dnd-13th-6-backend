package com.runky.auth.domain;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.runky.auth.exception.domain.InvalidTokenException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
	name = "refresh_token",
	indexes = {
		@Index(name = "ux_refresh_token_member_id", columnList = "member_id", unique = true),
		@Index(name = "ix_refresh_token_expires_at", columnList = "expires_at")
	}
)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "token_hash", nullable = false, length = 64)
	private String tokenHash;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Builder
	private RefreshToken(Long memberId, String tokenHash, Instant expiresAt) {
		this.memberId = memberId;
		this.tokenHash = tokenHash;
		this.expiresAt = expiresAt;
	}

	public static RefreshToken issue(Long memberId, String tokenHash, Instant expiresAt) {
		if (tokenHash.length() > 64) {
			throw new InvalidTokenException();
		}
		return RefreshToken.builder()
			.memberId(memberId)
			.tokenHash(tokenHash)
			.expiresAt(expiresAt)
			.build();
	}
}
