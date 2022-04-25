package propofol.userservice.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propofol.userservice.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일을 통해 멤버 조회
    Optional<Member> findByEmail(String email);

    // 닉네임을 통해 회원 조회 (중복 체크를 위함)
    Member findDuplicateByNickname(String nickname);
    // 이메일을 통해 회원 조회 (중복 체크를 위함)
    Member findDuplicateByEmail(String email);
}