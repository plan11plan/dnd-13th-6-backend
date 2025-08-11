package com.runky.crew.infrastructure;

import com.runky.crew.domain.CrewMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewMemberJpaRepository extends JpaRepository<CrewMember, Long> {
    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);

    List<CrewMember> findByMemberId(Long memberId);
}
