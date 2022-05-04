package propofol.userservice.api.common.jwt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

// JWT 토큰 생성을 위한 Dto
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TokenDto {
    private String type;
    private String accessToken;
    private String refreshToken;

    @Builder(builderMethodName = "createTokenDto")
    public TokenDto(String type, String accessToken, String refreshToken) {
        this.type = type;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
