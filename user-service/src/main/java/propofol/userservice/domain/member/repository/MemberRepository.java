package propofol.userservice.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propofol.userservice.domain.member.entity.Member;

import java.util.Optional;

// Member Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일을 통해 멤버 조회
    Optional<Member> findByEmail(String email);

    // 이메일을 통해 회원 존재 여부 확인 (Oauth 사용자) + 중복 체크
    Member findExistByEmail(String email);

    // 닉네임을 통해 회원 존재 여부 확인 (중복 체크)
    Member findExistByNickname(String nickname);

    // refreshToken으로 멤버 찾기
    Optional<Member> findByRefreshToken(String refreshToken);
}
