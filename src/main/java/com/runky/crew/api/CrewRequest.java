package com.runky.crew.api;

import jakarta.validation.constraints.NotNull;

public class CrewRequest {
    public record Create(
            @NotNull(message = "크루 이름은 필수입니다.")
            String name
    ) {
    }

    private CrewRequest() {
    }
}
