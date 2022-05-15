package propofol.userservice.api.member.controller.dto.streak;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 스트릭 생성 요청 DTO
@Data
@NoArgsConstructor
public class StreakRequestDto {
    private LocalDate date;
    private Boolean working;
}
