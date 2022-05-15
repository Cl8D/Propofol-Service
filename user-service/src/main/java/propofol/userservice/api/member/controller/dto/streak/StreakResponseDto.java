package propofol.userservice.api.member.controller.dto.streak;

import lombok.Data;
import lombok.NoArgsConstructor;
import propofol.userservice.api.member.controller.dto.streak.StreakDetailResponseDto;

import java.util.ArrayList;
import java.util.List;

// 사용자의 스트릭 정보를 담는 DTO
@Data
@NoArgsConstructor
public class StreakResponseDto {
    // 스트릭은 1년까지만 보여지기 때문에 연도 정보 추가.
    private String year;
    private List<StreakDetailResponseDto> streaks = new ArrayList<>();
}
