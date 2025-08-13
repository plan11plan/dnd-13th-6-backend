package com.runky.auth.domain.port;

import com.runky.auth.domain.vo.Tokens;

public interface TokenIssuer {
	Tokens issue(Long memberId, String memberRole);

}
