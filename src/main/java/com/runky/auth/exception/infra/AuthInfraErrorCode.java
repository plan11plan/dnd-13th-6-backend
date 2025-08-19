package com.runky.auth.exception.infra;

import org.springframework.http.HttpStatus;

import com.runky.global.error.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthInfraErrorCode implements ErrorCode {
	// JWT 키/알고리즘 관련
	JWT_ACCESS_SECRET_BLANK(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-INFRA-001", "JWT Access 비밀키가 비어있습니다."),
	JWT_REFRESH_SECRET_BLANK(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-INFRA-002", "JWT Refresh 비밀키가 비어있습니다."),
	JWT_SECRET_INVALID_LENGTH(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-INFRA-003", "JWT HMAC 비밀키 길이가 유효하지 않습니다."),
	JWT_KEY_INIT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-INFRA-004", "JWT 서명 키 초기화에 실패했습니다."),
	JWT_ALGORITHM_UNSUPPORTED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-INFRA-005", "지원하지 않는 JWT 알고리즘입니다."),

	// 토큰 해시 관련
	TOKEN_HASH_PEPPER_BLANK(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-INFRA-101", "리프레시 토큰 해시용 pepper 값이 비어있습니다."),
	TOKEN_HASH_ALGORITHM_UNSUPPORTED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-INFRA-102", "지원하지 않는 토큰 해시 알고리즘입니다."),
	TOKEN_HASH_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-INFRA-103", "토큰 해시 계산에 실패했습니다."),
	TOKEN_BLANK(HttpStatus.BAD_REQUEST, "AUTH-INFRA-104", "토큰이 비어있습니다."),
	;

	private final HttpStatus status;
	private final String code;
	private final String message;
}
