package propofol.tilservice.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import propofol.tilservice.domain.board.BaseEntity;
import propofol.tilservice.domain.file.entity.Image;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Board extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="board_id")
    private Long id;

    // 글 제목
    @Column(nullable = false)
    private String title;

    // 글 내용
    @Column(nullable = false)
    private String content;

    // 추천 수
    private Integer recommend;

    // 공개 여부 설정
    @Column(nullable = false)
    private Boolean open;

    // 하나의 게시글에는 여러 개의 추천이 있을 수 있음
    @OneToMany(mappedBy = "board")
    private List<Recommend> recommends = new ArrayList<>();


    // 파일 저장
    // 하나의 게시글에는 여러 개의 파일이 들어갈 수 있다.
    // 여기서 cascade 옵션을 사용하였는데,
    // 이러면 영속성 객체에 수행하는 행동이 자식까지 전파된다.
    // 즉, 여기서는 board 객체가 변경감지에 의해 체크되면 image도 함께 변경되는 것.
    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST)
    List<Image> images = new ArrayList<>();


    // 하나의 게시글에는 여러 개의 댓글이 달릴 수 있다.
    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST)
    List<Comment> comments = new ArrayList<>();

    public void addImage(Image image) {
        images.add(image);
        image.addBoard(this);
    }

    // 빌더 생성
    @Builder(builderMethodName = "createBoard")
    public Board(String title, String content, Integer recommend, Boolean open) {
        this.title = title;
        this.content = content;
        this.recommend = recommend;
        this.open = open;
    }

    // 양방향 연관관계 주입
    public void addRecommend(Recommend recommend) {
        recommend.addBoard(this);
        recommends.add(recommend);
    }

    // 추천 수 증가 (1번 클릭)
    public void setRecommendUp() {
        this.recommend = this.recommend + 1;
    }

    // 추천 수 감소 (1번 더 클릭 시)
    public void setRecommendDown() {
        this.recommend = this.recommend - 1;
    }

    // 글 수정을 위한 메서드
    public void updateBoard(String title, String content, Boolean open) {
        if(title!=null) this.title =title;
        if(content!=null) this.content = content;
        this.open = open;
    }
}
