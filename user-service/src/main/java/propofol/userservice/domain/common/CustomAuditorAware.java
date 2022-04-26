package propofol.userservice.domain.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;

import java.util.Optional;

// BaseEntity의 생성자, 수정자 자동 주입하는 기능 추가
@Component
@RequiredArgsConstructor
public class CustomAuditorAware implements AuditorAware<String> {
    private final MemberService memberService;

    @Override
    public Optional<String> getCurrentAuditor() {
        // 현재 작업 대상을 가져오기.
        // 여기서는 Authentication 객체의 getName -> 사용자의 Db의 pk값인 id를 가져왔다.
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        // SecurityContext에서 인증되지 않은 사용자는 anonymousUser로 가져온다.
        // 인증이 없는 사용자는 로그인, 회원가입을 할 수 있는데, 그중 DB에 영향을 끼치는 건 회원가입.
        // 회원가입을 한 사용자 > DB에 영향을 미침 > Master라고 지정 (그냥 이름을 master로 지정)
        if(name.equals("anonymousUser"))
            return Optional.ofNullable("MASTER");

        else {
            Member member = memberService.getMemberById(Long.valueOf(name))
                    .orElseThrow(() -> {
                        throw new NotFoundMember("존재하지 않는 회원입니다.");
                    });

            // 누가 DB를 수정했는지 리턴 (수정 > jwt 토큰이 있어야만 수정이 가능 > 해당 사용자의 Email 리턴)
            return Optional.ofNullable(member.getEmail());
        }
    }
}
