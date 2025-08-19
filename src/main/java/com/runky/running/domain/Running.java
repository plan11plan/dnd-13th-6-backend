package com.runky.running.domain;

import java.time.LocalDateTime;

import com.runky.global.error.GlobalException;
import com.runky.running.error.RunningErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "runnings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Running {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "runner_id", nullable = false)
	private Long runnerId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private Status status; // RUNNING, FINISHED

	@Column(name = "started_at", nullable = false)
	private LocalDateTime startedAt;

	@Column(name = "ended_at", nullable = true)
	private LocalDateTime endedAt;

	@Column(name = "total_distance_minutes")
	private Double totalDistanceMinutes;

	@Column(name = "duration_seconds")
	private Long durationSeconds;

	@Column(name = "avg_speed_mps")
	private Double avgSpeedMPS;

	public static Running start(Long runningId, LocalDateTime now) {
		return Running.builder()
			.runnerId(runningId)
			.status(Status.RUNNING)
			.startedAt(now)
			.build();
	}

	public boolean isActive() {
		return this.status == Status.RUNNING && this.endedAt == null;
	}

	public void finish(double totalDistanceMinutes, long durationSeconds, Double avgSpeedMps) {
		if (this.status != Status.RUNNING) {
			throw new GlobalException(RunningErrorCode.NOT_ACTIVE_RUNNING);
		}
		this.endedAt = LocalDateTime.now();
		this.status = Status.FINISHED;
		this.totalDistanceMinutes = totalDistanceMinutes;
		this.durationSeconds = durationSeconds;
		this.avgSpeedMPS = avgSpeedMps;
	}

	public enum Status {RUNNING, FINISHED}
}
