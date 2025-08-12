package com.runky.crew.infrastructure;

import com.runky.crew.domain.Crew;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewJpaRepository extends JpaRepository<Crew, Long> {
}
