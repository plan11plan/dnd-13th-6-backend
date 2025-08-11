package com.runky.crew.domain;

import java.util.List;
import java.util.Optional;

public interface CrewRepository {

    Crew save(Crew crew);

    Optional<Crew> findById(Long crewId);

    Optional<CrewMember> findByCrewAndMember(Long crewId, Long memberId);

    List<CrewMember> findCrewMemberOfUser(Long memberId);

    CrewMemberCount save(CrewMemberCount crewMemberCount);

    Optional<CrewMemberCount> findCountByMemberId(Long memberId);
}
