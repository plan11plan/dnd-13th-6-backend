package com.runky.crew.domain;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.error.GlobalException;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Code {
    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    private String value;

    public Code(String value) {
        if (value.length() != CrewConstants.CODE_LENGTH.value()) {
            throw new GlobalException(CrewErrorCode.OVER_CODE_LENGTH);
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new GlobalException(CrewErrorCode.INVALID_CODE_PATTERN);
        }

        this.value = value;
    }

    public String value() {
        return value;
    }
}
