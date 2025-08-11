package com.runky.crew.domain;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.error.GlobalException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;

@Entity
@Getter
@Table(name = "member_crew_count")
public class CrewMemberCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_member_id", nullable = false, unique = true)
    private Long memberId;

    @Column(name = "crew_count", nullable = false)
    private Long crewCount;

    @Version
    private Long version;

    protected CrewMemberCount() {
    }

    private CrewMemberCount(Long memberId, Long crewCount) {
        this.memberId = memberId;
        this.crewCount = crewCount;
    }

    public static CrewMemberCount of(Long userId) {
        return new CrewMemberCount(userId, 0L);
    }

    public boolean isOver() {
        return crewCount >= 5;
    }

    public void increment() {
        if (crewCount >= 5) {
            throw new GlobalException(CrewErrorCode.OVER_CREW_COUNT);
        }
        crewCount++;
    }
}
