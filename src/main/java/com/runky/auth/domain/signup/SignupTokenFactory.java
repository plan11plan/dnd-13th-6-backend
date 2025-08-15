package com.runky.auth.domain.signup;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Component;

import com.runky.auth.config.props.SignupTokenProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SignupTokenFactory {
	private final SignupTokenIdGenerator tokenIdGenerator;
	private final SignupTokenProperties props;

	public SignupToken create() {
		SignupTokenId id = new SignupTokenId(tokenIdGenerator.generate());
		Duration ttl = props.ttl();
		Instant expired = Instant.now(Clock.systemUTC()).plus(ttl);
		return SignupToken.issue(id, expired);
	}
}
