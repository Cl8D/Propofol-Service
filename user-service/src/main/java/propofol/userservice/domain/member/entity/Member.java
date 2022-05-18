package propofol.userservice.domain.member.entity;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

// Member Entity
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
// 변경된 점에 대해서만 update 하는 어노테이션
@DynamicUpdate
public class Member extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(updatable = false, nullable = false, unique = true)
    private String email; // 아이디

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(updatable = false)
    private String username; // 사용자 이름(성명)

    @Column(unique = true)
    private String nickname; // 별명

    private String phoneNumber; // 핸드폰 번호

    private LocalDate birth; // 생일
    private String degree; // 학력
    private String score; // 학점

    @Enumerated(value = EnumType.STRING)
    private Authority authority; // 권한

    private String refreshToken; // refreshToken

    @Builder(builderMethodName = "createMember")
    public Member(String email, String password, String username, String nickname,
                  String phoneNumber, LocalDate birth, String degree, String score, Authority authority) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
        this.degree = degree;
        this.score = score;
        this.authority = authority;
    }

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // 회원 정보 수정 - setter를 막았기 때문에 이런 식으로 업데이트를 해줘야 한다.
    public void update(String nickname, String password, String phoneNumber) {
        if(nickname!= null)
            this.nickname = nickname;

        if(password != null)
            this.password = password;

        if(phoneNumber != null)
            this.phoneNumber = phoneNumber;
    }

    // 비밀번호 수정
    public void updatePassword(String password) {
        this.password = password;
    }
}
