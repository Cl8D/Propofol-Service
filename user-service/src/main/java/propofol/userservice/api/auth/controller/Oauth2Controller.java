package propofol.userservice.api.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import propofol.userservice.api.auth.controller.dto.ResponseDto;
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
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto kakaoLogin(@RequestParam String code) {
        // code를 통하여 jwtToken 생성
        TokenDto tokenDto = oauth2Service.getToken(code);
        return new ResponseDto<>(HttpStatus.OK.value(), "success", "카카오 로그인 성공!", tokenDto);
    }

}
