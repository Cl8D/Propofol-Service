package propofol.userservice.api.member.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

// 스트릭 생성 요청 DTO
@Data
@NoArgsConstructor
public class StreakRequestDto {
    private LocalDate date;
    private Boolean working;
}
