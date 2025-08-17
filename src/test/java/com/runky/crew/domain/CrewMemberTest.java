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
            CrewMember crewMember = crew.banMember(2L);

            GlobalException thrown = assertThrows(GlobalException.class, crewMember::rejoin);

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.BANNED_MEMBER));
        }

        @Test
        @DisplayName("이미 가입된 크루라면, ALREADY_IN_CREW 예외가 발생한다.")
        void throwAlreadyInCrewException_whenAlreadyJoined() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("ABC123"));
            CrewMember crewMember = crew.joinMember(2L);

            GlobalException thrown = assertThrows(GlobalException.class, crewMember::rejoin);

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

    @Nested
    @DisplayName("크루 탈퇴 시,")
    class Leave {

        @Test
        @DisplayName("리더가 탈퇴하면, HAVE_TO_DELEGATE_LEADER 예외가 발생한다.")
        void throwHaveToDelegateLeaderException_whenLeaderLeaves() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("ABC123"));
            CrewMember leader = crew.getLeader();

            GlobalException thrown = assertThrows(GlobalException.class, leader::leave);

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.HAVE_TO_DELEGATE_LEADER));
        }

        @Test
        @DisplayName("추방된 크루원이 탈퇴하면, NOT_CREW_MEMBER 예외가 발생한다.")
        void throwNotCrewMemberException_whenBannedMemberLeaves() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("ABC123"));
            crew.joinMember(2L);
            CrewMember bannedMember = crew.banMember(2L);

            GlobalException thrown = assertThrows(GlobalException.class, bannedMember::leave);

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.NOT_CREW_MEMBER));
        }

        @Test
        @DisplayName("이미 탈퇴한 크루원이 다시 탈퇴하면, NOT_CREW_MEMBER 예외가 발생한다.")
        void throwNotCrewMemberException_whenAlreadyLeftMemberLeaves() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("ABC123"));
            CrewMember member = CrewMember.memberOf(2L, crew);
            member.leave();

            GlobalException thrown = assertThrows(GlobalException.class, member::leave);

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.NOT_CREW_MEMBER));
        }

        @Test
        @DisplayName("멤버는 정상적으로 탈퇴할 수 있다.")
        void memberCanLeaveCrew() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("ABC123"));
            CrewMember member = CrewMember.memberOf(2L, crew);

            member.leave();

            assertThat(member.getRole()).isEqualTo(CrewMember.Role.LEFT);
        }
    }
}