package com.runky.auth.domain.port;

import com.runky.auth.domain.vo.RefreshTokenClaims;

public interface TokenDecoder {
	RefreshTokenClaims decodeRefresh(String refreshToken);
}
