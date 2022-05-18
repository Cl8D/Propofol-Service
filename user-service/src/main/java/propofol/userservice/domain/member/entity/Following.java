package propofol.userservice.domain.member.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Following {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="following_id")
    private Long id;

    private Long followingMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    public void changeMember(Member member) {
        this.member = member;
    }

    @Builder(builderMethodName = "createFollowing")
    public Following(Long followingMemberId) {
        this.followingMemberId = followingMemberId;
    }
}
