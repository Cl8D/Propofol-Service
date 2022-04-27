package propofol.tilservice.api.common.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

// jwt 설정 정보 - 프로퍼티 파일 어노테이션으로 불러오기
@Getter
// .properties, .yml 파일의 프로퍼티 정보를 자바 클래스에 값을 바인딩하여 사용 가능하도록 해준다.
// cf) https://programmer93.tistory.com/47
// .yml 파일의 token에 있는 하위 정보들에 대해서 (type, expiration_time, secret) 각각 바인딩.
@ConfigurationProperties(prefix = "token")
@ConstructorBinding
public class JwtProperties {
    private final String type;
    private final String expirationTime;
    private final String secret;

    public JwtProperties(String type, String expirationTime, String secret) {
        this.type = type;
        this.expirationTime = expirationTime;
        this.secret = secret;
    }
}
