package com.runky.member.domain.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.runky.member.domain.ExternalAccount;
import com.runky.member.domain.Member;
import com.runky.member.domain.dto.MemberCommand;
import com.runky.member.domain.dto.MemberInfo;
import com.runky.member.domain.exception.DuplicateMemberException;
import com.runky.member.domain.port.MemberRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberRegistrar {
	private final MemberRepository memberRepository;

	public MemberInfo.Summary registerFromExternal(MemberCommand.RegisterFromExternal command) {
		ExternalAccount account = ExternalAccount.of(command.provider(), command.providerId());
		String nickname = command.nickname();

		Optional<Member> existing = memberRepository
			.findByExternalAccountProviderAndExternalAccountProviderId(account.provider(), account.providerId());
		if (existing.isPresent()) {
			Member m = existing.get();
			return new MemberInfo.Summary(m.getId(), m.getRole(), m.getNickname());
		}

		Member member = Member.register(account, nickname);
		try {
			Member saved = memberRepository.saveAndFlush(member);
			return new MemberInfo.Summary(saved.getId(), saved.getRole(), saved.getNickname());
		} catch (DataIntegrityViolationException e) {
			Member merged = memberRepository.findByExternalAccountProviderAndExternalAccountProviderId(
				account.provider(), account.providerId()
			).orElseThrow(DuplicateMemberException::new);
			return new MemberInfo.Summary(merged.getId(), merged.getRole(), merged.getNickname());
		}

	}
}
