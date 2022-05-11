package propofol.userservice.domain.member.service;

import propofol.userservice.api.auth.controller.dto.UpdatePasswordRequestDto;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;

import java.util.Optional;

// Member Service
public interface MemberService {
    // ID로 멤버 조회
    Optional<Member> getMemberById(Long id);

    // 이메일을 통해 멤버 조회
    Member getMemberByEmail(String email);

    // 회원가입 로직
    void saveMember(Member member);

    // 회원 정보 수정 로직
    void updateMember(UpdateMemberDto updateMemberDto, Long id);

    // 이메일 존재 여부 확인 (Oauth 사용자) + 중복 체크
    Boolean isExistByEmail(String email);

    // 닉네임 존재 여부 확인 (중복 체크_
    Boolean isExistByNickname(String nickname);

    // 비밀번호 변경
    void updatePassword(String email, String password);

    // refreshToken을 가진 member 찾기
    Member getRefreshMember(String refreshToken);

    // refreshToken 변경
    void changeRefreshToken(Member findMember, String refreshToken);
}
