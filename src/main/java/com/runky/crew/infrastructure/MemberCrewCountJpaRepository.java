package com.runky.crew.infrastructure;

import com.runky.crew.domain.CrewMemberCount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCrewCountJpaRepository extends JpaRepository<CrewMemberCount, Long> {
    Optional<CrewMemberCount> findByMemberId(Long memberId);
}
