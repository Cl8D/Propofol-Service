package propofol.tagservice.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("select t from Tag t")
    Page<Tag> findPageTags(final Pageable pageable);

    @Query("select t from Tag t where t.name like :keyword%")
    Page<Tag> findSliceTags(final Pageable pageable, @Param(value = "keyword") final String keyword);

    @Query("select t from Tag t where t.id in :ids")
    List<Tag> findByIds(@Param("ids") final Collection<Long> ids);

    @Query("select t from Tag t where t.id in :ids")
    List<Tag> findBySetIds(@Param("ids") final Collection<Long> ids);
}
