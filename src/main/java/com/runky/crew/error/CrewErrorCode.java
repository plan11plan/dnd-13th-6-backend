package com.runky.crew.error;

import com.runky.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CrewErrorCode implements ErrorCode {
    OVER_CREW_COUNT(HttpStatus.CONFLICT, "C100", "크루는 최대 5개까지 참여할 수 있습니다"),
    OVER_CREW_NAME(HttpStatus.BAD_REQUEST, "C101", "크루 이름은 최대 15자까지 가능합니다."),
    BLANK_CREW_NAME(HttpStatus.BAD_REQUEST, "C102", "크루 이름은 공백일 수 없습니다."),
    OVER_CODE_LENGTH(HttpStatus.BAD_REQUEST, "C103", "코드의 길이는 6자여야 합니다."),
    INVALID_CODE_PATTERN(HttpStatus.BAD_REQUEST, "C104", "코드는 영문 대소문자와 숫자만 포함할 수 있습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
