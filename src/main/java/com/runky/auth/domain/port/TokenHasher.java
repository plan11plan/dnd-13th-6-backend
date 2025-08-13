package com.runky.auth.domain.port;

public interface TokenHasher {
	String hash(String token);
}
