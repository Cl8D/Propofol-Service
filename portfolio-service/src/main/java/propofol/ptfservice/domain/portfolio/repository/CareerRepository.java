package propofol.ptfservice.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import propofol.ptfservice.domain.portfolio.entity.Career;

public interface CareerRepository extends JpaRepository<Career, Long> {
    @Transactional
    @Modifying
    @Query("delete from Career c where c.portfolio.id =:portfolioId")
    int bulkDeleteAll(@Param(value = "portfolioId") Long portfolioId);
}
