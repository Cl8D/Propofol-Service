package propofol.tilservice.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import propofol.tilservice.domain.board.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id", updatable = false)
    private Board board;

    // 가장 상위 댓글(부모)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Comment parent;

    // 대댓글 (자식)
    @OneToMany(mappedBy = "parent")
    private List<Comment> childList = new ArrayList<>();

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public void addBoard(Board board) {
        this.board = board;
    }

    @Builder(builderMethodName = "createComment")
    public Comment(String content, Board board) {
        this.content = content;
        this.board = board;
    }



}
