package propofol.userservice.api.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.userservice.api.auth.controller.dto.LoginRequestDto;
import propofol.userservice.api.auth.controller.dto.UpdatePasswordRequestDto;
import propofol.userservice.api.auth.service.AuthService;
import propofol.userservice.api.common.exception.dto.ErrorDetailDto;
import propofol.userservice.api.common.exception.dto.ErrorDto;
import propofol.userservice.api.common.jwt.JwtProvider;
import propofol.userservice.api.common.jwt.TokenDto;
import propofol.userservice.api.member.controller.dto.SaveMemberDto;
import propofol.userservice.domain.member.entity.Authority;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// 일반 로그인 관리
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Slf4j
// 로그인 시 JWT 토큰 반환
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;
    private final BCryptPasswordEncoder encoder;
    private final JwtProvider jwtProvider;

    // 로그인
    @PostMapping("/login")
    // @Validated를 통해 유효성 검사
    public Object login(@Validated @RequestBody LoginRequestDto loginDto,
                        HttpServletResponse response) {
        return authService.propofolLogin(loginDto, response);
    }

    /********************/

    // 멤버 저장 (회원가입)
    @PostMapping("/join")
    // created -> 클라이언트의 요청을 서버가 정상적으로 처리 + 새로운 리소스 생성
    @ResponseStatus(HttpStatus.CREATED)
    // @validated를 통해 원하는 속성에 대해서만 유효성 검사 가능
    // requestBody로 회원 가입 시 해당 멤버 정보 저장
    public Object saveMember(@Validated @RequestBody SaveMemberDto saveMemberDto, HttpServletResponse response){
        ErrorDto errorDto = new ErrorDto();

        // 이메일, 닉네임에 대한 중복 체크
        checkDuplicate(saveMemberDto, errorDto);

        // 만약 중복이 존재한다면
        if(errorDto.getErrors().size() != 0){
            // errorDto 객체를 통해 오류 정보 저장
            errorDto.setStatus(HttpStatus.BAD_REQUEST.value());
            errorDto.setMessage("중복 오류!");
            // status 상태 설정
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return errorDto;
        }

        // 회원가입 받은 string 형 생년월일 정보를 날짜 형태로 변경
        String birth = saveMemberDto.getMemberBirth();
        LocalDate date = LocalDate.parse(birth, DateTimeFormatter.ISO_DATE);

        // member 객체 생성
        Member member = createMember(saveMemberDto, date);

        // 회원 가입 진행
        memberService.saveMember(member);

        return "ok";
    }

    private Member createMember(SaveMemberDto saveMemberDto, LocalDate date) {
        Member member = Member.createMember()
                .email(saveMemberDto.getEmail())
                // 비밀번호 설정 시 암호화해서 넣기
                .password(encoder.encode(saveMemberDto.getPassword()))
                .nickname(saveMemberDto.getNickname())
                .username(saveMemberDto.getUsername())
                .birth(date)
                .degree(saveMemberDto.getDegree())
                .phoneNumber(saveMemberDto.getPhoneNumber())
                .score(saveMemberDto.getScore())
                .authority(Authority.ROLE_USER)
                .build();
        return member;
    }

    // 중복 체크
    private void checkDuplicate(SaveMemberDto saveMemberDto, ErrorDto errorDto) {
        // 이메일 중복 체크
        if(memberService.checkDuplicateByEmail(saveMemberDto.getEmail())){
            errorDto.getErrors().add(new ErrorDetailDto("Email", "중복 오류"));
        }

        // 닉네임 중복 체크
        if(memberService.checkDuplicateByNickname(saveMemberDto.getNickname())){
            errorDto.getErrors().add(new ErrorDetailDto("Nickname", "중복 오류"));
        }
    }

    /********************/

    // 비밀번호 변경 기능
    @PostMapping("/updatePassword")
    public String updatePassword(@RequestBody UpdatePasswordRequestDto requestDto) {
        memberService.updatePassword(requestDto.getEmail(), requestDto.getPassword());
        return "ok";
    }

    /********************/

    // refresh Token 요청 URL
    // 흐름)
    // JWT -> 요청할 때 이걸로만 진행. 유효시간 30분. Refresh Token -> 유효시간 하루, DB에 (Redis 사용 - 서버 종료되면 날아가게 하기 위해) 저장되어 있음.
    // 클라이언트가 JWT(access token)으로 요청을 하면 api-gateway에서는 인증을 진행한 다음 요청 서비스에 넘긴다.
    // 이때, JWT가 만료되었으면 서비스는 다시 클라이언트에게 refresh token을 보내라고 요청하며,
    // 클라이언트는 이때 JWT + refresh Token을 함께 보내준다.
    // 그럼 api-gateway는 refresh token이 있으면 인증을 하지 않고 서비스로 넘기게 되며 (아예 filter 자체를 돌지 않는다),
    // 이때 서비스는 DB에 저장된 refresh token값과 일치하는지 비교 + access token 만료 여부 확인 + refresh token의 만료 여부 확인을 끝난 뒤 모두 통과하면
    // 새로운 토큰을 만들어서 refresh, JWT토큰을 함께 리턴해준다.

    // 클라이언트는 refresh token 요청 시 무조건 user-service/auth/refresh로 보내야 함!
    @GetMapping("/refresh")
    public Object checkRefreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                    @RequestHeader("refresh-token") String refreshToken) {
        Member findMember = memberService.getRefreshMember(refreshToken);

        // access-token이 만료되었는지 확인
        // 만약 아직 JWT가 유효하다면
        if(jwtProvider.isJwtValid(token)) {
            // responseEntity = HttpRequest에 대한 응답 데이터를 포함하는 클래스.
            // = HttpStatus + HttpHeaders + HttpBody
            return new ResponseEntity("Valid Access-Token!", HttpStatus.BAD_REQUEST);
        }

        // Refresh-token의 유효시간이 지나지 않았는지 확인
        // + DB에 저장된 refresh-token과 일치하는지 확인
        if(jwtProvider.isRefreshTokenValid(refreshToken) &&
                findMember.getRefreshToken().equals(refreshToken)) {
            // 토큰 재생성
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenDto tokenDto = jwtProvider.createJwt(authentication);
            memberService.changeRefreshToken(findMember, refreshToken);
            return tokenDto;

        }
        // 아니라면 에러.
        return new ResponseEntity("Please Re-Login!", HttpStatus.BAD_REQUEST);
    }

}
