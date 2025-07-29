package com.dnd13.runners_server.support.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	HttpStatus getStatus();

	String getCode();

	String getMessage();
}
