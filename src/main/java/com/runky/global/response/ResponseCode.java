package com.runky.global.response;

import org.springframework.http.HttpStatus;

public interface ResponseCode {
	HttpStatus getStatus();

	String getCode();

	String getMessage();
}
