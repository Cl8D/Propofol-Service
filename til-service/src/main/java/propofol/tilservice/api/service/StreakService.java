package propofol.tilservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.tilservice.api.feign.UserServiceFeignClient;
import propofol.tilservice.api.feign.dto.StreakResponseDto;

import java.time.LocalDate;

// 게시글 생성시 스트릭이 추가되는 것이고, 스트릭의 경우 user-service에서 관리되기 때문에
// domain이 아닌 api단에서 StreakService 생성
@Service
@RequiredArgsConstructor
public class StreakService {
    private final UserServiceFeignClient userServiceFeignClient;

    public void saveStreak(String token) {
        // 스트릭 정보 관리. 현재 날짜와 스트릭의 생성 여부가 들어간다.
        StreakResponseDto streakResponseDto = new StreakResponseDto();
        streakResponseDto.setDate(LocalDate.now());
        streakResponseDto.setWorking(true);
        userServiceFeignClient.saveStreak(token, streakResponseDto);
    }
}
