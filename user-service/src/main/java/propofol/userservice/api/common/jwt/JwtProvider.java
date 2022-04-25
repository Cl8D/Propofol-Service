package propofol.userservice.api.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// JWT 토큰 생성기
@Component
@Slf4j
public class JwtProvider {

    // 설정파일을 주입시킬 때 @Value를 사용한다.
    // @Value("${properties-key}"와 같은 형식으로 사용한다.
    // 만약 설정파일에 test.name="hi"라고 저장해왔다면,
    // @Value("${test.name}")을 통해 값을 알려줄 수 있다.
//    @Value("${test}")
//    private String test;

    // environment를 통해서 property를 PropertySource로 통합 관리한다.
    // 걍 @Value랑 똑같은 역할을 한다고 보면 될 것 같다.
//    private final Environment env;

    private final Key key;
    private final String expirationTime;
    private final String type;

    // 프로퍼티 정보 가져오기 - 생성자를 통해 의존성 주입
    // 원래는 기존 코드에서 로드했었는데 그냥 한 번에 로드하는 형식으로 코드 변경
    public JwtProvider(@Value("${token.secret}") String secret,
                       @Value("${token.expiration_time}") String expirationTime,
                       @Value("${token.type}") String type) {

        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expirationTime = expirationTime;
        this.type = type;
    }


    // 유저 정보를 활용하여 accessToken, refreshToken을 생성한다.
    public TokenDto createJwt(Authentication authentication) {
        // authentication.getAuthorities를 사용하면 collections 형태로 나오게 되는데,
        // 각 권한을 grantedAuthority 변수에 담아서 하나의 string으로 만들어주기
        String authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                // joining => a, b, c... 이런 식으로 생성 가능
                .collect(Collectors.joining(", "));

//        // application-secret.yml에 있는 token:secret 값을 가져온다.
//        String secret = env.getProperty("token.secret");
//
//        // string->byte
//        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
//
//        key = Keys.hmacShaKeyFor(keyBytes);

        // 토큰 만료 기간
        Date expirationDate = new Date(System.currentTimeMillis()
//                + Long.parseLong(env.getProperty("token.expiration_time")));
                + Long.parseLong(expirationTime));

        // jwt 토큰 생성
        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("role", authorities)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

//        String type = env.getProperty("token.type"); // bearer

        return TokenDto.createTokenDto()
                .type(type)
                .accessToken(token)
                .refreshToken(null)
                .expirationDate(expirationDate.getTime())
                .build();

        // https://wildeveloperetrain.tistory.com/58
        // 나중에 참고하기 좋읗 것 같아서 넣어둠
    }

    // jwt Token 정보를 통해서 사용자가 누구인지 알기 위한 함수
    public Authentication getUserInfo(String token) {
        // jwt token 검증
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        // 토큰 생성 시 저장한 subject 정보(pk) + 권한 정보 가져오기
        String memberId = claims.getSubject();
        String authority = claims.get("role").toString();

        // user 객체를 만들기 위해 권한 정보 타입 설정
        Collection<? extends GrantedAuthority> at = Arrays.stream(authority.split(","))
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

        UserDetails principal = new User(memberId, "", at);

        // user 객체를 활용하여 token 생성
        return new UsernamePasswordAuthenticationToken(principal, "", at);
    }
}
