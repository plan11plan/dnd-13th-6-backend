package com.runky.crew.api;

import com.runky.crew.application.CrewCriteria;
import com.runky.crew.application.CrewFacade;
import com.runky.crew.application.CrewResult;
import com.runky.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ApiResponse<CrewResponse.Create> createCrew(@Valid @RequestBody CrewRequest.Create request,
                                                       @RequestHeader("X-USER-ID") Long userId) {
        CrewResult result = crewFacade.create(new CrewCriteria.Create(userId, request.name()));
        return ApiResponse.success(CrewResponse.Create.from(result));
    }

    @Override
    @PostMapping("/join")
    public ApiResponse<CrewResponse.Join> joinCrew(@Valid @RequestBody CrewRequest.Join request,
                                                   @RequestHeader("X-USER-ID") Long userId) {
        CrewResult result = crewFacade.join(new CrewCriteria.Join(userId, request.code()));
        return ApiResponse.success(CrewResponse.Join.from(result));
    }

    @Override
    @GetMapping
    public ApiResponse<CrewResponse.Cards> getCrews(@RequestHeader("X-USER-ID") Long userId) {
        List<CrewResult.Card> cards = crewFacade.getCrews(userId);
        return ApiResponse.success(CrewResponse.Cards.from(cards));
    }
}
