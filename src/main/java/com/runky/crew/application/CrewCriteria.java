package com.runky.crew.application;

import com.runky.crew.domain.CrewCommand;

public class CrewCriteria {
    public record Create(
            Long userId,
            String name
    ) {
        public CrewCommand.Create toCommand() {
            return new CrewCommand.Create(
                    userId,
                    name
            );
        }
    }

    private CrewCriteria() {
    }
}
