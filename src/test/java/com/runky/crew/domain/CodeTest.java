package com.runky.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.error.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CodeTest {

    @Nested
    @DisplayName("코드 생성 시,")
    class Create {

        @ParameterizedTest
        @ValueSource(strings = {"ABC12", "ABC1234"})
        @DisplayName("코드 길이가 6자가 아니면, BAD_REQUEST 예외가 발생한다.")
        void throwBadRequestException_whenCodeLengthIsNotSix(String code) {
            GlobalException thrown = assertThrows(GlobalException.class, () -> new Code(code));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.OVER_CODE_LENGTH));
        }

        @ParameterizedTest
        @ValueSource(strings = {"ABC12!", "ABC1코드", "ABC 23"})
        @DisplayName("코드가 영문자와 숫자가 아니면, BAD_REQUEST 예외가 발생한다.")
        void throwBadRequestException_whenCodeContainsInvalidCharacters(String code) {
            GlobalException thrown = assertThrows(GlobalException.class, () -> new Code(code));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new GlobalException(CrewErrorCode.INVALID_CODE_PATTERN));
        }
    }
}