package propofol.tagservice.domain.tag.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Tag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    // 태그 이름
    @Column(unique = true)
    private String name;

    public void changeTag(String tagName){
        this.name = tagName;
    }

    @Builder(builderMethodName = "createTag")
    public Tag(String name) {
        this.name = name;
    }
}
