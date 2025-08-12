package com.runky.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CrewMemberTest {

    @DisplayName("CrewMember 생성 시,")
    @Nested
    class Create {

        @Test
        @DisplayName("리더를 생성할 수 있다.")
        void createLeader() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("ABC123"));

            CrewMember leader = CrewMember.leaderOf(crew);

            assertThat(leader.getRole()).isEqualTo(CrewMember.Role.LEADER);
        }
    }
}