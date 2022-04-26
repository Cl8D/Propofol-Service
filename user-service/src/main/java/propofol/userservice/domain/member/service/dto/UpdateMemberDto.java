package propofol.userservice.domain.member.service.dto;

import lombok.Data;

// domain-api를 서로 분리하기 위해서 dto 계층 새로 추가.
// api단에서 domain에 접근하기 위해 사용되는 dto
@Data
public class UpdateMemberDto {
    private String password;
    private String nickname;
    private String score;
    private String degree;
    private String phoneNumber;
}
