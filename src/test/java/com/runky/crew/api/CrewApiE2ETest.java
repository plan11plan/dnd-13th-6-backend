package com.runky.crew.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.runky.crew.domain.Code;
import com.runky.crew.domain.Crew;
import com.runky.crew.domain.CrewCommand;
import com.runky.crew.domain.CrewMemberCount;
import com.runky.crew.domain.CrewRepository;
import com.runky.global.response.ApiResponse;
import com.runky.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CrewApiE2ETest {
    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final CrewRepository crewRepository;

    @Autowired
    public CrewApiE2ETest(TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp,
                          CrewRepository crewRepository) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.crewRepository = crewRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("POST /api/crews")
    class Create {
        final String BASE_URL = "/api/crews";

        @Test
        @DisplayName("크루를 생성한다.")
        void createCrew() {
            long userId = 1L;
            crewRepository.save(CrewMemberCount.of(userId));
            ParameterizedTypeReference<ApiResponse<CrewResponse.Create>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-USER-ID", String.valueOf(userId));
            CrewRequest.Create request = new CrewRequest.Create("Test Crew");

            ResponseEntity<ApiResponse<CrewResponse.Create>> response =
                    testRestTemplate.exchange(BASE_URL, HttpMethod.POST, new HttpEntity<>(request, httpHeaders),
                            responseType);

            Crew crew = crewRepository.findById(response.getBody().getResult().crewId()).orElseThrow();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getResult().crewId()).isEqualTo(crew.getId());
            assertThat(response.getBody().getResult().name()).isEqualTo(crew.getName());
            assertThat(response.getBody().getResult().code()).isEqualTo(crew.getCode().value());
        }
    }

    @Nested
    @DisplayName("POST /api/crews/join")
    class Join {
        final String BASE_URL = "/api/crews/join";

        @Test
        @DisplayName("크루에 참여한다.")
        void joinCrew() {
            Crew crew = Crew.of(new CrewCommand.Create(1L, "Crew"), new Code("abc123"));
            Crew savedCrew = crewRepository.save(crew);
            Long userId = 2L;
            crewRepository.save(CrewMemberCount.of(userId));
            ParameterizedTypeReference<ApiResponse<CrewResponse.Join>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-USER-ID", String.valueOf(userId));
            CrewRequest.Join request = new CrewRequest.Join(crew.getCode().value());

            ResponseEntity<ApiResponse<CrewResponse.Join>> response =
                    testRestTemplate.exchange(BASE_URL, HttpMethod.POST, new HttpEntity<>(request, httpHeaders),
                            responseType);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getResult().crewId()).isEqualTo(savedCrew.getId());
        }
    }

    @Nested
    @DisplayName("GET /api/crews")
    class GetCrews {
        final String BASE_URL = "/api/crews";

        @Test
        @DisplayName("사용자의 크루 목록을 조회한다.")
        void getCrews() {
            long userId = 1L;
            crewRepository.save(CrewMemberCount.of(userId));
            Crew crew1 = crewRepository.save(Crew.of(new CrewCommand.Create(userId, "Crew 1"), new Code("abc123")));
            Crew crew2 = crewRepository.save(Crew.of(new CrewCommand.Create(userId, "Crew 2"), new Code("abc456")));

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-USER-ID", String.valueOf(userId));
            ParameterizedTypeReference<ApiResponse<CrewResponse.Cards>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<CrewResponse.Cards>> response =
                    testRestTemplate.exchange(BASE_URL, HttpMethod.GET, new HttpEntity<>(httpHeaders), responseType);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getResult().crews()).hasSize(2);
            assertThat(response.getBody().getResult().crews())
                    .extracting("crewId")
                    .containsExactlyInAnyOrder(crew1.getId(), crew2.getId());
        }
    }

    @Nested
    @DisplayName("GET /api/crews/{crewId}")
    class GetCrew {
        final String BASE_URL = "/api/crews/{crewId}";

        @Test
        @DisplayName("크루의 상세 정보를 조회한다.")
        void getCrew() {
            long userId = 1L;
            crewRepository.save(CrewMemberCount.of(userId));
            Crew crew = crewRepository.save(Crew.of(new CrewCommand.Create(userId, "Crew"), new Code("abc123")));

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-USER-ID", String.valueOf(userId));
            ParameterizedTypeReference<ApiResponse<CrewResponse.Detail>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<CrewResponse.Detail>> response =
                    testRestTemplate.exchange(BASE_URL, HttpMethod.GET, new HttpEntity<>(httpHeaders), responseType,
                            crew.getId());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getResult().crewId()).isEqualTo(crew.getId());
            assertThat(response.getBody().getResult().name()).isEqualTo(crew.getName());
            assertThat(response.getBody().getResult().code()).isEqualTo(crew.getCode().value());
            assertThat(response.getBody().getResult().memberCount()).isEqualTo(crew.getActiveMemberCount());
            assertThat(response.getBody().getResult().notice()).isEqualTo(crew.getNotice());
        }
    }

    @Nested
    @DisplayName("DELETE /api/crews/{crewId}/members/me")
    class Leave {
        final String BASE_URL = "/api/crews/{crewId}/members/me";

        @Test
        @DisplayName("크루에 속한 사용자가 크루를 탈퇴한다.")
        void leaveCrew() {
            long userId = 1L;
            crewRepository.save(CrewMemberCount.of(userId));
            Crew crew = Crew.of(new CrewCommand.Create(userId, "Crew"), new Code("abc123"));
            crew.joinMember(2L);
            Crew savedCrew = crewRepository.save(crew);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-USER-ID", String.valueOf(2L));
            ParameterizedTypeReference<ApiResponse<CrewResponse.Leave>> responseType = new ParameterizedTypeReference<>() {
            };

            CrewRequest.Leave request = new CrewRequest.Leave(null);
            ResponseEntity<ApiResponse<CrewResponse.Leave>> response =
                    testRestTemplate.exchange(BASE_URL, HttpMethod.DELETE, new HttpEntity<>(request, httpHeaders),
                            responseType,
                            crew.getId());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getResult().crewId()).isEqualTo(crew.getId());
        }
    }

    @Nested
    @DisplayName("GET /api/crews/{crewId}/members")
    class GetCrewMembers {
        final String BASE_URL = "/api/crews/{crewId}/members";

        @Test
        @DisplayName("크루의 멤버 목록을 조회한다.")
        void getCrewMembers() {
            long userId = 1L;
            crewRepository.save(CrewMemberCount.of(userId));
            Crew crew = Crew.of(new CrewCommand.Create(userId, "Crew"), new Code("abc123"));
            crew.joinMember(2L);
            crew.joinMember(3L);
            crew.joinMember(4L);
            crew.leaveMember(4L);
            Crew savedCrew = crewRepository.save(crew);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-USER-ID", String.valueOf(userId));
            ParameterizedTypeReference<ApiResponse<CrewResponse.Members>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<CrewResponse.Members>> response =
                    testRestTemplate.exchange(BASE_URL, HttpMethod.GET, new HttpEntity<>(httpHeaders), responseType,
                            savedCrew.getId());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getResult().members()).hasSize(3);
        }
    }
}