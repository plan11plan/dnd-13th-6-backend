package com.runky.auth.domain.port;

import com.runky.auth.domain.vo.IssuedTokens;

public interface TokenIssuer {
	IssuedTokens issue(Long memberId, String memberRole);

}
