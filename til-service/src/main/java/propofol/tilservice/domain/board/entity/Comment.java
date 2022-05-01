package propofol.tilservice.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import propofol.tilservice.domain.board.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
public class Comment extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    // 댓글 내용
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id", updatable = false)
    private Board board;

    // 닉네임
    private String nickname;

    // 댓글 그룹Id 지정 (하나의 부모댓글 - 여러 개의 자식댓글은 같은 그룹이 된다)
    private Long groupId;

    /** 댓글-대댓글 구현 시 페이징을 위해 자기참조 -> 그룹 설정 형태로 변경 */
//    // 가장 상위 댓글(부모)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="parent_id")
//    private Comment parent;
//
//    // 대댓글 (자식)
//    @OneToMany(mappedBy = "parent")
//    private List<Comment> childList = new ArrayList<>();
//
//    public void setParent(Comment parent) {
//        this.parent = parent;
//    }

    public void addBoard(Board board) {
        this.board = board;
    }

    public void addGroupInfo(Long groupId) {
        this.groupId = groupId;
    }

    @Builder(builderMethodName = "createComment")
    public Comment(String content, Board board, String nickname) {
        this.content = content;
        this.board = board;
        this.nickname = nickname;
    }
}
