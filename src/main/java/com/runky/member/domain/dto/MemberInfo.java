package com.runky.member.domain.dto;

import com.runky.member.domain.MemberRole;

public sealed interface MemberInfo {

	record Summary(Long id, MemberRole role, String nickname) implements MemberInfo {
	}
}
