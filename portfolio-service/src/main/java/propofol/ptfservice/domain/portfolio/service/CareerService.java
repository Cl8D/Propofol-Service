package propofol.ptfservice.domain.portfolio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.ptfservice.api.common.exception.NotMatchMemberException;
import propofol.ptfservice.domain.exception.NotFoundCareerException;
import propofol.ptfservice.domain.exception.NotFoundPortfolioException;
import propofol.ptfservice.domain.portfolio.entity.Career;
import propofol.ptfservice.domain.portfolio.entity.Portfolio;
import propofol.ptfservice.domain.portfolio.repository.CareerRepository;
import propofol.ptfservice.domain.portfolio.repository.PortfolioRepository;
import propofol.ptfservice.domain.portfolio.service.dto.CareerDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class CareerService {
    private final CareerRepository careerRepository;
    private final PortfolioService portfolioService;
    private final PortfolioRepository portfolioRepository;

    /**
     * 포트폴리오 수정 - 커리어 수정
     */

    @Transactional
    public String updateCareer(Long portfolioId, Long careerId, String memberId, CareerDto careerDto) {
        Portfolio findPortfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> {
            throw new NotFoundPortfolioException("포트폴리오를 찾을 수 없습니다.");
        });

        // 포트폴리오 작성자가 아니라면
        if(!findPortfolio.getCreatedBy().equals(memberId))
            throw new NotMatchMemberException("권한이 없습니다.");

        Career findCareer = careerRepository.findById(careerId).orElseThrow(() -> {
            throw new NotFoundCareerException("경력 사항을 찾을 수 없습니다.");
        });

        Career createdCareer = portfolioService.getCareer(careerDto);
        findCareer.updateCareer(createdCareer.getTitle(), createdCareer.getStartTerm(), createdCareer.getEndTerm(),
                createdCareer.getBasicContent(), createdCareer.getDetailContent());

        return "ok";
    }

}
