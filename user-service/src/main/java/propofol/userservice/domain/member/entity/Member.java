package propofol.userservice.domain.member.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

// Member Entity
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(updatable = false, nullable = false, unique = true)
    private String email; // 아이디

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false, updatable = false)
    private String username; // 사용자 이름(성명)

    @Column(nullable = false, unique = true)
    private String nickname; // 별명

    @Column(nullable = false)
    private String phoneNumber;

    private LocalDate birth;
    private String degree; // 학력
    private String score; // 학점

    @Enumerated(value = EnumType.STRING)
    private Authority authority;

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

    // 회원 정보 수정 - setter를 막았기 때문에 이런 식으로 업데이트를 해줘야 한다.
    public void update(String nickname, String password, String score, String degree, String phoneNumber) {
        if(nickname!= null)
            this.nickname = nickname;

        if(password != null)
            this.password = password;

        if(score!= null)
            this.score = score;

        if(degree != null)
            this.degree = degree;

        if(phoneNumber != null)
            this.phoneNumber = phoneNumber;
    }
}
