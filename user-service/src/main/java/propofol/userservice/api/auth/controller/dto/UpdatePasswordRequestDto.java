package propofol.userservice.api.auth.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

// 비밀번호 변경을 위한 DTO
@Data
public class UpdatePasswordRequestDto {

    @NotBlank(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "올바른 비밀번호 형식이 아닙니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8~15 자리입니다.")
    private String password;

}
