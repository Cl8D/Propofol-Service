package propofol.ptfservice.domain.portfolio.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.ptfservice.domain.portfolio.entity.Portfolio;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    // createdBy를 기준으로 portfolio data 가져오기
    @Query("select p from Portfolio p where p.createdBy =:memberId")
    Optional<Portfolio> findPortfolioByCreatedBy(@Param(value = "memberId") String memberId);

}
