package com.runky.auth.domain.vo;

import java.time.Instant;

public record DecodedToken(Long memberId, String role, Instant expiresAt) {
}
