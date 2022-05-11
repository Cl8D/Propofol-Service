package propofol.userservice.api.auth.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

// 이메일 요청 DTO 추가 (프론트에서 중복 확인 버튼 클릭 시)
@Data
public class EmailRequestDto {
    @NotBlank
    private String email;
}