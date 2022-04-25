package propofol.userservice.domain.member.service;

import propofol.userservice.domain.member.entity.Member;

public interface MemberService {
    // 이메일을 통해 멤버 조회
    Member getMemberByEmail(String email);

    // 닉네임, 이메일을 통해 회원 중복 조회 체크
    Boolean checkDuplicateByNickname(String nickname);
    Boolean checkDuplicateByEmail(String email);

    // 회원가입 로직
    void saveMember(Member member);
}
