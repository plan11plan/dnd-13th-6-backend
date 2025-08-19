package com.runky.member.domain.port;

import java.util.Optional;

import com.runky.member.domain.Member;

public interface MemberRepository {
	boolean existsByExternalAccountProviderAndExternalAccountProviderId(String provider, String providerId);

	Optional<Member> findByExternalAccountProviderAndExternalAccountProviderId(String provider, String providerId);

	Member save(Member member);

}
