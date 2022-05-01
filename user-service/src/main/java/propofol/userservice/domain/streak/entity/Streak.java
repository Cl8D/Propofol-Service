package propofol.userservice.domain.streak.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import propofol.userservice.domain.member.entity.Member;

import javax.persistence.*;
import java.time.LocalDate;

// 스트릭 엔티티. 하나의 스트릭에는 작업 날짜 + 그 날짜에 몇 개의 작업을 했는지 들어간다.
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Getter
public class Streak {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="streak_id")
    private Long id;

    // 작업을 한 날짜
    private LocalDate workingDate;

    // 작업 횟수
    private Integer working;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    public void addMember(Member member){
        this.member = member;
    }

    public void addWorking(Integer working) {
        this.working = working;
    }

    @Builder(builderMethodName = "createStreak")
    public Streak(LocalDate workingDate, Integer working) {
        this.workingDate = workingDate;
        this.working = working;
    }
}
