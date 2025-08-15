package com.runky.crew.domain;

import com.runky.crew.error.CrewErrorCode;
import com.runky.global.entity.BaseTimeEntity;
import com.runky.global.error.GlobalErrorCode;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "crew")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private Long activeMemberCount;

    @Version
    private Long version;

    private Crew(Long leaderId, String name, Code code, String notice, Long activeMemberCount) {
        this.leaderId = leaderId;
        this.name = name;
        this.code = code;
        this.notice = notice;
        this.activeMemberCount = activeMemberCount;
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

    public CrewMember joinMember(Long memberId) {
        if (hasHistory(memberId)) {
            CrewMember member = getMemberHistory(memberId);
            member.rejoin();
            incrementActiveMemberCount();
            return member;
        }
        CrewMember crewMember = CrewMember.memberOf(memberId, this);
        add(crewMember);
        return crewMember;
    }

    public CrewMember banMember(Long memberId) {
        CrewMember crewMember = this.members.stream()
                .filter(member -> member.getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND));

        crewMember.ban();
        decrementActiveMemberCount();
        return crewMember;
    }

    public CrewMember leaveMember(Long memberId) {
        CrewMember crewMember = this.members.stream()
                .filter(member -> member.getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND));

        crewMember.leave();
        decrementActiveMemberCount();
        return crewMember;
    }

    public void add(CrewMember crewMember) {
        crewMember.join(this);
        this.members.add(crewMember);
        incrementActiveMemberCount();
    }

    public boolean hasHistory(Long memberId) {
        return this.members.stream()
                .anyMatch(member -> member.getMemberId().equals(memberId));
    }

    public boolean containsMember(Long memberId) {
        return this.members.stream()
                .anyMatch(member -> member.getMemberId().equals(memberId) && member.isInCrew());
    }

    public void incrementActiveMemberCount() {
        if (this.activeMemberCount >= CrewConstants.CREW_CAPACITY.value()) {
            throw new GlobalException(CrewErrorCode.OVER_CREW_MEMBER_COUNT);
        }
        this.activeMemberCount++;
    }

    public void decrementActiveMemberCount() {
        this.activeMemberCount--;
    }

    public CrewMember getLeader() {
        return this.members.stream()
                .filter(member -> member.getRole() == CrewMember.Role.LEADER)
                .findFirst()
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND));
    }

    public CrewMember getMemberHistory(Long memberId) {
        return this.members.stream()
                .filter(member -> member.getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND));
    }

    public CrewMember getMember(Long memberId) {
        return this.members.stream()
                .filter(member -> member.getMemberId().equals(memberId) && member.isInCrew())
                .findFirst()
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND));
    }
}
