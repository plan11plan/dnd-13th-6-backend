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

    public record Join(
            Long userId,
            String code
    ) {
        public CrewCommand.Join toCommand() {
            return new CrewCommand.Join(
                    userId,
                    code
            );
        }
    }

    public record Card(
            Long userId
    ) {
    }

    public record Detail(
            Long crewId,
            Long userId
    ) {
        public CrewCommand.Detail toCrewCommand() {
            return new CrewCommand.Detail(
                    crewId,
                    userId
            );
        }
    }

    private CrewCriteria() {
    }
}
