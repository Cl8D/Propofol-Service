package propofol.userservice.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.userservice.api.auth.controller.dto.UpdatePasswordRequestDto;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.repository.MemberRepository;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;

import java.util.Optional;

// Member Service 구현체
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    /*****************/

    // id를 통해 멤버 조회 - throw는 controller단에서 처리
    @Override
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    /*****************/

    // 이메일을 통해 멤버 조회 (optional)
    // 회원 조회 실패 시 notFoundMember Exception
    @Override
    public Member getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
            throw new NotFoundMember("해당 회원을 찾을 수 없습니다.");
        });
        return member;
    }

    /*****************/


    // 회원 가입 - db에 저장
    @Override
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    /*****************/

    // 회원 정보 수정
    @Transactional
    @Override
    public void updateMember(UpdateMemberDto dto, Long id) {
        Member findMember = memberRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundMember("회원을 찾을 수 없습니다.");
        });

        String password = dto.getPassword();
        String degree = dto.getDegree();
        String nickname = dto.getNickname();
        String score = dto.getScore();
        String phoneNumber = dto.getPhoneNumber();

        // 패스워드 암호화
        if(password != null) {
            password = encoder.encode(password);
        }

        findMember.update(nickname, password, score, degree, phoneNumber);

    }

    /*****************/
    // 회원 존재 여부 체크
    @Override
    public Boolean isExistByEmail(String email) {
        Member findMember = memberRepository.findExistByEmail(email);
        // 회원이 null이면 신규 유저니까 false, 기존에 존재한다면 true 리턴
        return findMember == null ? false : true;
    }

    /*****************/
    // 닉네임 존재 여부 체크
    @Override
    public Boolean isExistByNickname(String nickname) {
        Member findMember = memberRepository.findExistByNickname(nickname);
        return findMember == null ? false : true;
    }

    /*****************/
    // 비밀번호 변경
    @Override
    @Transactional
    public void updatePassword(String email, String password) {
        Member findMember = getMemberByEmail(email);
        findMember.updatePassword(encoder.encode(password));
    }

    /*****************/

    // RefreshToken을 가진 member 찾기
    @Override
    public Member getRefreshMember(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken).orElseThrow(() -> {
            throw new NotFoundMember("올바르지 않은 refresh Token입니다. (DB 불일치)");
        });
    }

    /*****************/

    // refreshToken 변경하기
    @Transactional
    @Override
    public void changeRefreshToken(Member findMember, String refreshToken) {
        findMember.changeRefreshToken(refreshToken);
    }
}
