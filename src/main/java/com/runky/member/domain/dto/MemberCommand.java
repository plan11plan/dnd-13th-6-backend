package com.runky.member.domain.dto;

public sealed interface MemberCommand {

	record RegisterFromExternal(String provider, String providerId, String nickname) implements MemberCommand {
	}
}
