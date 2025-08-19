package com.runky.auth.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "runky.security.refresh-token.hash")
public record RefreshTokenHashProperties(
	String pepper
) {
}
