package propofol.ptfservice.domain.portfolio.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectDto {
    private String title;
    private String startTerm;
    private String endTerm;
    private String basicContent;
    private String detailContent;
    private String projectLink;
    private String skill;

}
