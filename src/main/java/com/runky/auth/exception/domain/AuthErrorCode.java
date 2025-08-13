package com.runky.auth.exception.domain;

import org.springframework.http.HttpStatus;

import com.runky.global.error.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401-01", "유효하지 않은 토큰입니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401-02", "만료된 토큰입니다."),
	TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH-401-03", "토큰 정보가 일치하지 않습니다."),
	TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH-400-01", "토큰이 필요합니다."),
	SIGNUP_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-401-10", "회원가입 토큰이 만료되었습니다. 다시 로그인해주세요.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
