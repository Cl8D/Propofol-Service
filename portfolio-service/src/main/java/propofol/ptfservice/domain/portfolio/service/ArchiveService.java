package propofol.ptfservice.domain.portfolio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.ptfservice.api.common.exception.NotMatchMemberException;
import propofol.ptfservice.domain.exception.NotFoundArchiveException;
import propofol.ptfservice.domain.exception.NotFoundPortfolioException;
import propofol.ptfservice.domain.portfolio.entity.Archive;
import propofol.ptfservice.domain.portfolio.entity.Portfolio;
import propofol.ptfservice.domain.portfolio.repository.ArchiveRepository;
import propofol.ptfservice.domain.portfolio.repository.PortfolioRepository;
import propofol.ptfservice.domain.portfolio.service.dto.ArchiveDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveService {
    private final PortfolioRepository portfolioRepository;
    private final ArchiveRepository archiveRepository;
    private final PortfolioService portfolioService;

    /**
     * 포트폴리오 수정 - 아카이브 수정
     */

    @Transactional
    public String updateArchive(Long portfolioId, Long archiveId, String memberId, ArchiveDto archiveDto) {
        Portfolio findPortfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> {
            throw new NotFoundPortfolioException("포트폴리오를 찾을 수 없습니다.");
        });

        // 포트폴리오 작성자가 아니라면
        if(!findPortfolio.getCreatedBy().equals(memberId))
            throw new NotMatchMemberException("권한이 없습니다.");

        Archive findArchive = archiveRepository.findById(archiveId).orElseThrow(() -> {
            throw new NotFoundArchiveException("아카이브가 존재하지 않습니다.");
        });

        Archive createdArchive = portfolioService.getArchive(archiveDto);
        findArchive.updateArchive(createdArchive.getLink(), createdArchive.getContent());
        return "ok";
    }


}
