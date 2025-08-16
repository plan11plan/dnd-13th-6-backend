package com.runky.auth.config.props;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.Min;

@ConfigurationProperties(prefix = "runky.security.jwt")
public record JwtProperties(
	Section access,
	Section refresh
) {
	public record Section(
		String secretKey,
		@Min(1) Long expirationMinutes,
		String algorithm
	) {
		public Duration ttl() {
			return Duration.ofMinutes(expirationMinutes);
		}
	}
}
