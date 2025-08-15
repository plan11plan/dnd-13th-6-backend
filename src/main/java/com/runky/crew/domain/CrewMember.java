package com.runky.crew.domain;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.entity.BaseTimeEntity;
import com.runky.global.error.GlobalException;
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

    public static CrewMember memberOf(Long memberId, Crew crew) {
        return new CrewMember(memberId, crew, Role.MEMBER);
    }

    public void join(Crew crew) {
        this.crew = crew;
    }

    public void rejoin() {
        if (isBanned()) {
            throw new GlobalException(CrewErrorCode.BANNED_MEMBER);
        }
        if (isInCrew()) {
            throw new GlobalException(CrewErrorCode.ALREADY_IN_CREW);
        }
        this.role = Role.MEMBER;
    }

    public void ban() {
        this.role = Role.BANNED;
    }

    public void leave() {
        this.role = Role.LEFT;
    }

    public void demote() {
        if (!isLeader()) {
            throw new GlobalException(CrewErrorCode.NOT_CREW_LEADER);
        }
        this.role = Role.MEMBER;
    }

    public void promote() {
        if (this.role != Role.MEMBER) {
            throw new GlobalException(CrewErrorCode.NOT_CREW_MEMBER);
        }
        this.role = Role.LEADER;
    }

    public boolean isLeader() {
        return this.role == Role.LEADER;
    }

    public boolean isBanned() {
        return this.role == Role.BANNED;
    }

    public boolean isInCrew() {
        return this.role == Role.LEADER || this.role == Role.MEMBER;
    }

    public enum Role {
        LEADER,
        MEMBER,
        LEFT,
        BANNED
    }
}
