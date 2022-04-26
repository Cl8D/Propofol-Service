package propofol.userservice.api.common.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

// KakaoOauth2 설정 정보 프로퍼티
// access token 발급 시 필요한 파라미터 정보들이다.
// cf) https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
@Getter
@ConfigurationProperties(prefix = "oauth.provider.kakao")
@ConstructorBinding
public class KakaoOauth2Properties {
    // token 발급을 위해 카카오에게 요청하는 주소
    private final String getTokenUri;

    // access token 발급 후 유저 정보를 가져오기 위해 카카오에게 보내는 주소
    private final String userInfoUri;

    // REST API 키
    private final String clientId;

    // 토큰 발급 시 보안 강화를 위해 추가 확인하는 시크릿 값
    private final String clientSecret;

    // code가 리다리엑트 될 URI
    private final String redirectUri;

    // token에서 필요 => authorization_code (고정)
    private final String grantType;

    public KakaoOauth2Properties(String getTokenUri, String userInfoUri, String clientId, String clientSecret, String redirectUri, String grantType) {
        this.getTokenUri = getTokenUri;
        this.userInfoUri = userInfoUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.grantType = grantType;
    }
}
