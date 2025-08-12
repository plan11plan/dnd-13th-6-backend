package com.runky.crew.application;

import com.runky.crew.domain.Crew;

public record CrewResult(
        Long id,
        String name,
        String code,
        Long leaderId,
        String notice,
        Long memberCount
) {
    public static CrewResult from(Crew crew) {
        return new CrewResult(
                crew.getId(),
                crew.getName(),
                crew.getCode().value(),
                crew.getLeaderId(),
                crew.getNotice(),
                crew.getMemberCount()
        );
    }
}
