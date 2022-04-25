package propofol.userservice.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.repository.MemberRepository;

import java.util.Optional;

// Member Service 구현체
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

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
}
