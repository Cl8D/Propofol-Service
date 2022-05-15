package propofol.userservice.domain.streak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.userservice.api.member.controller.dto.streak.StreakRequestDto;
import propofol.userservice.domain.exception.NotFoundMember;
import propofol.userservice.domain.member.entity.Member;
import propofol.userservice.domain.member.service.MemberService;
import propofol.userservice.domain.streak.entity.Streak;
import propofol.userservice.domain.streak.repository.StreakRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreakService {
    private final StreakRepository streakRepository;
    private final MemberService memberService;

    // 스트릭 리스트 가져오기
    public List<Streak> getStreakByMemberId(Long memberId, LocalDate start, LocalDate end) {
        return streakRepository.findByMember_IdAndWorkingDateBetween(memberId, start, end);
    }

    /***************************/
    // 스트릭 생성
    @Transactional
    public String saveStreak(Long memberId, StreakRequestDto requestDto) {
        Member findMember = memberService.getMemberById(memberId).orElseThrow(() -> {
            throw new NotFoundMember("회원을 찾을 수 없습니다.");
        });


        // 멤버의 기존 스트릭 정보를 가져오고
        // orElse -> null이면 첫 스트릭 객체 만들기
        // 스트릭 생성 날짜 = 오늘, 기본적으로 1 설정.
        // null 아니면 찾은 스트릭 객체 리턴
        Streak resultStreak = streakRepository.findByMember_IdAndWorkingDate(memberId, requestDto.getDate())
                .orElse(Streak.createStreak().workingDate(requestDto.getDate())
                        .working(0)
                        .build());


        // 찾은 스트릭 객체와 찾은 스트릭 객체가 동일하다면 = 첫 스트릭
        resultStreak.addMember(findMember);
        resultStreak.addWorking(resultStreak.getWorking()+1);
        streakRepository.save(resultStreak);

        return "ok";
    }
}
