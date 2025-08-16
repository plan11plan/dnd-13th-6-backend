package com.runky.auth.domain.vo;

import java.time.Instant;

public record RawAccessToken(String token, Instant issuedAt, Instant expiresAt) {
}
