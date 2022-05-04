package propofol.ptfservice.domain.portfolio.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import propofol.ptfservice.domain.portfolio.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Portfolio extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="portfolio_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Template template; // 포트폴리오 템플릿

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.PERSIST)
    private List<Archive> archives = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.PERSIST)
    private List<Career> careers = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.PERSIST)
    private List<Project> projects = new ArrayList<>();

    /**TODO 태그 정보 추가 == 스킬*/
    /**TODO discovery-server에 ptf-service 등록해주기*/

    public void addArchive(Archive archive) {
        archives.add(archive);
        archive.addPortfolio(this);
    }

    public void addCareer(Career career) {
        careers.add(career);
        career.addPortfolio(this);
    }

    public void addProject(Project project) {
        projects.add(project);
        project.addPortfolio(this);
    }

    @Builder(builderMethodName = "createPortfolio")
    public Portfolio(Template template) {
        this.template = template;
    }

    public void updateTemplate (Template template) {
        this.template = template;
    }
}
