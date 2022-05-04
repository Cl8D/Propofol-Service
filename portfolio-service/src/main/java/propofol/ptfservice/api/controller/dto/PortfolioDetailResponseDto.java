package propofol.ptfservice.api.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.ptfservice.api.controller.dto.archive.ArchiveResponseDto;
import propofol.ptfservice.api.controller.dto.career.CareerResponseDto;
import propofol.ptfservice.api.controller.dto.project.ProjectResponseDto;
import propofol.ptfservice.domain.portfolio.entity.Template;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PortfolioDetailResponseDto {
    private Template template;
    private List<ArchiveResponseDto> archives = new ArrayList<>();
    private List<CareerResponseDto> careers = new ArrayList<>();
    private List<ProjectResponseDto> projects = new ArrayList<>();
}
