package propofol.userservice.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.userservice.domain.member.entity.Following;

import java.util.Optional;

public interface FollowingRepository extends JpaRepository<Following, Long> {

    // followingId와 memberId가 일치하는 following 정보 가져오기
    Optional<Following> findByFollowingMemberIdAndMemberId(Long followingMemberId, Long MemberId);

    // 팔로잉 카운트 쿼리 - 팔로우된 사용자의 id가 회원의 id인 경우 찾아오기
    @Query("select count(f) from Following f where f.followingMemberId=:memberId")
    int getFollowers(@Param("memberId") Long memberId);
}
