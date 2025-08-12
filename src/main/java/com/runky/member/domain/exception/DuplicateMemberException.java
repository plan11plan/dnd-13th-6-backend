package com.runky.member.domain.exception;

import com.runky.global.error.GlobalException;

public class DuplicateMemberException extends GlobalException {
	public DuplicateMemberException() {
		super(MemberErrorCode.DUPLICATE_MEMBER);
	}
}
