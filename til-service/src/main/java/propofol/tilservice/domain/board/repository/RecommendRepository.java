package propofol.tilservice.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.tilservice.domain.board.entity.Recommend;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {

    // @Query 어노테이션으로 작성된 변경, 삭제 쿼리 메서드 사용 시 (insert/update/delete)
    // 특히, 벌크 연산에서는 @Modifying을 사용한다.
    // 벌크 연산은 1차 캐시를 포함한 영속성 컨텍스트를 무시하고 바로 @Query를 실행하게 된다.
    /** @Modifying(clearAutomatically=true)로 설정하면 벌크 연산 직후 영속성 컨텍스트 비워줌!
     * -> 단, 우리 프로젝트에서는 트랜잭션 하나에 하나의 흐름만 있어서 영속성 컨텍스트를 비우기 위한 insert 쿼리가 불필요하게 나간다고 판단. -> 적용X*/
    @Modifying
    @Query(value = "delete from Recommend r where r.board.id =:boardId")
    int deleteBulkRecommends(@Param(value = "boardId") Long boardId);
}
