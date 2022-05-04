package propofol.ptfservice.domain.portfolio.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

// 개인 블로그, 소스코드 정보 같은 아카이빙 정보 엔티티
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Archive {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="archive_id")
    private Long id;

    // 주소
    private String link;
    // 설명
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="portfolio_id")
    private Portfolio portfolio;

    public void addPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    @Builder(builderMethodName = "createArchive")
    public Archive(String link, String content) {
        this.link = link;
        this.content = content;
    }

    public void updateArchive(String link, String content){
        if(link!=null) this.link = link;
        if(content!=null) this.content = content;
    }
}
