package propofol.ptfservice.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.api.controller.dto.archive.ArchiveCreateRequestDto;
import propofol.ptfservice.api.controller.dto.career.CareerCreateRequestDto;
import propofol.ptfservice.api.controller.dto.project.ProjectCreateRequestDto;
import propofol.ptfservice.domain.portfolio.entity.Template;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PortfolioCreateRequestDto {
    @NotNull
    private Template template;
    @Valid
    private List<ArchiveCreateRequestDto> archives = new ArrayList<>();
    @Valid
    private List<CareerCreateRequestDto> careers = new ArrayList<>();
    @Valid
    private List<ProjectCreateRequestDto> projects = new ArrayList<>();

}
