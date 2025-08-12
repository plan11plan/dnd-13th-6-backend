package com.runky.member.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	name = "member",
	uniqueConstraints = @UniqueConstraint(
		name = "uk_member_provider_provider_id",
		columnNames = {"provider", "provider_id"}
	),
	indexes = @Index(name = "ix_member_provider_provider_id", columnList = "provider,provider_id")
)
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private ExternalAccount externalAccount;

	@Enumerated(EnumType.STRING)
	private MemberRole role;
	private String nickname;

	@Builder(access = AccessLevel.PRIVATE)
	private Member(Long id, ExternalAccount externalAccount, MemberRole role, String nickname) {
		this.id = id;
		this.externalAccount = externalAccount;
		this.role = role;
		this.nickname = nickname;
	}

	public static Member register(ExternalAccount account, String nickname) {
		return Member.builder()
			.externalAccount(account)
			.role(MemberRole.USER)
			.nickname(nickname)
			.build();
	}
}
