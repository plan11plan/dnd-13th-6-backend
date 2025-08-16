package com.runky.auth.domain.vo;

public record IssuedTokens(RawAccessToken access, RawRefreshToken refresh) {
}
