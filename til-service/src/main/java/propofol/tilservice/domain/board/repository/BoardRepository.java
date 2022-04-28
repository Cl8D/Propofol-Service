package propofol.tilservice.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.domain.board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 페이지로 나눠서 게시글 가져오기
    // 이때, Query를 직접 작성해줘서 :N으로 연결된 정보를 제외하고
    // 오직 게시글에 대한 정보만 가져올 수 있도록 해준다!
    @Query("select b from Board b")
    @Transactional(readOnly = true)
    Page<Board> findAll(Pageable pageable);

    // 내가 쓴 글 조회
    // createdBy(=글 작성자)와 일치하는 게시글 목록을 페이지 형태로 가져오기
    @Query(countQuery = "select b from Board b where b.createdBy =:createdBy")
    @Transactional(readOnly = true)
    Page<Board> findPagesByCreatedBy(Pageable pageable,
                                     @Param("createdBy") String createdBy);

}
