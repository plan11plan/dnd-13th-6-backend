package com.runky.auth.domain.vo;

import java.time.Instant;

public record RawRefreshToken(String token, Instant issuedAt, Instant expiresAt) {
}
