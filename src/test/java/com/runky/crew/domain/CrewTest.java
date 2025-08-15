package com.runky.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.error.GlobalErrorCode;
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

            assertThat(crew.getActiveMemberCount()).isEqualTo(1L);
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

    @Test
    @DisplayName("크루원 추가 시, 인원 수가 초과하면, OVER_CREW_MEMBER_COUNT 예외가 발생한다.")
    void throwOverCrewMemberCountException_whenAddingMemberExceedsCapacity() {
        CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
        Code code = new Code("ABC123");
        Crew crew = Crew.of(command, code);
        crew.joinMember(2L);
        crew.joinMember(3L);
        crew.joinMember(4L);
        crew.joinMember(5L);
        crew.joinMember(6L);

        GlobalException thrown = assertThrows(GlobalException.class, () -> crew.joinMember(7L));

        assertThat(thrown)
                .usingRecursiveComparison()
                .isEqualTo(new GlobalException(CrewErrorCode.OVER_CREW_MEMBER_COUNT));
    }

    @Nested
    @DisplayName("크루원 재가입 시,")
    class Rejoin {

        @Test
        @DisplayName("가입하지 않았던 사용자의 재가입 요청일 경우, NOT_FOUND 예외가 발생한다.")
        void throwNotFoundException_whenRejoiningMemberNotInCrew() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.rejoinMember(2L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(GlobalErrorCode.NOT_FOUND));
        }

        @Test
        @DisplayName("사용자가 크루에 재가입 요청할 때, 가입한 적이 없다면, NOT_FOUND 예외가 발생한다.")
        void throwNotFoundException_whenRejoiningMemberNotFound() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.rejoinMember(2L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(GlobalErrorCode.NOT_FOUND));
        }

        @Test
        @DisplayName("크루원이 이미 꽉 차있다면, OVER_CREW_MEMBER_COUNT 예외가 발생한다.")
        void throwOverCrewMemberCountException_whenRejoiningFullCrew() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);
            crew.joinMember(2L);
            crew.joinMember(3L);
            crew.joinMember(4L);
            crew.joinMember(5L);
            crew.joinMember(6L);
            crew.leaveMember(6L);
            crew.joinMember(7L);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.rejoinMember(6L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.OVER_CREW_MEMBER_COUNT));
        }
    }

    @Nested
    @DisplayName("크루 가입 시,")
    class Join {

        @Test
        @DisplayName("이미 가입한 크루일 경우, ALREADY_IN_CREW 예외가 발생한다.")
        void throwAlreadyInCrewException_whenJoiningAlreadyJoinedCrew() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);
            crew.joinMember(2L);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.joinMember(2L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.ALREADY_IN_CREW));
        }

        @Test
        @DisplayName("크루가 이미 꽉 찬 경우, OVER_CREW_MEMBER_COUNT 예외가 발생한다.")
        void throwOverCrewMemberCountException_whenJoiningFullCrew() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);
            crew.joinMember(2L);
            crew.joinMember(3L);
            crew.joinMember(4L);
            crew.joinMember(5L);
            crew.joinMember(6L);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.joinMember(7L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.OVER_CREW_MEMBER_COUNT));
        }
    }

    @Nested
    @DisplayName("크루원 추방 시,")
    class Ban {
        @Test
        @DisplayName("크루원이 존재하지 않으면, NOT_FOUND 예외가 발생한다.")
        void throwNotFoundException_whenBanningNonExistentMember() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.banMember(2L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(GlobalErrorCode.NOT_FOUND));
        }

        @Test
        @DisplayName("크루원 추방 후, 크루원 수가 감소한다.")
        void decrementActiveMemberCount_whenBanningMember() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);
            crew.joinMember(2L);
            crew.joinMember(3L);

            crew.banMember(2L);

            assertThat(crew.getActiveMemberCount()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("크루원 탈퇴 시,")
    class Leave {

        @Test
        @DisplayName("크루원이 존재하지 않으면, NOT_FOUND 예외가 발생한다.")
        void throwNotFoundException_whenLeavingNonExistentMember() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.leaveMember(2L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(GlobalErrorCode.NOT_FOUND));
        }

        @Test
        @DisplayName("크루원 탈퇴 후, 크루원 수가 감소한다.")
        void decrementActiveMemberCount_whenLeavingMember() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);
            crew.joinMember(2L);
            crew.joinMember(3L);

            crew.leaveMember(2L);

            assertThat(crew.getActiveMemberCount()).isEqualTo(2L);
        }
    }

    @Test
    @DisplayName("크루의 리더를 조회한다.")
    void getLeader() {
        CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
        Code code = new Code("ABC123");
        Crew crew = Crew.of(command, code);
        crew.joinMember(2L);
        crew.joinMember(3L);
        crew.joinMember(4L);

        CrewMember leader = crew.getLeader();

        assertThat(leader.getRole()).isEqualTo(CrewMember.Role.LEADER);
        assertThat(leader.getMemberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("크루의 멤버를 조회한다.")
    void getMember() {
        CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
        Code code = new Code("ABC123");
        Crew crew = Crew.of(command, code);
        crew.joinMember(2L);
        crew.joinMember(3L);

        CrewMember member = crew.getMember(2L);

        assertThat(member.getMemberId()).isEqualTo(2L);
        assertThat(member.getRole()).isEqualTo(CrewMember.Role.MEMBER);
    }
}
