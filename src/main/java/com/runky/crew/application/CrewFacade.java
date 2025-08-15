package com.runky.crew.application;

import com.runky.crew.domain.Crew;
import com.runky.crew.domain.CrewService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrewFacade {
    private final CrewService crewService;

    public CrewResult create(CrewCriteria.Create criteria) {
        Crew crew = crewService.create(criteria.toCommand());
        return CrewResult.from(crew);
    }

    public CrewResult join(CrewCriteria.Join criteria) {
        Crew crew = crewService.join(criteria.toCommand());
        return CrewResult.from(crew);
    }

    public List<CrewResult.Card> getCrews(Long userId) {
        List<Crew> crews = crewService.getCrewsOfUser(userId);
        // TODO 크루원 대표 캐릭터 이미지 불러오는 작업 추가
        return crews.stream()
                .map(crew -> new CrewResult.Card(crew.getId(), crew.getName(), crew.getActiveMemberCount(),
                        crew.getLeaderId().equals(userId), List.of("runky/1.pvg", "runky/2.png")))
                .toList();
    }

    public CrewResult.Detail getCrew(CrewCriteria.Detail criteria) {
        Crew crew = crewService.getCrew(criteria.toCrewCommand());
        BigDecimal goal = new BigDecimal("10.00"); // TODO 그룹 목표 조합 작업 추가 : GoalService
        String leaderNickname = "leader"; // TODO 크루 리더의 닉네임을 가져오는 작업 추가 : UserService
        return new CrewResult.Detail(crew.getId(), crew.getName(), leaderNickname, crew.getNotice(),
                crew.getActiveMemberCount(), goal, crew.getCode().value());
    }
}
