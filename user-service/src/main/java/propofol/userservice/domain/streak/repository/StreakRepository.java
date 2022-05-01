package propofol.userservice.domain.streak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propofol.userservice.domain.streak.entity.Streak;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StreakRepository extends JpaRepository<Streak, Long> {
    // member_id (db에 저장된 형태)로 검색 + workingdate를 기준으로 start~end 사이에 있는 값만 검색하기
    /** TODO 쿼리 확인하기 */
    // select s from Streak s where s.member.id=:memberId and s.workingdate start and end; 이런 식으로 나가려나...?
    List<Streak> findByMember_IdAndWorkingDateBetween(Long memberId, LocalDate start, LocalDate end);

    // select s from Streak s where s.member.id=:memberId and s.workingDate=:workingDate 느낌...?
    Optional<Streak> findByMember_IdAndWorkingDate(Long memberId, LocalDate workingDate);

}
