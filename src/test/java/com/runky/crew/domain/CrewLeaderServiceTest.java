package com.runky.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.error.GlobalException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrewLeaderServiceTest {

    @InjectMocks
    private CrewLeaderService crewLeaderService;
    @Mock
    private CrewRepository crewRepository;

    @Nested
    @DisplayName("크루 공지사항 수정 시,")
    class UpdateNotice {
        @Test
        @DisplayName("크루가 존재하지 않으면 NOT_FOUND_CREW 예외를 발생시킨다.")
        void throwNotFoundCrewException_whenCrewNotFound() {
            CrewCommand.UpdateNotice command = new CrewCommand.UpdateNotice(1L, 2L, "New Notice");
            given(crewRepository.findById(command.crewId()))
                    .willReturn(Optional.empty());

            GlobalException thrown = assertThrows(GlobalException.class, () -> crewLeaderService.updateNotice(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.NOT_FOUND_CREW));
        }

        @Test
        @DisplayName("사용자가 크루 리더가 아니면 NOT_CREW_LEADER 예외를 발생시킨다.")
        void throwNotCrewLeaderException_whenUserNotLeader() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("abc123"));
            crew.joinMember(2L);
            given(crewRepository.findById(crew.getId()))
                    .willReturn(Optional.of(crew));
            CrewCommand.UpdateNotice command = new CrewCommand.UpdateNotice(crew.getId(), 3L, "New Notice");

            GlobalException thrown = assertThrows(GlobalException.class, () -> crewLeaderService.updateNotice(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.NOT_CREW_LEADER));
        }
    }

    @Nested
    @DisplayName("크루 이름 수정 시,")
    class UpdateName {
        @Test
        @DisplayName("크루가 존재하지 않으면 NOT_FOUND_CREW 예외를 발생시킨다.")
        void throwNotFoundCrewException_whenCrewNotFound() {
            CrewCommand.UpdateName command = new CrewCommand.UpdateName(1L, 2L, "New Name");
            given(crewRepository.findById(command.crewId()))
                    .willReturn(Optional.empty());

            GlobalException thrown = assertThrows(GlobalException.class, () -> crewLeaderService.updateName(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.NOT_FOUND_CREW));
        }

        @Test
        @DisplayName("사용자가 크루 리더가 아니면 NOT_CREW_LEADER 예외를 발생시킨다.")
        void throwNotCrewLeaderException_whenUserNotLeader() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("abc123"));
            crew.joinMember(2L);
            given(crewRepository.findById(crew.getId()))
                    .willReturn(Optional.of(crew));
            CrewCommand.UpdateName command = new CrewCommand.UpdateName(crew.getId(), 3L, "New Name");

            GlobalException thrown = assertThrows(GlobalException.class, () -> crewLeaderService.updateName(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.NOT_CREW_LEADER));
        }
    }

    @Nested
    @DisplayName("크루 해체 시,")
    class Disband {
        @Test
        @DisplayName("크루가 존재하지 않으면 NOT_FOUND_CREW 예외를 발생시킨다.")
        void throwNotFoundCrewException_whenCrewNotFound() {
            CrewCommand.Disband command = new CrewCommand.Disband(1L, 2L);
            given(crewRepository.findById(command.crewId()))
                    .willReturn(Optional.empty());

            GlobalException thrown = assertThrows(GlobalException.class, () -> crewLeaderService.disband(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.NOT_FOUND_CREW));
        }

        @Test
        @DisplayName("사용자가 크루 리더가 아니면 NOT_CREW_LEADER 예외를 발생시킨다.")
        void throwNotCrewLeaderException_whenUserNotLeader() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("abc123"));
            crew.joinMember(2L);
            given(crewRepository.findById(crew.getId()))
                    .willReturn(Optional.of(crew));
            CrewCommand.Disband command = new CrewCommand.Disband(crew.getId(), 3L);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crewLeaderService.disband(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.NOT_CREW_LEADER));
        }

        @Test
        @DisplayName("크루에 속한 멤버들의 CrewMemberCount를 감소시킨다.")
        void decrementCrewMemberCount_whenCrewDisbanded() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("abc123"));
            crew.joinMember(2L);
            crew.joinMember(3L);
            crew.joinMember(4L);
            crew.banMember(4L);
            CrewMemberCount memberCount1 = CrewMemberCount.of(1L);
            memberCount1.increment();
            CrewMemberCount memberCount2 = CrewMemberCount.of(1L);
            memberCount2.increment();
            CrewMemberCount memberCount3 = CrewMemberCount.of(1L);
            memberCount3.increment();
            given(crewRepository.findById(crew.getId()))
                    .willReturn(Optional.of(crew));
            given(crewRepository.findCrewMemberCounts(Set.of(1L, 2L, 3L)))
                    .willReturn(List.of(memberCount1, memberCount2, memberCount3));
            CrewCommand.Disband command = new CrewCommand.Disband(crew.getId(), 1L);

            crewLeaderService.disband(command);

            assertThat(memberCount1.getCrewCount()).isEqualTo(0);
            assertThat(memberCount2.getCrewCount()).isEqualTo(0);
            assertThat(memberCount3.getCrewCount()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("크루 리더 위임 시,")
    class DelegateLeader {

        @Test
        @DisplayName("크루가 존재하지 않으면 NOT_FOUND_CREW 예외를 발생시킨다.")
        void throwNotFoundCrewException_whenCrewNotFound() {
            CrewCommand.Delegate command = new CrewCommand.Delegate(1L, 2L, 3L);
            given(crewRepository.findById(command.crewId()))
                    .willReturn(Optional.empty());

            GlobalException thrown = assertThrows(GlobalException.class, () -> crewLeaderService.delegateLeader(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.NOT_FOUND_CREW));
        }

        @Test
        @DisplayName("사용자가 크루 리더가 아니면 NOT_CREW_LEADER 예외를 발생시킨다.")
        void throwNotCrewLeaderException_whenUserNotLeader() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("abc123"));
            crew.joinMember(2L);
            given(crewRepository.findById(crew.getId()))
                    .willReturn(Optional.of(crew));
            CrewCommand.Delegate command = new CrewCommand.Delegate(crew.getId(), 3L, 4L);

            GlobalException thrown = assertThrows(GlobalException.class, () -> crewLeaderService.delegateLeader(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.NOT_CREW_LEADER));
        }

        @Test
        @DisplayName("크루의 리더 ID는 새로운 리더 ID로 변경된다.")
        void updateCrewLeaderId_whenDelegatingLeader() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Test Crew"), new Code("abc123"));
            crew.joinMember(2L);
            given(crewRepository.findById(crew.getId()))
                    .willReturn(Optional.of(crew));
            CrewCommand.Delegate command = new CrewCommand.Delegate(crew.getId(), 1L, 2L);

            Crew updatedCrew = crewLeaderService.delegateLeader(command);

            assertThat(updatedCrew.getLeaderId()).isEqualTo(2L);
        }
    }
}
