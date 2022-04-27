package propofol.tilservice.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import propofol.tilservice.domain.board.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    // 빌더 생성
    @Builder(builderMethodName = "createBoard")
    public Board(String title, String content, Integer recommend, Boolean open) {
        this.title = title;
        this.content = content;
        this.recommend = recommend;
        this.open = open;
    }

    // 글 수정을 위한 메서드
    public void updateBoard(String title, String content, Boolean open) {
        if(title!=null) this.title =title;
        if(content!=null) this.content = content;
        this.open = open;
    }
}
