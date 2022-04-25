package propofol.userservice.api.auth.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

// 로그인 정보 요청에 대한 dto 추가
// 로그인 시 이메일-패스워드 입력하니까!
@Getter @Setter
@NoArgsConstructor
public class LoginRequestDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
