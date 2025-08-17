package com.runky.crew.domain;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.error.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrewLeaderService {

    private final CrewRepository crewRepository;

    @Transactional
    public Crew updateNotice(CrewCommand.UpdateNotice command) {
        Crew crew = crewRepository.findById(command.crewId())
                .orElseThrow(() -> new GlobalException(CrewErrorCode.NOT_FOUND_CREW));
        if (!crew.isLeader(command.userId())) {
            throw new GlobalException(CrewErrorCode.NOT_CREW_LEADER);
        }
        crew.updateNotice(command.notice());
        return crew;
    }

    @Transactional
    public Crew updateName(CrewCommand.UpdateName command) {
        Crew crew = crewRepository.findById(command.crewId())
                .orElseThrow(() -> new GlobalException(CrewErrorCode.NOT_FOUND_CREW));
        if (!crew.isLeader(command.userId())) {
            throw new GlobalException(CrewErrorCode.NOT_CREW_LEADER);
        }
        crew.updateName(command.name());
        return crew;
    }
}
