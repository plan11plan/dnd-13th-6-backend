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
}