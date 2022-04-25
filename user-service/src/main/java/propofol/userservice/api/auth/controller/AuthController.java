package propofol.userservice.api.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import propofol.userservice.api.auth.controller.dto.LoginRequestDto;
import propofol.userservice.api.auth.service.AuthService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
// 로그인 시 JWT 토큰 반환
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequestDto loginDto,
                        HttpServletResponse response) {
        return authService.propofolLogin(loginDto, response);
    }
}
