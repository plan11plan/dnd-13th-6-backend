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

    public record Detail(
            Long crewId,
            Long userId
    ) {
    }

    public record Leave(
            Long crewId,
            Long userId,
            Long newLeaderId
    ) {
    }

    public record Members(
            Long crewId,
            Long userId
    ) {
    }

    public record UpdateNotice(
            Long crewId,
            Long userId,
            String notice
    ) {
    }

    private CrewCommand() {
    }
}
