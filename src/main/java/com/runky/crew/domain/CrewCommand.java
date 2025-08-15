package com.runky.crew.domain;

public class CrewCommand {
    public record Create(
            Long userId,
            String name
    ) {
    }

    public record Join(
            Long userId,
            String code
    ) {
    }

    private CrewCommand() {
    }
}
