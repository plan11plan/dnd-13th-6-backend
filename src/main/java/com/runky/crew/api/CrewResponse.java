package com.runky.crew.api;

import com.runky.crew.application.CrewResult;

public class CrewResponse {
    public record Create(
            Long crewId,
            String name,
            String code
    ) {
        public static Create from(CrewResult result) {
            return new Create(result.id(), result.name(), result.code());
        }
    }

    public record Join(
            Long crewId
    ) {
        public static Join from(CrewResult result) {
            return new Join(result.id());
        }
    }

    private CrewResponse() {
    }
}
