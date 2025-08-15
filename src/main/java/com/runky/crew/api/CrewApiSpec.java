package com.runky.crew.api;

import com.runky.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Crew API", description = "Runky Crew API입니다.")
public interface CrewApiSpec {

    @Operation(
            summary = "크루 생성",
            description = "크루를 생성합니다."
    )
    ApiResponse<CrewResponse.Create> createCrew(
            @Schema(name = "크루 생성 요청", description = "크루 생성에 필요한 정보") CrewRequest.Create request, Long userId
    );

    @Operation(
            summary = "크루 가입",
            description = "크루에 가입합니다."
    )
    ApiResponse<CrewResponse.Join> joinCrew(
            @Schema(name = "크루 가입 요청", description = "크루 가입에 필요한 정보") CrewRequest.Join request, Long userId
    );
}
