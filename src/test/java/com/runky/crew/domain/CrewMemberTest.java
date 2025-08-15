package com.runky.crew.domain;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.error.GlobalException;
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

        @Test
        @DisplayName("일반 크루원을 생성할 수 있다.")
        void createMember() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("ABC123"));

            CrewMember member = CrewMember.memberOf(2L, crew);

            assertThat(member.getRole()).isEqualTo(CrewMember.Role.MEMBER);
        }
    }

    @Nested
    @DisplayName("크루 재가입 할 시,")
    class Rejoin {
        @Test
        @DisplayName("추방된 크루라면, BANNED_MEMBER 예외가 발생한다.")
        void throwBannedMemberException_whenBanned() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("ABC123"));
            crew.joinMember(2L);
            crew.banMember(2L);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.rejoinMember(2L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.BANNED_MEMBER));
        }

        @Test
        @DisplayName("이미 가입된 크루라면, ALREADY_IN_CREW 예외가 발생한다.")
        void throwAlreadyInCrewException_whenAlreadyJoined() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("ABC123"));
            crew.joinMember(2L);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.rejoinMember(2L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.ALREADY_IN_CREW));
        }
    }

    @Test
    @DisplayName("크루원이 추방될 경우, 상태는 BANNED로 변경된다.")
    void banMember() {
        Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("ABC123"));
        CrewMember crewMember = CrewMember.memberOf(1L, crew);

        crewMember.ban();

        assertThat(crewMember.getRole()).isEqualTo(CrewMember.Role.BANNED);
    }

    @Test
    @DisplayName("크루의 리더와 멤버는 크루에 속해있다고 판단한다.")
    void isInCrew() {
        Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("ABC123"));

        CrewMember leader = crew.getLeader();
        CrewMember member = CrewMember.memberOf(1L, crew);

        boolean isLeaderInCrew = leader.isInCrew();
        boolean isMemberInCrew = member.isInCrew();

        assertThat(isLeaderInCrew).isTrue();
        assertThat(isMemberInCrew).isTrue();
    }
}