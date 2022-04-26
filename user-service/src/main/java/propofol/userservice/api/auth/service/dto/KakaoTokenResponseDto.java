package propofol.userservice.api.auth.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/** Access Token 받기 */
// POST "https://kauth.kakao.com/oauth/token"
// 흐름) 서버 -> 카카오에게 code를 바탕으로 access token 생성 요청
// 카카오는 서버에게 access token에 대한 response 제공
// 카카오에서 준 access token에 대한 응답 Dto
@Data
public class KakaoTokenResponseDto {

    // jsonProperty -> 객체를 json 형식으로 변환 시 key 이름 설정 가능

    // 토큰 타입 (Bearer )
    @JsonProperty("token_type")
    private String tokenType;

    // 발급된 access token 값
    @JsonProperty("access_token")
    private String accessToken;

    // access token, id_token(사용자 인증 정보) 의 만료시간
    @JsonProperty("expires_in")
    private String expiresIn;

    // refresh token 값
    @JsonProperty("refresh_token")
    private String refreshToken;

    // refresh token의 만료 시간
    @JsonProperty("refresh_token_expires_in")
    private String refreshTokenExpiresIn;
}
