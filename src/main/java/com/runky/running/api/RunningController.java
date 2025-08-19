package com.runky.running.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.runky.global.response.ApiResponse;
import com.runky.running.application.RunningCriteria;
import com.runky.running.application.RunningFacade;
import com.runky.running.application.RunningResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/runnings")
@RequiredArgsConstructor
public class RunningController implements RunningApiSpec {

	private final RunningFacade runningFacade;

	@Override
	@PostMapping("/start")
	public ApiResponse<RunningResponse.Start> start(@RequestHeader("X-USER-ID") Long userId) {
		RunningResult.Start result = runningFacade.start(new RunningCriteria.Start(userId));

		String publish = "/app/runnings/" + result.runningId() + "/location";

		RunningResponse.Start response = RunningResponse.Start.from(publish, result);
		return ApiResponse.success(response);
	}

	@Override
	@PostMapping("/{runningId}/end")
	public ApiResponse<RunningResponse.End> end(@RequestHeader("X-USER-ID") Long userId, @PathVariable Long runningId,
		@RequestBody RunningRequest.End request) {
		RunningCriteria.End criteria = request.toCriteria(userId, runningId);
		RunningResult.End result = runningFacade.end(criteria);

		RunningResponse.End response = RunningResponse.End.from(result);
		return ApiResponse.success(response);
	}
}
