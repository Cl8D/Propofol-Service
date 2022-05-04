package propofol.userservice.api.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import propofol.userservice.api.common.exception.ExpiredRefreshTokenException;
import propofol.userservice.api.common.properties.JwtProperties;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// JWT 토큰 생성기
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    /** v1. 설정파일 주입 시 @Value 사용 */
    // 설정파일을 주입시킬 때 @Value를 사용한다.
    // @Value("${properties-key}"와 같은 형식으로 사용한다.
    // 만약 설정파일에 test.name="hi"라고 저장해왔다면,
    // @Value("${test.name}")을 통해 값을 알려줄 수 있다.
//    @Value("${test}")
//    private String test;

    // environment를 통해서 property를 PropertySource로 통합 관리한다.
    // 걍 @Value랑 똑같은 역할을 한다고 보면 될 것 같다.
//    private final Environment env;

//    private final Key key;
//    private final String expirationTime;
//    private final String type;


    /** v3. 설정 정보 적용 */
    // Jwt 설정 정보 적용하기 (Env 사용 x)
    private final JwtProperties jwtProperties;
    private Key key;

    /** v2. 프로퍼티 정보 가져올 때 생성자를 통해서 의존성 주입하기*/
    // 프로퍼티 정보 가져오기 - 생성자를 통해 의존성 주입
    // 원래는 기존 코드에서 로드했었는데 그냥 한 번에 로드하는 형식으로 코드 변경
//    public JwtProvider(@Value("${token.secret}") String secret,
//                       @Value("${token.expiration_time}") String expirationTime,
//                       @Value("${token.type}") String type) {
//
//        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
//        this.key = Keys.hmacShaKeyFor(bytes);
//        this.expirationTime = expirationTime;
//        this.type = type;
//    }


    /** v3. PostConstruct를 통해서 key값 초기화하기*/
    // PostConstrct -> DI 이후 초기화 수행하는 메서드
    // bean이 초기화될 때 동시에 DI를 확인할 수 있음!
    // jwtProperties에서 값을 꺼내와서 적용해주기
    @PostConstruct
    private void createKey() {
        byte[] bytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        key = Keys.hmacShaKeyFor(bytes);
    }


    /*************************/


    // 유저 정보를 활용하여 accessToken, refreshToken을 생성한다.
    public TokenDto createJwt(Authentication authentication) {
        // authentication.getAuthorities를 사용하면 collections 형태로 나오게 되는데,
        // 각 권한을 grantedAuthority 변수에 담아서 하나의 string으로 만들어주기
        String authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                // joining => a, b, c... 이런 식으로 생성 가능
                .collect(Collectors.joining(", "));


        /** v1. env 사용해서 yml 파일 읽기 */
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
//                + Long.parseLong(expirationTime));
                + Long.parseLong(jwtProperties.getExpirationTime()));

        // jwt 토큰 생성
        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("role", authorities)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

//        String type = env.getProperty("token.type"); // bearer

        return TokenDto.createTokenDto()
//                .type(type)
                .type(jwtProperties.getType())
                .accessToken(token)
                .refreshToken(createRefreshToken())
                .build();

        // https://wildeveloperetrain.tistory.com/58
        // 나중에 참고하기 좋읗 것 같아서 넣어둠
    }


    /*************************/

    // refresh-token 생성 함수
    public String createRefreshToken() {
        // refresh-token의 유효기간 설정 = 하루(config에서 읽어온 정보)
        Date refreshExpirationTime = new Date(System.currentTimeMillis() +
                Long.parseLong(jwtProperties.getRefreshExpirationTime()));

        return Jwts.builder()
                .setExpiration(refreshExpirationTime)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /*************************/

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

    /*************************/

    // JWT 토큰이 만료되었는지 체크
    // 유효하면 true, 만료되면 false
    public boolean isJwtValid(String bearerToken) {
        try {
            // bearer 제거
            String token = bearerToken.replaceAll("Bearer ", "").toString();
            // 토큰 검증
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
            Claims claims = jwtParser.parseClaimsJwt(token).getBody();

            // 토큰의 만료기간이 현재 날짜보다 더 이전이라면 = 만료되었음
            // 이후라면 = 만료되지 않았음 (유효함)
            boolean isNotValid = claims.getExpiration().before(new Date());
            if(isNotValid)
                return false;
            else
                return true;

        } catch (Exception e) {
            // jwt 토큰이 만료되면 기본적으로 exception이 처리되기 때문에 try-catch로 찹아주기
            if(e instanceof ExpiredJwtException) {
                // 예외 발생 = 토큰이 만료되었음을 의미함
                return false;
            }
        }
        return false;
    }


    /*************************/

    // refresh-token의 유효성 확인
    public boolean isRefreshTokenValid(String refreshToken) {
        try {
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
            Claims claims = jwtParser.parseClaimsJwt(refreshToken).getBody();
            boolean isNotValid = claims.getExpiration().before(new Date());

            if(isNotValid)
                return false;
            else
                return true;

        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                // 만약 refresh-token이 만료되었으면 예외 발생.
                throw new ExpiredRefreshTokenException("Please Re-Login!");
            }
        }

        return false;
    }



}
