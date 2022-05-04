package propofol.tagservice.domain.tag.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import propofol.tagservice.domain.tag.entity.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // 태그 이름으로 태그 찾기
    Optional<Tag> findByName(String tagName);

    // 페이지 단위 태그 조회
    @Transactional(readOnly = true)
    @Query("select t from Tag t")
    Page<Tag> findPageTags(Pageable pageable);

    @Transactional(readOnly = true)
    @Query("select t from Tag t where t.name like :keypoint%")
    Page<Tag> findSliceTags(Pageable pageable, @Param(value = "keypoint") String keypoint);
}
