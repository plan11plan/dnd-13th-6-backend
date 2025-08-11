package com.runky.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.error.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CrewTest {

    @Nested
    @DisplayName("크루 생성 시,")
    class Create {

        @Test
        @DisplayName("크루 이름이 공백이면, BLANK_CREW_NAME 예외가 발생한다.")
        void throwBlankCrewNameException_whenNameIsBlank() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "  ");

            GlobalException thrown = assertThrows(GlobalException.class, () -> Crew.of(command, new Code("ABC123")));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.BLANK_CREW_NAME));
        }

        @Test
        @DisplayName("크루 이름이 10자를 초과하면, OVER_CREW_NAME 예외가 발생한다.")
        void throwOverCrewNameException_whenNameIsOver10() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ThisNameIsTooLong");

            GlobalException thrown = assertThrows(GlobalException.class, () -> Crew.of(command, new Code("ABC123")));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.OVER_CREW_NAME));
        }

        @Test
        @DisplayName("공지는 공백으로 생성된다.")
        void returnBlankNotice_whenCreated() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");

            Crew crew = Crew.of(command, code);

            assertThat(crew.getNotice()).isEqualTo("");
        }

        @Test
        @DisplayName("크루 현 인원은 생성한 사람을 포함한 1명으로 생성된다.")
        void returnZeroMemberCount_whenCreated() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");

            Crew crew = Crew.of(command, code);

            assertThat(crew.getMemberCount()).isEqualTo(1L);
        }

        @Test
        @DisplayName("크루를 생성한 사람은 리더로 존재한다.")
        void createLeader() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");

            Crew crew = Crew.of(command, code);

            assertThat(crew.getMembers()).hasSize(1);
            assertThat(crew.getMembers().get(0).getRole()).isEqualTo(CrewMember.Role.LEADER);
        }
    }
}