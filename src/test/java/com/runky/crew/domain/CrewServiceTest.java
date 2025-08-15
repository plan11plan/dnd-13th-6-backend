package com.runky.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.runky.global.error.GlobalErrorCode;
import com.runky.global.error.GlobalException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrewServiceTest {

    @InjectMocks
    private CrewService crewService;
    @Mock
    private CrewRepository crewRepository;
    @Mock
    private CodeGenerator codeGenerator;

    @DisplayName("크루 생성 시,")
    @Nested
    class Create {

        @Test
        @DisplayName("사용자의 크루 가입 수 정보가 없는 경우, NOT_FOUND 예외를 발생시킨다.")
        void throwNotFoundException_whenMemberCrewCountNotFound() {
            CrewCommand.Create command = new CrewCommand.Create(1L, "Test Crew");
            given(crewRepository.findCountByMemberId(command.userId()))
                    .willReturn(Optional.empty());

            GlobalException thrown = assertThrows(GlobalException.class, () -> crewService.create(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(GlobalErrorCode.NOT_FOUND));
        }
    }

    @DisplayName("크루 가입 시,")
    @Nested
    class Join {

        @Test
        @DisplayName("탈퇴했던 크루일 경우, 재가입 처리된다.")
        void rejoinCrew_whenAlreadyJoined() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Crew"), new Code("ABC123"));
            crew.joinMember(2L);
            crew.leaveMember(2L);
            given(crewRepository.findCrewByCode(crew.getCode()))
                    .willReturn(Optional.of(crew));
            given(crewRepository.findCountByMemberId(2L))
                    .willReturn(Optional.of(CrewMemberCount.of(2L)));
            CrewCommand.Join command = new CrewCommand.Join(2L, crew.getCode().value());

            Crew joinedCrew = crewService.join(command);

            assertThat(joinedCrew.getMember(2L).getRole()).isEqualTo(CrewMember.Role.MEMBER);
            assertThat(joinedCrew.getMember(2L).isInCrew()).isTrue();
        }

        @Test
        @DisplayName("크루에 처음 가입하는 경우, 새로운 멤버로 추가된다.")
        void joinCrew_whenFirstTimeJoining() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "New Crew"), new Code("XYZ789"));
            given(crewRepository.findCrewByCode(crew.getCode()))
                    .willReturn(Optional.of(crew));
            given(crewRepository.findCountByMemberId(3L))
                    .willReturn(Optional.of(CrewMemberCount.of(3L)));
            CrewCommand.Join command = new CrewCommand.Join(3L, crew.getCode().value());

            Crew joinedCrew = crewService.join(command);

            assertThat(joinedCrew.getMember(3L).getRole()).isEqualTo(CrewMember.Role.MEMBER);
            assertThat(joinedCrew.getMember(3L).isInCrew()).isTrue();
        }
    }
}