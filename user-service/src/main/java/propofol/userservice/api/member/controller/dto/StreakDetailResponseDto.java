package propofol.userservice.api.member.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// streak 하나에 대한 정보를 담는 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreakDetailResponseDto {
    private LocalDate workingDate;
    private Integer working;
}
