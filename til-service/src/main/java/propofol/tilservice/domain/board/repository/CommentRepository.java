package propofol.tilservice.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.tilservice.domain.board.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 페이징으로 댓글 가져오기
    // boardId가 일치하면서, group by를 통해 comment를 groupId가 같은 애들끼리 그룹화를 해준다.
    // 이때, c.id는 PK 값으로 꼭 넣어줘야 잘 가져온다...
    // order by는 정렬 조건으로, groupId를 기준으로 1차적으로 오름차순 정렬, 2차적으로 하나의 그룹에 대해 id 값을 기준으로 오름차순 정렬을 진행해준다!
//    @Query("select c from Comment c where c.board.id=:boardId group by c.groupId, c.id order by c.groupId, c.id desc") // 다중조건
    @Query("select c from Comment c where c.board.id=:boardId group by c.groupId, c.id order by c.groupId, c.id asc")
    Page<Comment> findPageComments(@Param(value = "boardId") Long boardId, Pageable pageable);


    // 댓글 벌크 삭제 기능
    @Modifying
    @Query("delete from Comment c where c.board.id=:boardId")
    int deleteBulkComments(@Param("boardId") Long boardId);


    // 댓글 개수 조회
    @Query("select count(c) from Comment c where c.board.id=:boardId")
    int getCommentCount(@Param("boardId") Long boardId);
}
