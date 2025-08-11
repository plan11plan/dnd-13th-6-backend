package com.runky.crew.domain;

import com.runky.global.error.GlobalErrorCode;
import com.runky.global.error.GlobalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewService {
    private final CrewRepository crewRepository;
    private final CodeGenerator codeGenerator;

    @Transactional
    public Crew create(CrewCommand.Create command) {
        CrewMemberCount crewMemberCount = crewRepository.findCountByMemberId(command.userId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND));
        crewMemberCount.increment();

        Crew crew = Crew.of(command, codeGenerator.generate());
        return crewRepository.save(crew);
    }
}
