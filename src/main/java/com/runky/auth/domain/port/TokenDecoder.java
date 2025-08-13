package com.runky.auth.domain.port;

import com.runky.auth.domain.vo.DecodedToken;

public interface TokenDecoder {
	DecodedToken decodeRefresh(String refreshToken);
}
