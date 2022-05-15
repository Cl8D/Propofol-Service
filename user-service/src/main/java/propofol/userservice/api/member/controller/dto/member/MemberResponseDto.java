package propofol.userservice.api.member.controller.dto.member;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 회원 정보 조회 Dto
@Data
@NoArgsConstructor
// 없는 필드에 대해서는 json 생성 x
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MemberResponseDto {
    private String email;
    private String username;
    private String nickname;
    private String phoneNumber;
    private LocalDate birth;
    private String degree;
    private String score;
}
