package propofol.userservice.api.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;

import java.util.Collections;

// 로그인 요청을 한 사용자의 권한을 확인한다.
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthMemberService implements UserDetailsService {

    private final MemberService memberService;

    // login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어 있는
    // loadUserByUsername 함수가 실행된다.
    // db에 접근하여 사용자의 정보를 가져오는 역할을 한다.
    /*
        security session에는 Authentication 객체가 있으며,
        이는 userDetails를 받게 된다.
        약간 securitySession(Authentication(userDetails)) 같은 느낌.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member findMember = memberService.getMemberByEmail(username);

        if(findMember == null) {
            throw new NotFoundMember("없는 계정입니다.");
        }

        // 해당 사용자가 가지고 있는 권한을 의미한다.
        // 스프링에서는 grantedAuthority 인터페이스를 구현한 클래스 객체로 만들어주는데,
        // 이중에서 simpleGrantedAuthority 클래스를 사용할 수 있다.
        GrantedAuthority grantedAuthority
                // 계정이 가지고 있는 권한을 파라미터로 전달해주기
                = new SimpleGrantedAuthority(findMember.getAuthority().toString());

        // UserDetails의 구현체인 user 객체를 리턴해줄 수 있다.
        // 이때 파라미터로 계정의 이름, 패스워드, 권한 목록을 리턴해준다.
        // 이때 권한을 넘겨줄 때 singleton으로 넘겨주면... 권한 객체 한 개만 넘겨줄 수 있다 (싱글톤)
        return new User(findMember.getEmail(), findMember.getPassword(),
                Collections.singleton(grantedAuthority));
    }
}
