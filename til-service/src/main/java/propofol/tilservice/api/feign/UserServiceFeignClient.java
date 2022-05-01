package propofol.tilservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import propofol.tilservice.api.feign.dto.MemberInfoDto;
import propofol.tilservice.api.feign.dto.StreakResponseDto;

// til-service => user-service로 정보 요청
@FeignClient(name = "user-service")
public interface UserServiceFeignClient {
    // 멤버 정보 가져오기 - user-service의 getMemberByMemberId를 호출하는 것과 같은 형태라고 볼 수 있다.
    @GetMapping("/api/v1/members")
    MemberInfoDto getMemberInfo(@RequestHeader(name = "Authorization") String token);

    // 글 쓸 때 스트릭 정보를 저장하기 위해 user-service의 saveStreak 함수 호출
    @PostMapping("/api/v1/members/streak")
    void saveStreak(
            // (이 함수를 호출하는 컨트롤러에서) token 값을 Authorization 헤더 값에 넣어주기
            @RequestHeader(name = "Authorization") String token,
            // responseDto 정보 역시 넣어주기!
            @RequestBody StreakResponseDto streakResponseDto);
}
