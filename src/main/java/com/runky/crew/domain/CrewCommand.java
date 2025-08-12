package com.runky.crew.domain;

public class CrewCommand {
    public record Create(
            Long userId,
            String name
    ) {
    }

    private CrewCommand() {
    }
}
