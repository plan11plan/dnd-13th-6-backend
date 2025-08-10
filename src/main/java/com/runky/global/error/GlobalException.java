package com.runky.global.error;

import lombok.Getter;

/** 프로젝트 에러를 반환 할 때 사용하는 기본 클래스 errorCode를 주입받아야 한다. */
public class GlobalException extends RuntimeException {
	@Getter
	private final ErrorCode errorCode;

	public GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
