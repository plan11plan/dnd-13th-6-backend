package com.runky.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.runky.utils.DatabaseCleanUp;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CrewServiceIntegrationTest {

    @Autowired
    private CrewService crewService;
    @Autowired
    private CrewRepository crewRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("크루 생성 시,")
    class Create {
        @Test
        @DisplayName("속한 크루 개수가 증가한다.")
        void incrementCrewCount() {
            crewRepository.save(CrewMemberCount.of(1L));
            CrewCommand.Create command = new CrewCommand.Create(1L, "Test Crew");

            crewService.create(command);

            CrewMemberCount crewMemberCount = crewRepository.findCountByMemberId(1L).orElseThrow();
            assertThat(crewMemberCount.getCrewCount()).isEqualTo(1L);
        }

        @Test
        @DisplayName("생성한 사용자는 크루의 리더가 된다.")
        void createLeader() {
            crewRepository.save(CrewMemberCount.of(1L));
            CrewCommand.Create command = new CrewCommand.Create(1L, "Test Crew");

            Crew crew = crewService.create(command);

            CrewMember crewMember = crewRepository.findByCrewAndMember(crew.getId(), 1L).orElseThrow();
            assertThat(crew.getLeaderId()).isEqualTo(crewMember.getMemberId());
            assertThat(crewMember.isLeader()).isTrue();
        }
    }

    @Nested
    @DisplayName("동시성 테스트")
    class Concurrency {
        @Test
        @DisplayName("동시에 크루 생성을 시도할 경우, 낙관적 락에 의한 실패 케이스가 존재한다.")
        void createOnlyOneCrew_withConcurrency() throws InterruptedException {
            crewRepository.save(CrewMemberCount.of(1L));
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            CountDownLatch start = new CountDownLatch(1);

            for (int i = 0; i < threadCount; i++) {
                CrewCommand.Create command = new CrewCommand.Create(1L, "Test Crew " + i);
                executor.submit(() -> {
                    try {
                        start.await();
                        crewService.create(command);
                    } catch (Exception e) {
                        System.out.println("실패: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
            start.countDown();
            latch.await();


            CrewMemberCount crewMemberCount = crewRepository.findCountByMemberId(1L).orElseThrow();
            List<CrewMember> crewMembers = crewRepository.findCrewMemberOfUser(1L);
            assertThat(crewMembers).hasSizeLessThan(6);
            assertThat(crewMemberCount.getCrewCount()).isLessThan(6);
        }
    }
}