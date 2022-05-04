package propofol.ptfservice.domain.portfolio.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

// 포트폴리오의 프로젝트 정보
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    // 프로젝트 제목
    private String title;

    // 프로젝트 기간 (yyyy.mm 형태)
    private String startTerm;
    private String endTerm;

    // 프로젝트 내용 (간단 소개)
    private String basicContent;

    // 프로젝트 내용 (세부 소개)
    private String detailContent;

    // 프로젝트 소스코드 저장 (ex 깃허브)
    private String projectLink;

    // 프로젝트 사용 기술
    private String skill;

    /** TODO 프로젝트에서 사진도 넣을 수 있어야 할 것 같은데 이부분은 게시판 이미지부터 구현한 다음 구현하기*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="portfolio_id")
    private Portfolio portfolio;

    public void addPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    @Builder(builderMethodName = "createProject")
    public Project(String title, String startTerm, String endTerm, String basicContent, String detailContent, String projectLink, String skill) {
        this.title = title;
        this.startTerm = startTerm;
        this.endTerm = endTerm;
        this.basicContent = basicContent;
        this.detailContent = detailContent;
        this.projectLink = projectLink;
        this.skill = skill;
    }

    public void updateProject(String title, String startTerm, String endTerm, String basicContent, String detailContent, String projectLink, String skill) {
        if(title!=null) this.title = title;
        if(startTerm!=null) this.startTerm = startTerm;
        if(endTerm!=null) this.endTerm = endTerm;
        if(basicContent!=null) this.basicContent = basicContent;
        if(detailContent!=null) this.detailContent = detailContent;
        if(projectLink!=null) this.projectLink = projectLink;
        if(skill!=null) this.skill = skill;
    }
}
