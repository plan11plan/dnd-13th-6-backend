package com.runky.crew.domain;

import com.runky.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "crew_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_crew_id", nullable = false)
    private Crew crew;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    private CrewMember(Long memberId, Crew crew, Role role) {
        this.memberId = memberId;
        this.crew = crew;
        this.role = role;
    }

    public static CrewMember leaderOf(Crew crew) {
        return new CrewMember(crew.getLeaderId(), crew, Role.LEADER);
    }

    public void join(Crew crew) {
        this.crew = crew;
    }

    public boolean isLeader() {
        return this.role == Role.LEADER;
    }

    public enum Role {
        LEADER,
        MEMBER
    }
}
