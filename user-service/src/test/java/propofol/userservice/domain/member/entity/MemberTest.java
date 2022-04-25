package propofol.userservice.domain.member.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import propofol.userservice.domain.member.repository.MemberRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    public void memberTest() throws Exception {
        // given
        Member member = Member.createMember()
                .username("jiwon")
                .nickname("jiwon123!")
                .authority(Authority.USER_BASIC)
                .birth(LocalDate.now())
                .degree("숭실대")
                .email("jj@naver.com")
                .password("qweqwe")
                .phoneNumber("01041565974")
                .score("4.5")
                .build();
        memberRepository.save(member);

        em.flush();
        em.clear();

        // then
        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getAuthority()).isEqualTo(Authority.USER_BASIC);
    }
}