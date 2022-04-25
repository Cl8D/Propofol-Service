package propofol.userservice.api.jwt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TokenDto {
    private String type;
    private String accessToken;
    private String refreshToken;
    // 토큰 만료 기간
    private Long expirationDate;

    @Builder(builderMethodName = "createTokenDto")
    public TokenDto(String type, String accessToken, String refreshToken, Long expirationDate) {
        this.type = type;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
    }
}
