package propofol.userservice.api.member.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Date;

// 회원가입 Dto
@Data
@NoArgsConstructor
public class SaveMemberDto {
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotEmpty(message = "빈 값을 허용하지 않습니다.")
    private String email;

    @NotEmpty(message = "빈 값을 허용하지 않습니다.")
    private String password;

    @NotEmpty(message = "빈 값을 허용하지 않습니다.")
    private String username;

    @NotEmpty(message = "빈 값을 허용하지 않습니다.")
    private String nickname;

    @NotEmpty(message = "빈 값을 허용하지 않습니다.")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phoneNumber;
}
