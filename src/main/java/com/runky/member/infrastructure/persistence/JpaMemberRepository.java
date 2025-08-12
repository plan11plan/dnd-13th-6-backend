package com.runky.member.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.runky.member.domain.Member;

public interface JpaMemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByExternalAccountProviderAndExternalAccountProviderId(String provider, String providerId);

	boolean existsByExternalAccountProviderAndExternalAccountProviderId(String provider, String providerId);
}
