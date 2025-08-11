package com.runky.crew.api;

import com.runky.crew.api.CrewResponse.Create;
import com.runky.crew.application.CrewCriteria;
import com.runky.crew.application.CrewFacade;
import com.runky.crew.application.CrewResult;
import com.runky.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crews")
@RequiredArgsConstructor
public class CrewController implements CrewApiSpec {
    private final CrewFacade crewFacade;

    @Override
    @PostMapping
    public ApiResponse<Create> createCrew(@Valid @RequestBody CrewRequest.Create request, @RequestHeader("X-USER-ID") Long userId) {
        CrewResult result = crewFacade.create(new CrewCriteria.Create(userId, request.name()));
        return ApiResponse.success(CrewResponse.Create.from(result));
    }
}
