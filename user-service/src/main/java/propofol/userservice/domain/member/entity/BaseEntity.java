package propofol.userservice.domain.member.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

// 공통 필드

// 객체 입장에서 공통의 매핑 정보가 필요할 때 사용
// 부모 클래스에 선언 후 상속받아서 사용할 수 있음.
// 엔티티가 아니기 때문에 테이블이랑 매핑 X + 조회, 검색 불가능
// 추상 클래스로 만드는 것이 정석이다.
@MappedSuperclass

// 엔티티에 변화가 생겼을 때 감지
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

    // Entity가 생성되어 저장되었을 때 시간이 자동으로 저장된다.
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    // 조회한 Entity 값을 변경할 때 시간이 자동 저장된다.
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
