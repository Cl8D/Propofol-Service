package propofol.userservice.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.repository.MemberRepository;
import propofol.userservice.domain.member.service.dto.UpdateMemberDto;

import java.util.Optional;

// Member Service 구현체
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    // id를 통해 멤버 조회 - throw는 controller단에서 처리
    @Override
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    // 이메일을 통해 멤버 조회 (optional)
    // 회원 조회 실패 시 notFoundMember Exception
    @Override
    public Member getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> {
            throw new NotFoundMember("해당 회원을 찾을 수 없습니다.");
        });
        return member;
    }

    // 닉네임을 통해 멤버 조회
    @Override
    public Boolean checkDuplicateByNickname(String nickname) {
        // 이미 멤버가 존재한다면 true, 없으면 false
        Member findMember = memberRepository.findDuplicateByNickname(nickname);
        if(findMember == null) return false;
        return true;
    }

    @Override
    public Boolean checkDuplicateByEmail(String email) {
        Member findMember = memberRepository.findDuplicateByEmail(email);
        if(findMember == null) return false;
        return true;
    }

    // 회원 가입 - db에 저장
    @Override
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

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
}
