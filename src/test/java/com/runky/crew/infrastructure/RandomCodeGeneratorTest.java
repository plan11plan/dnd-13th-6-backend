package com.runky.crew.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.runky.crew.domain.Code;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RandomCodeGeneratorTest {

    @DisplayName("코드 생성 시, 6자리 코드가 생성된다.")
    @Test
    void generate_lengthEqualSix() {
        RandomCodeGenerator randomCodeGenerator = new RandomCodeGenerator();

        Code code = randomCodeGenerator.generate();

        assertThat(code.value()).hasSize(6);
    }
}