package propofol.ptfservice.api.controller.dto.career;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class CareerUpdateRequestDto {
    private String title;
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}", message = "올바른 날짜 형식이 아닙니다.")
    private String startTerm;
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}", message = "올바른 날짜 형식이 아닙니다.")
    private String endTerm;
    private String basicContent;
    private String detailContent;
}
