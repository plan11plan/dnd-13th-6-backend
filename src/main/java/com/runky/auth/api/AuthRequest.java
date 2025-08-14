package com.runky.auth.api;

public sealed interface AuthRequest {

	record Signup(String nickname) implements AuthRequest {
	}
}
