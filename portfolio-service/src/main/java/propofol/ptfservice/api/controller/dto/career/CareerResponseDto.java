package propofol.ptfservice.api.controller.dto.career;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CareerResponseDto {
    private String title;
    private String startTerm;
    private String endTerm;
    private String basicContent;
    private String detailContent;
}
