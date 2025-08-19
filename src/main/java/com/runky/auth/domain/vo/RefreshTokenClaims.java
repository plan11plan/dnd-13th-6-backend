package com.runky.auth.domain.vo;

import java.time.Instant;

public record RefreshTokenClaims(Long memberId, String role, Instant expiresAt) {
}
