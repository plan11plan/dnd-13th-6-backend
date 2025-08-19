package com.runky.auth.scheduler;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.runky.auth.domain.RefreshTokenMaintenanceService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {

	private final RefreshTokenMaintenanceService maintenanceService;

	// 매주 일요일 0시 0분에 실행
	@Scheduled(cron = "0 0 0 * * 0", zone = "Asia/Seoul")
	@Transactional
	public void cleanupExpiredRefreshTokens() {
		int deleted = maintenanceService.deleteExpiredUntil(Instant.now());
	}
}
