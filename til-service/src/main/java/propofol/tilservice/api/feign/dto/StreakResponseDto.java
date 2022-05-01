package propofol.tilservice.api.feign.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class StreakResponseDto {
    private LocalDate date;
    private Boolean working;
}
