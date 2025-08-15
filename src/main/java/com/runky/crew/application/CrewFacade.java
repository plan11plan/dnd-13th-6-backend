package com.runky.crew.application;

import com.runky.crew.domain.Crew;
import com.runky.crew.domain.CrewMember;
import com.runky.crew.domain.CrewService;
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
}
