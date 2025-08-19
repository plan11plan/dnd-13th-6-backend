package com.runky.auth.config.props;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "runky.security.cookie")
public record AuthCookieProperties(
	Common common,
	Spec access,
	Spec refresh,
	Spec signup
) {
	public record Common(
		String path,
		String domain,
		boolean httpOnly,
		boolean secure,
		String sameSite // "Lax" | "Strict" | "None"
	) {
	}

	/** maxAgeMinutes가 null이면 토큰 TTL과 동기화 */
	public record Spec(
		String name,
		Long maxAgeMinutes
	) {
		public Duration maxAgeOr(Duration fallback) {
			return (maxAgeMinutes == null) ? fallback : Duration.ofMinutes(maxAgeMinutes);
		}
	}
}
