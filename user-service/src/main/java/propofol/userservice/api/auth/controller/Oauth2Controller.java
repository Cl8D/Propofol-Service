package propofol.userservice.api.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import propofol.userservice.api.auth.service.Oauth2Service;
import propofol.userservice.api.common.jwt.TokenDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class Oauth2Controller {

    private final Oauth2Service oauth2Service;

    // client=>server로 code를 파싱하여 전송해줌
    // 이때 전송 주소는 /oauth2/kakao/login/?code=~
    @GetMapping("/kakao/login")
    public TokenDto kakaoLogin(@RequestParam String code) {
        // code를 통하여 jwtToken 생성
        TokenDto token = oauth2Service.getToken(code);
        return token;
    }

}
