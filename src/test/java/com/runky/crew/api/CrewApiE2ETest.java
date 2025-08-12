package com.runky.crew.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.runky.crew.domain.Crew;
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
}