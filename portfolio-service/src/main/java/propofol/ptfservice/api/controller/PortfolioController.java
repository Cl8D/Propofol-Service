package propofol.ptfservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.ptfservice.api.common.annotation.Token;
import propofol.ptfservice.api.controller.dto.MemberInfoResponseDto;
import propofol.ptfservice.api.controller.dto.PortfolioCreateRequestDto;
import propofol.ptfservice.api.controller.dto.PortfolioDetailResponseDto;
import propofol.ptfservice.api.controller.dto.PortfolioResponseDto;
import propofol.ptfservice.api.controller.dto.archive.ArchiveResponseDto;
import propofol.ptfservice.api.controller.dto.archive.ArchiveUpdateRequestDto;
import propofol.ptfservice.api.controller.dto.career.CareerResponseDto;
import propofol.ptfservice.api.controller.dto.career.CareerUpdateRequestDto;
import propofol.ptfservice.api.controller.dto.project.ProjectResponseDto;
import propofol.ptfservice.api.controller.dto.project.ProjectUpdateRequestDto;
import propofol.ptfservice.domain.portfolio.entity.Portfolio;
import propofol.ptfservice.domain.portfolio.entity.Template;
import propofol.ptfservice.domain.portfolio.service.ArchiveService;
import propofol.ptfservice.domain.portfolio.service.CareerService;
import propofol.ptfservice.domain.portfolio.service.PortfolioService;
import propofol.ptfservice.domain.portfolio.service.ProjectService;
import propofol.ptfservice.domain.portfolio.service.dto.ArchiveDto;
import propofol.ptfservice.domain.portfolio.service.dto.CareerDto;
import propofol.ptfservice.domain.portfolio.service.dto.PortfolioDto;
import propofol.ptfservice.domain.portfolio.service.dto.ProjectDto;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final ArchiveService archiveService;
    private final CareerService careerService;
    private final ProjectService projectService;
    private final ModelMapper modelMapper;


    /**
     * 포트폴리오 생성
     */
    @PostMapping
    public String createPortfolio (@Validated @RequestBody PortfolioCreateRequestDto requestDto) {
        List<ArchiveDto> archiveDtos = new ArrayList<>();
        requestDto.getArchives().forEach(archive -> {
            archiveDtos.add(modelMapper.map(archive, ArchiveDto.class));
        });

        List<CareerDto> careerDtos = new ArrayList<>();
        requestDto.getCareers().forEach(career -> {
            careerDtos.add(modelMapper.map(career, CareerDto.class));
        });

        List<ProjectDto> projectDtos = new ArrayList<>();
        requestDto.getProjects().forEach(project -> {
            projectDtos.add(modelMapper.map(project, ProjectDto.class));
        });

        PortfolioDto portfolioDto = new PortfolioDto(requestDto.getTemplate(), archiveDtos, careerDtos, projectDtos);
        Portfolio portfolio = portfolioService.createPortfolio(portfolioDto);
        return portfolioService.savePortfolio(portfolio);
    }

    /**
     * 포트폴리오 조회
     */
    @GetMapping("/myPortfolio")
    public PortfolioResponseDto getPortfolio (@RequestHeader(name = "Authorization") String token,
                                              @Token String memberId) {
        PortfolioResponseDto responseDto = new PortfolioResponseDto();

        MemberInfoResponseDto memberInfo = portfolioService.getMemberInfo(token);
        Portfolio findPortfolio = portfolioService.getPortfolioInfo(memberId);

        responseDto.setEmail(memberInfo.getEmail());
        responseDto.setUsername(memberInfo.getUsername());
        responseDto.setPhoneNumber(memberInfo.getPhoneNumber());
        responseDto.setBirth(memberInfo.getBirth());
        responseDto.setDegree(memberInfo.getDegree());
        responseDto.setScore(memberInfo.getScore());

        PortfolioDetailResponseDto portfolioDto = new PortfolioDetailResponseDto();

        // 하나의 포트폴리오 내부 필드
        List<CareerResponseDto> careerDtos = new ArrayList<>();
        List<ProjectResponseDto> projectDtos = new ArrayList<>();
        List<ArchiveResponseDto> archiveDtos = new ArrayList<>();

        findPortfolio.getCareers().forEach(career -> {
            CareerResponseDto careerDto = modelMapper.map(career, CareerResponseDto.class);
            careerDtos.add(careerDto);
        });

        findPortfolio.getProjects().forEach(project -> {
            ProjectResponseDto projectDto = modelMapper.map(project, ProjectResponseDto.class);
            projectDtos.add(projectDto);
        });

        findPortfolio.getArchives().forEach(archive -> {
            ArchiveResponseDto archiveDto = modelMapper.map(archive, ArchiveResponseDto.class);
            archiveDtos.add(archiveDto);
        });

        portfolioDto.setTemplate(findPortfolio.getTemplate());
        portfolioDto.setCareers(careerDtos);
        portfolioDto.setProjects(projectDtos);
        portfolioDto.setArchives(archiveDtos);

        responseDto.setPortfolioDto(portfolioDto);

        return responseDto;
    }


    /**
     * 포트폴리오 삭제 (초기화)
     */
    @DeleteMapping("/{portfolioId}")
    public String deletePortfolio(@PathVariable(value = "portfolioId") Long portfolioId,
                                  @Token String memberId) {
        return portfolioService.deletePortfolio(portfolioId, memberId);
    }

    /**
     * 포트폴리오 수정 - 템플릿 수정
     */
    @PostMapping("/{portfolioId}/template")
    public String updateTemplate(@PathVariable(value = "portfolioId") Long portfolioId,
                                 @Token String memberId,
                                 @RequestParam Template template) {
        return portfolioService.updateTemplate(portfolioId, memberId, template);
    }

    /**
     * 포트폴리오 수정 - 아카이브 수정
     */
    @PostMapping("/{portfolioId}/archive/{archiveId}")
    public String updatePortfolio(@PathVariable(value = "portfolioId") Long portfolioId,
                                  @PathVariable(value = "archiveId") Long archiveId,
                                  @Token String memberId,
                                  @Validated @RequestBody ArchiveUpdateRequestDto requestDto) {

        ArchiveDto archiveDto = modelMapper.map(requestDto, ArchiveDto.class);
        return archiveService.updateArchive(portfolioId, archiveId, memberId, archiveDto);
    }

    /**
     * 포트폴리오 수정 - 경력 수정
     */
    @PostMapping("/{portfolioId}/career/{careerId}")
    public String updatePortfolio(@PathVariable(value = "portfolioId") Long portfolioId,
                                  @PathVariable(value = "careerId") Long careerId,
                                  @Token String memberId,
                                  @Validated @RequestBody CareerUpdateRequestDto requestDto) {

        CareerDto careerDto = modelMapper.map(requestDto, CareerDto.class);
        return careerService.updateCareer(portfolioId, careerId, memberId, careerDto);
    }

    /**
     * 포트폴리오 수정 - 프로젝트 수정
     */
    @PostMapping("/{portfolioId}/project/{projectId}")
    public String updatePortfolio(@PathVariable(value = "portfolioId") Long portfolioId,
                                  @PathVariable(value = "projectId") Long projectId,
                                  @Token String memberId,
                                  @Validated @RequestBody ProjectUpdateRequestDto requestDto) {

        ProjectDto projectDto = modelMapper.map(requestDto, ProjectDto.class);
        return projectService.updateProject(portfolioId, projectId, memberId, projectDto);
    }



}
