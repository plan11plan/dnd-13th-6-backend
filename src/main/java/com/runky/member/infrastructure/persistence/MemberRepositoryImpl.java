package com.runky.member.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.runky.member.domain.Member;
import com.runky.member.domain.port.MemberRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
	private final JpaMemberRepository jpa;

	@Override
	public Optional<Member> findByExternalAccountProviderAndExternalAccountProviderId(String provider,
		String providerId) {
		return jpa.findByExternalAccountProviderAndExternalAccountProviderId(provider, providerId);
	}

	@Override
	public boolean existsByExternalAccountProviderAndExternalAccountProviderId(String provider, String providerId) {
		return jpa.existsByExternalAccountProviderAndExternalAccountProviderId(provider, providerId);
	}

	@Override
	public Member save(Member member) {
		return jpa.save(member);
	}
}
