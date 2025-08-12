package com.runky.crew.domain;

public enum CrewConstants {
    CAPACITY(6),
    CODE_LENGTH(6),
    MAX_CREW_NAME_LENGTH(15),
    MAX_CREW_NOTICE_LENGTH(20);

    private final int value;

    CrewConstants(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
