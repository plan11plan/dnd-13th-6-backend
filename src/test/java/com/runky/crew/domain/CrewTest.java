package com.runky.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.error.GlobalErrorCode;
import com.runky.global.error.GlobalException;
import java.util.List;
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
    @DisplayName("크루 가입 시,")
    class Join {

        @Test
        @DisplayName("가입한 기록이 있는 경우, 추방된 멤버라면, BANNED_MEMBER 예외가 발생한다.")
        void throwBannedMemberException_whenJoiningBannedMember() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);
            crew.joinMember(2L);
            crew.banMember(2L);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.joinMember(2L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.BANNED_MEMBER));
        }

        @Test
        @DisplayName("가입한 기록이 있는 경우, 이미 가입된 멤버라면, ALREADY_IN_CREW 예외가 발생한다.")
        void throwAlreadyInCrewException_whenJoiningAlreadyJoinedMember() {
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

    @Nested
    @DisplayName("크루 리더 위임 시,")
    class Delegate {

        @Test
        @DisplayName("새 리더를 지정하지 않으면, HAVE_TO_DELEGATE_LEADER 예외가 발생한다.")
        void throwHaveToDelegateLeaderException_whenNoNewLeaderSpecified() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.delegateLeader(null));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.HAVE_TO_DELEGATE_LEADER));
        }

        @Test
        @DisplayName("새 리더는 LEADER 역할로 지정되고, Crew의 LeaderId가 변경된다.")
        void delegateLeader() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);
            crew.joinMember(2L);
            crew.joinMember(3L);

            crew.delegateLeader(2L);

            assertThat(crew.getLeader().getMemberId()).isEqualTo(2L);
            assertThat(crew.getLeader().getRole()).isEqualTo(CrewMember.Role.LEADER);
            assertThat(crew.getLeaderId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("이전 리더는 MEMBER 역할로 변경된다.")
        void previousLeaderBecomesMember() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);
            crew.joinMember(2L);
            crew.joinMember(3L);

            crew.delegateLeader(2L);

            CrewMember previousLeader = crew.getMember(1L);
            assertThat(previousLeader.getRole()).isEqualTo(CrewMember.Role.MEMBER);
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

    @Nested
    @DisplayName("크루 내 가입했던 멤버 조회 시,")
    class GetHistory {

        @Test
        @DisplayName("가입한 적이 없다면, NOT_FOUND 예외가 발생한다.")
        void throwNotFoundException_whenGettingNonExistentMemberHistory() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.getMemberHistory(2L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(GlobalErrorCode.NOT_FOUND));
        }
    }

    @Nested
    @DisplayName("크루 내 활동중인 멤버 조회 시,")
    class GetMember {

        @Test
        @DisplayName("크루 내 활동중인 멤버 조회 시, 멤버가 존재하지 않다면, NOT_FOUND 예외가 발생한다.")
        void throwNotFoundException_whenGettingNonExistentMember() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.getMember(2L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(GlobalErrorCode.NOT_FOUND));
        }

        @Test
        @DisplayName("크루 내 활동중인 멤버 조회 시, 탈퇴한 멤버라면, NOT_FOUND 예외가 발생한다.")
        void throwNotFoundException_whenGettingLeftMember() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);
            crew.joinMember(2L);
            crew.leaveMember(2L);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.getMember(2L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(GlobalErrorCode.NOT_FOUND));
        }

        @Test
        @DisplayName("크루 내 활동중인 멤버 조회 시, 추방 멤버라면, NOT_FOUND 예외가 발생한다.")
        void throwNotFoundException_whenGettingBannedMember() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);
            crew.joinMember(2L);
            crew.banMember(2L);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crew.getMember(2L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(GlobalErrorCode.NOT_FOUND));
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

    @Nested
    @DisplayName("크루의 활동 멤버 조회 시,")
    class GetActiveMembers {

        @Test
        @DisplayName("크루의 활동 멤버를 조회한다.")
        void getActiveMembers() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "ValidName");
            Code code = new Code("ABC123");
            Crew crew = Crew.of(command, code);
            crew.joinMember(2L);
            crew.joinMember(3L);
            crew.joinMember(4L);
            crew.leaveMember(4L);

            List<CrewMember> activeMembers = crew.getActiveMembers();

            assertThat(activeMembers).hasSize(3);
            assertThat(activeMembers).extracting("memberId")
                    .containsExactlyInAnyOrder(1L, 2L, 3L);
        }
    }
}
