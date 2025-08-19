package com.runky.running.api;

import com.runky.running.application.RunningCriteria;

public sealed interface RunningRequest {

	record Start(Long runnerId) implements RunningRequest {
	}

	record End(
		Long runnerId,
		Summary summary,
		Track track
	) implements RunningRequest {
		RunningCriteria.End toCriteria(Long runningId) {
			return new RunningCriteria.End(
				runningId,
				runnerId,
				summary.totalDistanceM,
				summary.durationS,
				summary.avgSpeedMPS,
				track.format,
				track.points,
				track.pointCount
			);
		}

		record Summary(
			Double totalDistanceM,
			Long durationS,
			Double avgSpeedMPS
		) {
		}

		record Track(
			String format,
			String points,
			int pointCount
		) {
		}

		record Point(
			Double lat,
			Double lng,
			Long timestamp
		) {
		}
	}
}
