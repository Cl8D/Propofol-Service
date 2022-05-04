package propofol.userservice.api.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import propofol.userservice.api.auth.service.dto.KakaoTokenResponseDto;
import propofol.userservice.api.auth.service.dto.KakaoUserInfoDto;
import propofol.userservice.api.common.jwt.JwtProvider;
import propofol.userservice.api.common.jwt.TokenDto;
import propofol.userservice.api.common.properties.KakaoOauth2Properties;
import propofol.userservice.domain.member.entity.Authority;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2Service {
    /*
        흐름)
        Service Client(React) <-> Service Server(Spring) <-> Kakao Auth Server

        1)
        Client -> 카카오 로그인 요청
        Server -> (GET) /oauth/authorize -> Kakao

        2)
        Kakao -> 카카오 계정 로그인 요청
        Client -> 카카오 계정 로그인
        Kakao -> 동의 화면 출력
        Client -> 동의 후 시작

        3)
        Kakao -> Redirect URI를 통해 code 전달
        (CORS 문제로 인해서 redirect 후 Client=>Server로 URI parsing 후 code 부분 전달)

        == 여기까지가 인증 처리 단계 (code) ==

        4)
        Server -> code를 통해 access token 발급 요청 (POST) /oauth/token -> Kakao
        Kakao -> access token 발급 -> Server

        5)
        Server -> id_token 유효성 검증
        Server -> access token을 통해 사용자 정보 요청 -> Kakao
        정보 확인 후 서비스에 가입 처리, 로그인 완료 진행
     */

    private final KakaoOauth2Properties kakao;
    private final RestTemplate rt;
    private final BCryptPasswordEncoder encoder;
    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    // 3번 과정 후, client->server로 code를 넘겨주었다면
    public TokenDto getToken(String code) {

        // access token 발급 요청
        ResponseEntity<KakaoTokenResponseDto> tokenResponse = getAccessToken(code);

        // response에 대한 상태 코드 검증
        if(tokenResponse.getStatusCode() != HttpStatus.OK)
            throw new RuntimeException("로그인 오류가 발생하였습니다!");

        // response의 응답 결과 확인
        KakaoTokenResponseDto responseBody = tokenResponse.getBody();

        // body의 access_token을 보면 토큰을 가져올 수 있다.
        String accessToken = responseBody.getAccessToken();

        // 5번 과정 - 발급된 access_token을 바탕으로 사용자 정보 요청
        ResponseEntity<KakaoUserInfoDto> userInfoResponse = getUserInfo(accessToken);
        KakaoUserInfoDto userInfo = userInfoResponse.getBody();

        // 얻은 유저 정보를 통해서 Authentication 객체 생성하기
        Authentication authenticate = createAuthenticate(userInfo);

        // 만든 authentication 객체를 바탕으로 jwtToken 생성하기
        return jwtProvider.createJwt(authenticate);

    }

    /******************/

    // POST https://kauth.kakao.com/oauth/token
    private ResponseEntity<KakaoTokenResponseDto> getAccessToken(String code) {
        // 필수 파라미터를 포함하여 post로 요청해야 한다. (request에 5가지 파라미터 필요) -> 4번 과정
        // 이를 위해서 Http Body에 데이터 담아주기.
        // HttpHeader, HttpBody를 담는 오브젝트 생성
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", kakao.getGrantType()); // "authorization_code"로 고정
        map.add("client_id", kakao.getClientId()); // rest api 키
        map.add("redirect_uri", kakao.getRedirectUri()); // 로그인 성공 시 redirect될 uri
        map.add("code", code); // client로부터 온 code
        map.add("client_secret", kakao.getClientSecret()); // 보안 강화를 위해 추가 확인하는 코드


        // spring에서 제공하는 여러 template class를 동일한 원칙에 따라 설계한 게 restTemplate
        // cf) https://advenoh.tistory.com/46
        // POST로 요청을 보내고, 결과로 responseEntity를 반환해준다.

        // 파라미터 정보) URI, Request, responseType
        return rt.postForEntity(kakao.getGetTokenUri(),
                createHttpEntity(map, MediaType.APPLICATION_FORM_URLENCODED),
                KakaoTokenResponseDto.class);
    }

    /******************/

    // access token 요청 시 형식
    /*
    POST /oauth/token HTTP/1.1
    Host: kauth.kakao.com
    Content-type: application/x-www-form-urlencoded;charset=utf-8
    */
    private HttpEntity<Object> createHttpEntity(MultiValueMap map, MediaType mediaType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(mediaType);

        // httpEntity는 Http 요청이나 응답에 해당하는 HttpHeader나 HttpBody를 포함하는 클래스.
        // map -> body 정보, httpHeaders -> header 정보
        return new HttpEntity<>(map, httpHeaders);
    }

    /******************/

    // 유저 정보 요청 시 형식
    /*
    GET /v1/user/access_token_info HTTP/1.1
    Host: kapi.kakao.com
    Authorization: Bearer ${ACCESS_TOKEN}
     */
    private ResponseEntity<KakaoUserInfoDto> getUserInfo (String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        // exchange 사용 시 http 헤더 생성 가능 + 어떠한 http 메서드든 사용 가능
        // 파라미터 정보) URI, httpMethod, requestEntity, responseType
        // 이러면 응답 결과로 회원 정보를 준다!
        ResponseEntity<KakaoUserInfoDto> userInfoResponse = rt.exchange(kakao.getUserInfoUri(), HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                KakaoUserInfoDto.class);

        return userInfoResponse;
    }

    /******************/

    // Authentication 객체 생성
    private Authentication createAuthenticate(KakaoUserInfoDto userInfo) {
        // 임의로 사용자 id 생성해주기
        String email = "kakao" + userInfo.getId() + "@kakao.com";
        // 비밀번호 생성해주기
        String password = encoder.encode(kakao.getClientSecret());


        // 이미 존재하는 회원이 아니라면 (신규 회원)
        // 만든 이메일, 비밀번호를 바탕으로 우리 서비스에 맞는 member 객체 생성해주기
        if(!memberService.isExistByEmail(email)) {
            Member member = Member.createMember()
                    .email(email)
                    .password(password)
                    .phoneNumber(null)
                    .degree(null)
                    .birth(null)
                    .nickname(null)
                    .username(null)
                    .score(null)
                    .authority(Authority.ROLE_BASIC)
                    .build();
            memberService.saveMember(member);
        }

        // spring Security 적용을 위한 token 생성 - AuthService 참고!
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(email, kakao.getClientSecret());

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        return authenticate;
    }
}
