package com.runky.auth.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.runky.auth.domain.port.RefreshTokenRepository;
import com.runky.auth.domain.port.TokenDecoder;
import com.runky.auth.domain.port.TokenHasher;
import com.runky.auth.domain.port.TokenIssuer;
import com.runky.auth.domain.vo.IssuedTokens;
import com.runky.auth.domain.vo.RefreshTokenClaims;
import com.runky.auth.exception.domain.TokenMismatchException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

	private final TokenIssuer tokenIssuer;
	private final TokenDecoder tokenDecoder;
	private final TokenHasher tokenHasher;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public AuthInfo.TokenPair issue(Long memberId, String role) {
		IssuedTokens issued = tokenIssuer.issue(memberId, role);
		String rtHash = tokenHasher.hash(issued.refresh().token());
		RefreshToken entity = RefreshToken.issue(
			memberId,
			rtHash,
			issued.refresh().issuedAt(),
			issued.refresh().expiresAt()
		);
		refreshTokenRepository.save(entity);

		return new AuthInfo.TokenPair(issued.access().token(), issued.refresh().token());
	}

	/**
	 * 다중 RT
	 * RT는 원문 저장 금지, 해시로만 저장
	 * 회전은 제시된 RT 1건만 해당, 다른 RT들은 영향 없음(멀티 디바이스 고려)
	 * DB에 레코드 없으면 -> 만료 예외 발생
	 */
	@Transactional
	public AuthInfo.TokenPair rotateByRefreshToken(String refreshToken) {

		RefreshTokenClaims decoded = tokenDecoder.decodeRefresh(refreshToken);
		String providedHash = tokenHasher.hash(refreshToken);

		RefreshToken current = refreshTokenRepository.findByMemberIdAndTokenHash(
			decoded.memberId(), providedHash).orElseThrow(TokenMismatchException::new);

		IssuedTokens reissued = tokenIssuer.issue(decoded.memberId(), decoded.role());
		String newHash = tokenHasher.hash(reissued.refresh().token());

		current.rotateTo(newHash, reissued.refresh().issuedAt(), reissued.refresh().expiresAt());
		refreshTokenRepository.save(current);

		return new AuthInfo.TokenPair(reissued.access().token(), reissued.refresh().token());

	}

	@Transactional
	public void delete(Long memberId) {
		refreshTokenRepository.deleteByMemberId(memberId);
	}
}
