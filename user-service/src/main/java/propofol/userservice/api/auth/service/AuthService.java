package propofol.userservice.api.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.userservice.api.auth.controller.dto.LoginRequestDto;
import propofol.userservice.api.common.exception.dto.ErrorDto;
import propofol.userservice.api.common.jwt.JwtProvider;
import propofol.userservice.api.common.jwt.TokenDto;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.repository.MemberRepository;
import propofol.userservice.domain.member.service.MemberService;

import javax.servlet.http.HttpServletResponse;

// 로그인한 사용자 검증 및 Jwt 토큰 반환
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    // JWT 토큰 생성기
    private final JwtProvider jwtProvider;
    // Authentication 객체를 받아 인증하고, 인증되었다면 Authentication 객체를 돌려주는 메서드를 구현하도록 하는 인터페이스
    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;

    // refreshToken 정보를 DB에 반영하기 위해 @Transactional 걸어주기.
    @Transactional
    public Object propofolLogin(LoginRequestDto loginDto, HttpServletResponse response) {

        // username, password를 쓰는 form 기반 인증을 처리하는 필터 (아이디-패스워드 인증 담당)
        // Authentication 인터페이스의 구현체이다.

        // authenticationManager를 통해 인증을 실행하며,
        // 성공 시 Authentication 객체를 SecurityContext에 저장한 뒤 AuthenticationSuccessHandler 실행
        // 실패 시 AuthenticationFailureHandler 실행
        UsernamePasswordAuthenticationToken authenticationToken
                // email-password 정보를 통해 UsernamePasswordAuthenticationToken(Authentication)을 생성
                = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        try {
            // token을 활용하여 인증이 성공하면 authenticate() 메서드의 리턴 값인 Authentication 객체를 반환한다.
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            // 객체를 활용하여 jwt 토큰을 생성해준다. + refreshToken도 함께 DB에 저장하기
            TokenDto tokenDto = jwtProvider.createJwt(authenticate);
            saveRefreshToken(authenticate, tokenDto);
            return tokenDto;

        } catch (Exception e) {
            // 만약 인증을 실패하면
            log.info("login Error = {}", e.getMessage());
            ErrorDto errorDto = new ErrorDto();

            // https://codevang.tistory.com/268
            // 존재하지 않는 사용자일 때
            if(e instanceof AuthenticationServiceException) {
                errorDto.setErrorMessage("회원을 찾을 수 없습니다.");
            }
            // 패스워드가 일치하지 않을 때
            else if(e instanceof BadCredentialsException){
                errorDto.setErrorMessage("패스워드 오류!");
            } else {
                // 그 외의 다른 오류
                errorDto.setErrorMessage(e.getMessage());
            }
            return errorDto;
        }
    }

    /*****************************/

    private void saveRefreshToken(Authentication authentication, TokenDto tokenDto) {
        String refreshToken = tokenDto.getRefreshToken();
        // DB의 id 값이 저장되어 있음.
        Long id = Long.valueOf(authentication.getName());
        // id를 통해 멤버 검색
        Member findMember = memberService.getMemberById(id).orElseThrow(() -> {
            throw new NotFoundMember("회원을 찾을 수 없습니다.");
        });
        findMember.changeRefreshToken(refreshToken);
    }
}

// 참고하기 좋은) https://jeong-pro.tistory.com/205
/*
 * AuthenticationManager의 구현체 -> ProviderManager
 * 인증을 담당하는 클래스지만,
 * 멤버 변수로 가지고 있는 AuthenticationProvider들에게 인증을 처리하고
 * AuthenticationProvider를 구현한 클래스 객체가 인증을 하여 성공 시
 * 요청에 대해 ProviderManager가 인증이 되었다고 알려주는 방식이다.
 *
 * 이때, authenticate() 메서드의 리턴값인 Authentication 객체 안에 인증 값을 넣어주는 식으로 진행한다.
 *
 * 우리 코드에서 usernamePasswordAuthenticationToken이 ProviderManager에 도착하면,
 * ProviderManager는 자기가 가지고 있는 AuthenticationManager 목록을 순회하면서
 * support를 통해 이를 해결할 수 있는지 물어보고, 해결가능한 provider의 authenticate() 메서드를 실행한다.
 *
 */


/*
Authentication 객체로부터 인증에 필요한 정보 (username, password)를 가져오고,
UserDetailsService 인터페이스를 구현한 객체 (여기에서는 AuthMemberService)로부터
DB에 저장된 유저 정보를 받아온 뒤, password를 비교하고 인증한다.

 */