package com.runky.auth.infrastructure.erxternal;

import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.runky.auth.infrastructure.erxternal.dto.KakaoTokenResponse;
import com.runky.auth.infrastructure.erxternal.dto.KakaoUserInfoResponse;

public interface KakaoApiHttpClient {

	/**
	 * 인가 코드로 카카오 토큰을 요청합니다.
	 */
	@PostExchange(url = "https://kauth.kakao.com/oauth/token", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	KakaoTokenResponse getAccessToken(@RequestBody MultiValueMap<String, String> body);

	/**
	 * 액세스 토큰으로 사용자 정보를 조회합니다.
	 */
	@GetExchange("https://kapi.kakao.com/v2/user/me")
	KakaoUserInfoResponse getUserInfo(@RequestHeader("Authorization") String authorizationHeader);
}
