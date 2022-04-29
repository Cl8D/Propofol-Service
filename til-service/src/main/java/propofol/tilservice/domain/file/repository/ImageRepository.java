package propofol.tilservice.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.tilservice.domain.file.entity.Image;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    // 동일한 게시글의 id를 가지고 있는 image 리스트를 디비에서 가져오기.
    // 즉, 하나의 게시글에 올라온 이미지 목록을 가져오도록!
    @Query("select i from Image i where i.board.id =:boardId")
    List<Image> findImages(@Param(value = "boardId") Long boardId);
}
