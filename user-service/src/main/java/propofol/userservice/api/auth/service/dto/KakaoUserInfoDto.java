package propofol.userservice.api.auth.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/** Access Token를 통해 얻은 유저 정보 확인 */
// GET "https://kapi.kakao.com/v1/user/access_token_info"
// access token에 대한 정보를 담는 Dto
@Data
public class KakaoUserInfoDto {

    // 회원 번호
    private Long id;

    // access token 만료 시간
    @JsonProperty("expires_in")
    private Integer expiresIn;

    @JsonProperty("app_id")
    // token이 발급된 app Id
    private Integer appId;
}
