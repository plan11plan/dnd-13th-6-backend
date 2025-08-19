package com.runky.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.error.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CrewMemberCountTest {

    @Test
    @DisplayName("UserCrewCount 생성 시, 속한 크루 개수는 0이다.")
    void createUserCrewCount_withCountZero() {
        Long userId = 1L;
        CrewMemberCount crewMemberCount = CrewMemberCount.of(userId);

        assertNotNull(crewMemberCount);
        assertEquals(userId, crewMemberCount.getMemberId());
        assertEquals(0L, crewMemberCount.getCrewCount());
    }

    @Test
    @DisplayName("속한 크루 개수가 5개를 초과하면, OVER_CREW_COUNT 예외가 발생한다.")
    void throwOverCrewCountException_whenOver5() {
        Long userId = 1L;
        CrewMemberCount crewMemberCount = CrewMemberCount.of(userId);
        for (int i = 0; i < 5; i++) {
            crewMemberCount.increment();
        }

        GlobalException thrown = assertThrows(GlobalException.class, crewMemberCount::increment);

        assertThat(thrown)
                .usingRecursiveComparison()
                .isEqualTo(new GlobalException(CrewErrorCode.OVER_CREW_COUNT));
    }

    @Test
    @DisplayName("속한 크루 개수가 0개인 경우, decrement 시, NOT_IN_CREW 예외가 발생한다.")
    void throwNotInCrewException_whenDecrementFromZero() {
        Long userId = 1L;
        CrewMemberCount crewMemberCount = CrewMemberCount.of(userId);

        GlobalException thrown = assertThrows(GlobalException.class, crewMemberCount::decrement);

        assertThat(thrown)
                .usingRecursiveComparison()
                .isEqualTo(new GlobalException(CrewErrorCode.NOT_IN_CREW));
    }
}