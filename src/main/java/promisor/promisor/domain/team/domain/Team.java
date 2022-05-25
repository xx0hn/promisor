package promisor.promisor.domain.team.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import promisor.promisor.domain.model.BaseEntity;
import promisor.promisor.domain.place.domain.Place;
import promisor.promisor.domain.member.domain.Member;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Group 도메인 객체를 나타내는 자바 빈
 *
 * @author Sanha Ko
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 30)
    private String groupName;

    @Lob
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private final List<TeamMember> teamMembers = new ArrayList<>();

    private LocalDateTime date;

    public Team(Member member, String groupName) {
        super("ACTIVE");
        this.member = member;
        this.groupName = groupName;
    }

    public void changeGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void changeLeader(Member member){
        this.member = member;
    }
}
