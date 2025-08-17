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

    @Operation(
            summary = "크루 목록 조회",
            description = "사용자가 속한 크루 목록을 조회합니다."
    )
    ApiResponse<CrewResponse.Cards> getCrews(
            Long userId
    );

    @Operation(
            summary = "크루 상세 조회",
            description = "크루의 상세 정보를 조회합니다."
    )
    ApiResponse<CrewResponse.Detail> getCrew(
            @Schema(name = "크루 ID", description = "상세 조회할 크루 ID") Long crewId,
            Long userId
    );

    @Operation(
            summary = "크루 탈퇴",
            description = "크루에서 탈퇴합니다."
    )
    ApiResponse<CrewResponse.Leave> leaveCrew(
            @Schema(name = "멤버 ID", description = "새 리더로 지정할 멤버 ID") CrewRequest.Leave request,
            @Schema(name = "크루 ID", description = "탈퇴할 크루 ID") Long crewId,
            Long userId
    );

    @Operation(
            summary = "크루원 목록 조회",
            description = "크루의 멤버를 조회합니다."
    )
    ApiResponse<CrewResponse.Members> getCrewMembers(
            @Schema(name = "크루 ID", description = "멤버를 조회할 크루 ID") Long crewId,
            Long userId
    );

    @Operation(
            summary = "크루 공지사항 수정",
            description = "크루의 공지사항을 수정합니다."
    )
    ApiResponse<CrewResponse.Notice> updateNotice(
            @Schema(name = "크루 공지사항 수정 요청", description = "크루 공지사항 수정에 필요한 정보") CrewRequest.Notice request,
            @Schema(name = "크루 ID", description = "공지사항을 수정할 크루 ID") Long crewId,
            Long userId
    );
}
