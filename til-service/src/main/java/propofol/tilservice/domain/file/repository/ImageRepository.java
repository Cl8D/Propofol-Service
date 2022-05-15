package propofol.tilservice.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.tilservice.domain.file.entity.Image;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    // 동일한 게시글의 id를 가지고 있는 image 리스트를 디비에서 가져오기.
    // 즉, 하나의 게시글에 올라온 이미지 목록을 가져오도록!
//    @Query("select i from Image i where i.board.id =:boardId")
//    List<Image> findImages(@Param(value = "boardId") Long boardId);

    // 이미지 벌크 삭제
//    @Modifying
//    @Query("delete from Image i where i.board.id=:boardId")
//    int deleteBulkImages(@Param(value = "boardId") Long boardId);


    // 서버 저장 파일 이름으로 이미지 찾기
//    Image findImageByStoreFileName(String storeFileName);

    /****************/

    // 전체 이미지 리스트에서 서버에 저장된 파일 이름으로 이미지 리스트 찾기
    // 클라이언트가 하나의 게시글에 대한 이미지 리스트를 한 번에 보내주니까, 그거에 대한 게시판 정보를 넣기 위해서
    // DB에 저장된 전체 이미지 목록에서 해당 파일 이름과 일치하는 이미지들을 보내줘야 함!!
    // JPQL에서는 in 키워드를 지원하며, List나 Array 타입을 파라미터로 넘겨주면 된다
    // collection 타입으로 받아오는 게 정석!
    @Query("select i from Image i where i.storeFileName in :names")
    List<Image> findImagesInNames(@Param(value = "names") Collection<String> names);

    /****************/

    // 썸네일 이미지 뽑기 - JPA에서 제공하는 Top 사용
    Optional<Image> findTopByBoardId(Long boardId);

    /****************/

    // 게시글에 존재하는 이미지 목록 전부 가져오기
    List<Image> findAllByBoardId(Long boardId);
}
