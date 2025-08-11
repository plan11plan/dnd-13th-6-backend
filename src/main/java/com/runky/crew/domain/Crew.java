package com.runky.crew.domain;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.entity.BaseTimeEntity;
import com.runky.global.error.GlobalException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Table(name = "crew")
@Getter
public class Crew extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "leader_id", nullable = false)
    private Long leaderId;

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CrewMember> members = new ArrayList<>();

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private Code code;

    @Column(name = "notice")
    private String notice;

    @Column(name = "member_count", nullable = false)
    private Long memberCount;

    @Version
    private Long version;

    protected Crew() {
    }

    private Crew(Long leaderId, String name, Code code, String notice, Long memberCount) {
        this.leaderId = leaderId;
        this.name = name;
        this.code = code;
        this.notice = notice;
        this.memberCount = memberCount;
    }

    public static Crew of(CrewCommand.Create command, Code code) {
        if (command.name().isBlank()) {
            throw new GlobalException(CrewErrorCode.BLANK_CREW_NAME);
        }
        if (command.name().length() > CrewConstants.MAX_CREW_NAME_LENGTH.value()) {
            throw new GlobalException(CrewErrorCode.OVER_CREW_NAME);
        }

        Crew crew = new Crew(command.userId(), command.name(), code, "", 0L);
        crew.add(CrewMember.leaderOf(crew));
        return crew;
    }

    public void add(CrewMember crewMember) {
        this.members.add(crewMember);
        crewMember.join(this);
        this.memberCount++;
    }
}
