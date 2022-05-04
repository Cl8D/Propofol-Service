package propofol.ptfservice.domain.portfolio.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

// 포트폴리오 경력 정보
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Career {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="career_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    // 경력 제목 (기관명)
    private String title;

    // 경력 기간 - yyyy.mm 형태로 받을 예정
    private String startTerm;
    private String endTerm;

    // 경력 내용 (짧은 소개)
    private String basicContent;

    // 경력 내용 (세부 내용)
    private String detailContent;

    public void addPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    @Builder(builderMethodName = "createCareer")
    public Career(String title, String startTerm, String endTerm, String basicContent, String detailContent) {
        this.title = title;
        this.startTerm = startTerm;
        this.endTerm = endTerm;
        this.basicContent = basicContent;
        this.detailContent = detailContent;
    }

    public void updateCareer(String title, String startTerm, String endTerm, String basicContent, String detailContent) {
        if(title != null) this.title = title;
        if(startTerm != null) this.startTerm = startTerm;
        if(endTerm != null) this.endTerm = endTerm;
        if(basicContent != null) this.basicContent = basicContent;
        if(detailContent != null) this.detailContent = detailContent;
    }
}
