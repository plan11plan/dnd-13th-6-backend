package com.runky.crew.infrastructure;

import com.runky.crew.domain.Code;
import com.runky.crew.domain.Crew;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewJpaRepository extends JpaRepository<Crew, Long> {

    Optional<Crew> findByCode(Code code);
}
