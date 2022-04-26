package propofol.userservice.api.member.controller.dto;

import lombok.Data;

// 회원 정보 수정을 위해 사용되는 Dto - 컨트롤러단에서 받아온 정보 객체
@Data
public class UpdateRequestDto {
    private String password;
    private String nickname;
    private String score;
    private String degree;
    private String phoneNumber;
}
