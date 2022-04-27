package propofol.tilservice.api.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import propofol.tilservice.api.common.properties.JwtProperties;

import javax.annotation.PostConstruct;
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
    /** v3. 설정 정보 적용 */
    // Jwt 설정 정보 적용하기 (Env 사용 x)
    private final JwtProperties jwtProperties;
    private Key key;

    // PostConstrct -> DI 이후 초기화 수행하는 메서드
    // bean이 초기화될 때 동시에 DI를 확인할 수 있음!
    // jwtProperties에서 값을 꺼내와서 적용해주기
    @PostConstruct
    private void createKey() {
        byte[] bytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        key = Keys.hmacShaKeyFor(bytes);
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
