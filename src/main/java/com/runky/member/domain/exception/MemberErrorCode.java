package com.runky.member.domain.exception;

import org.springframework.http.HttpStatus;

import com.runky.global.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-404-01", "회원이 존재하지 않습니다."),

	INVALID_EXTERNAL_ACCOUNT(HttpStatus.BAD_REQUEST, "MEMBER-400-01", "외부 계정 정보가 올바르지 않습니다."),

	DUPLICATE_MEMBER(HttpStatus.CONFLICT, "MEMBER-409-01", "이미 가입된 회원입니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
