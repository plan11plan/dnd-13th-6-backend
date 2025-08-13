package com.runky.auth.infrastructure.erxternal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 사용자 정보 응답 DTO
 */
public record KakaoUserInfoResponse(
	Long id,
	@JsonProperty("connected_at") String connectedAt,
	@JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
	public record KakaoAccount(
		@JsonProperty("profile_nickname_needs_agreement") Boolean profileNicknameNeedsAgreement,
		Profile profile,
		@JsonProperty("has_email") Boolean hasEmail,
		@JsonProperty("email_needs_agreement") Boolean emailNeedsAgreement,
		@JsonProperty("is_email_valid") Boolean isEmailValid,
		@JsonProperty("is_email_verified") Boolean isEmailVerified,
		String email
	) {
	}

	public record Profile(
		String nickname,
		@JsonProperty("thumbnail_image_url") String thumbnailImageUrl,
		@JsonProperty("profile_image_url") String profileImageUrl,
		@JsonProperty("is_default_image") Boolean isDefaultImage
	) {
	}
}
