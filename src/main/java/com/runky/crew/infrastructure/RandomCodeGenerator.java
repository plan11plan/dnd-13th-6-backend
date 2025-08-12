package com.runky.crew.infrastructure;

import com.runky.crew.domain.Code;
import com.runky.crew.domain.CodeGenerator;
import com.runky.crew.domain.CrewConstants;
import com.runky.global.error.GlobalErrorCode;
import com.runky.global.error.GlobalException;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class RandomCodeGenerator implements CodeGenerator {

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    @Override
    public Code generate() {
        StringBuilder stringBuilder = new StringBuilder(CrewConstants.CODE_LENGTH.value());
        for (int i = 0; i < CrewConstants.CODE_LENGTH.value(); i++) {
            int idx = random.nextInt(CHAR_SET.length());
            stringBuilder.append(CHAR_SET.charAt(idx));
        }
        try {
            return new Code(stringBuilder.toString());
        } catch (RuntimeException e) {
            throw new GlobalException(GlobalErrorCode.OTHER);
        }
    }
}
