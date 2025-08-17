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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member_crew_count")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private CrewMemberCount(Long memberId, Long crewCount) {
        this.memberId = memberId;
        this.crewCount = crewCount;
    }

    public static CrewMemberCount of(Long userId) {
        return new CrewMemberCount(userId, 0L);
    }

    public boolean isOver() {
        return crewCount >= CrewConstants.MAX_CREW_COUNT.value();
    }

    public void increment() {
        if (isOver()) {
            throw new GlobalException(CrewErrorCode.OVER_CREW_COUNT);
        }
        crewCount++;
    }

    public void decrement() {
        if (crewCount <= 0) {
            throw new GlobalException(CrewErrorCode.NOT_IN_CREW);
        }
        crewCount--;
    }
}
