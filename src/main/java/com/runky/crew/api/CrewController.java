package com.runky.crew.api;

import com.runky.crew.application.CrewCriteria;
import com.runky.crew.application.CrewFacade;
import com.runky.crew.application.CrewResult;
import com.runky.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Override
    @GetMapping("/{crewId}")
    public ApiResponse<CrewResponse.Detail> getCrew(@PathVariable Long crewId,
                                                    @RequestHeader("X-USER-ID") Long userId) {
        CrewResult.Detail result = crewFacade.getCrew(new CrewCriteria.Detail(crewId, userId));
        return ApiResponse.success(CrewResponse.Detail.from(result));
    }

    @Override
    @DeleteMapping("/{crewId}/members/me")
    public ApiResponse<CrewResponse.Leave> leaveCrew(@Valid @RequestBody CrewRequest.Leave request,
                                                     @PathVariable Long crewId,
                                                     @RequestHeader("X-USER-ID") Long userId) {
        CrewResult.Leave result = crewFacade.leaveCrew(new CrewCriteria.Leave(crewId, userId, request.newLeaderId()));
        return ApiResponse.success(CrewResponse.Leave.from(result));
    }

    @Override
    @GetMapping("/{crewId}/members")
    public ApiResponse<CrewResponse.Members> getCrewMembers(@PathVariable Long crewId,
                                                            @RequestHeader("X-USER-ID") Long userId) {
        List<CrewResult.CrewMember> results = crewFacade.getCrewMembers(new CrewCriteria.Members(crewId, userId));
        List<CrewResponse.Member> members = results.stream()
                .map(CrewResponse.Member::from)
                .toList();
        return ApiResponse.success(new CrewResponse.Members(members));
    }

    @Override
    @PatchMapping("/{crewId}/notice")
    public ApiResponse<CrewResponse.Notice> updateNotice(@RequestBody CrewRequest.Notice request,
                                                         @PathVariable Long crewId,
                                                         @RequestHeader("X-USER-ID") Long userId) {
        CrewResult result = crewFacade.updateNotice(new CrewCriteria.UpdateNotice(crewId, userId, request.notice()));
        return ApiResponse.success(new CrewResponse.Notice(result.notice()));
    }

    @Override
    @PatchMapping("/{crewId}/name")
    public ApiResponse<CrewResponse.Name> updateName(@RequestBody CrewRequest.Name request,
                                                     @PathVariable Long crewId,
                                                     @RequestHeader("X-USER-ID") Long userId) {
        CrewResult result = crewFacade.updateName(new CrewCriteria.UpdateName(crewId, userId, request.name()));
        return ApiResponse.success(new CrewResponse.Name(result.name()));
    }

    @Override
    @DeleteMapping("/{crewId}")
    public ApiResponse<CrewResponse.Disband> disbandCrew(@PathVariable Long crewId,
                                                         @RequestHeader("X-USER-ID") Long userId) {
        CrewResult result = crewFacade.disband(new CrewCriteria.Disband(crewId, userId));
        return ApiResponse.success(new CrewResponse.Disband(result.name()));
    }

    @Override
    @PatchMapping("/{crewId}/leader")
    public ApiResponse<CrewResponse.Delegate> delegateLeader(@RequestBody CrewRequest.Delegate request,
                                                             @PathVariable Long crewId,
                                                             @RequestHeader("X-USER-ID") Long userId) {
        CrewResult.Delegate result = crewFacade.delegateLeader(
                new CrewCriteria.Delegate(crewId, userId, request.newLeaderId()));
        return ApiResponse.success(new CrewResponse.Delegate(result.leaderId(), result.leaderNickname()));
    }

    @Override
    @DeleteMapping("/{crewId}/members/{memberId}")
    public ApiResponse<CrewResponse.Ban> banMember(@PathVariable("crewId") Long crewId,
                                                   @PathVariable("memberId") Long targetId,
                                                   @RequestHeader("X-USER-ID") Long userId) {
        CrewResult.Ban result = crewFacade.banMember(new CrewCriteria.Ban(crewId, userId, targetId));
        return ApiResponse.success(new CrewResponse.Ban(result.targetId(), result.nickname()));
    }
}
