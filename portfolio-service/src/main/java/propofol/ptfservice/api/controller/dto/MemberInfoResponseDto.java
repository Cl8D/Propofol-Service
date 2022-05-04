package propofol.ptfservice.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MemberInfoResponseDto {
    private String email;
    private String username;
    private String nickname;
    private String phoneNumber;
    private LocalDate birth;
    private String degree;
    private String score;
}
