package propofol.ptfservice.api.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PortfolioResponseDto {
    private String email;
    private String username;
    private String phoneNumber;
    private LocalDate birth;
    private String degree;
    private String score;

    private PortfolioDetailResponseDto portfolioDto;

}
