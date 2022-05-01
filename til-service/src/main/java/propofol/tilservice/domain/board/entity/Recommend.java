package propofol.tilservice.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

// 추천에 대한 엔티티 생성
// 어떤 멤버가 어떤 게시글에 추천을 눌렀는지에 대한 정보 저장
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
public class Recommend {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommend_id")
    private Long id;

    // fetch Type은 LAZY로 설정해주기.
    @ManyToOne(fetch = FetchType.LAZY)
    // 연관관계의 주인 설정 (fk)
    @JoinColumn(name = "board_id", updatable = false)
    private Board board;

    // 추천한 멤버
    private String memberId;

    // 연관관계 주입
    public void addBoard(Board board) {
        this.board = board;
    }

    // 추천인 정보 저장
    @Builder(builderMethodName = "createRecommend")
    public Recommend (String memberId) {
        this.memberId = memberId;
    }




}
