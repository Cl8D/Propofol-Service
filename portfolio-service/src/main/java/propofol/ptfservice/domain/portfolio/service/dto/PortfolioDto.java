package propofol.ptfservice.domain.portfolio.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.domain.portfolio.entity.Template;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PortfolioDto {
    private Template template;
    private List<ArchiveDto> archives = new ArrayList<>();
    private List<CareerDto> careers = new ArrayList<>();
    private List<ProjectDto> projects = new ArrayList<>();

    public PortfolioDto(Template template, List<ArchiveDto> archives, List<CareerDto> careers, List<ProjectDto> projects) {
        this.template = template;
        this.archives = archives;
        this.careers = careers;
        this.projects = projects;
    }
}
