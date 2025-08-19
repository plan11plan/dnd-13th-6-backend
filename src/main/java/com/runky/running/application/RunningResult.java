package com.runky.running.application;

import java.time.LocalDateTime;

import com.runky.running.domain.RunningInfo;

public sealed interface RunningResult {

	record Start(Long runningId, Long runnerId, String status, LocalDateTime startedAt) implements RunningResult {
		public static RunningResult.Start from(RunningInfo.Start info) {
			return new RunningResult.Start(
				info.runningId(),
				info.runnerId(),
				info.status(),
				info.startedAt()
			);
		}
	}

	record End(Long runningId, Long runnerId, String status, LocalDateTime startedAt, LocalDateTime endedAt
	) implements RunningResult {

		public static RunningResult.End from(RunningInfo.End info) {
			return new RunningResult.End(
				info.runningId(),
				info.runnerId(),
				info.status(),
				info.startedAt(),
				info.endedAt()
			);
		}
	}

}
