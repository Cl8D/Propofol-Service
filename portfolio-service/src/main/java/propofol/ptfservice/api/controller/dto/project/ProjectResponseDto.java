package propofol.ptfservice.api.controller.dto.project;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectResponseDto {
    private String title;
    private String startTerm;
    private String endTerm;
    private String basicContent;
    private String detailContent;
    private String projectLink;
    private String skill;
}
