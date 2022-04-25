package propofol.apigateway.filter;

// api-gateway는 클라이언트가 가져온 jwt 토큰을 검증하게 된다.
// 일종의 출입문 같은 역할로, 여기서 인증된 사용자만 그 다음 service 계층으로 갈 수 있는 형태가 되는 것.
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
@Slf4j
// JWT 토큰을 파싱하는 Filter라고 볼 수 있다.
// Spring Cloud GateWay의 경우 Spring Webflux로 구현이 되어 있기 때문에, Filter를 모두 Flux로 구현해야 한다.
// cf) 기존에는 동기 방시인 Tomcat을 사용했지만, 여기서는 비동기 방식인 Netty를 사용해서 serverHttpRequest/Response를 사용한다.
// 여기서 사용자 커스텀 필터를 만들기 위해서는 Abstract 어쩌고를 써야 한다.
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    // 프로퍼티를 가져오기 위함
    Environment env;
    private Key key;

    public JwtFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    // 토큰 검증 로직을 추가한다.
    @Override
    public GatewayFilter apply(Config config) {
        // 파라미터는 각각 ServerWebExchage, GatewayFilterChain을 담고 있다.
        // 여기서 exchange의 request를 받아오면 prefilter로, response는 postfilter로 적용
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // request의 header - authorization 확인
            HttpHeaders headers = request.getHeaders();

            // 만약 존재하지 않는다면 error -> jwt token이 없는 요청임
            if(!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Jwt Token", HttpStatus.UNAUTHORIZED);
            }

            // bearer dfowijfoi28934892042 같은 형태로 저장되어 있음
            String authorization = headers.get(HttpHeaders.AUTHORIZATION).get(0);
            // bearar 제거 -> 순수 jwt token 값 뽑아내기
            String token = authorization.replace("Bearer ", "");


            // jwt token이 오기는 했으나, 해당 jwt token이 유효한지 확인하기
            if(!isValid(token, exchange)) {
                return onError(exchange, "Not Validate Jwt Token", HttpStatus.UNAUTHORIZED);
            }

            // 성공적으로 검증 완료라면
            return chain.filter(exchange);

        });
    }

    private boolean isValid(String token, ServerWebExchange exchange) {
        String subject = null;

        // application-secret.yml에 저장된 서버의 비밀키값 가져오기
        String secretKey = env.getProperty("token.secret");
        byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);
        // 키 생성
        key = Keys.hmacShaKeyFor(bytes);

        // JWT 토큰 검증 로직
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        Claims claims = jwtParser.parseClaimsJwt(token).getBody();
        // jwt 토큰 생성 시 지정한 subject 값 가져오기 (우리는 pk값으로 지정하였음)
        // 성공적으로 값이 반환되었다는 것은 서버의 키값으로 jwt 토큰이 암호화되었음을 의미하는 것이니까.
        subject = claims.getSubject();

        if(subject == null)
            return false;

        return true;
    }

    // Mono는 0~1개의 결과를 처리하기 위한 Reactor 객체이다. (여러 개는 Flux 사용)
    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        try {
            byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
            response.setStatusCode(httpStatus);
            // 바이트 배열을 databuffer형으로 래핑. (새로운 메모리 사용 x)
            DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
            // message body를 직접 적기 위해서 writeWith 사용
            // flux.jst를 사용하면 값을 바로 방출하게 된다.
            return response.writeWith(Flux.just(dataBuffer));

        } catch(Exception e) {
            response.setStatusCode(httpStatus);
            // setComplete 사용 시 해당 응답이 바로 종료된다.
            return response.setComplete();

        }

    }


    // Configuration 속성들을 넣어준다. (Global Filter에서 활용하는 것 같다)
    static class Config {

    }
}

// 참고) https://wonit.tistory.com/500
/*
토큰 검증 과정)
1. 사용자 요청
2. Gateway Handler Mapping이 Predicates를 검사
3. PreFilter에서 Request Header에 있는 authorization 파싱하기 (PreFilter)
-> 유효하지 않다면 error (검증은 postFilter에서)
-> 존재한다면 사용자가 요청한 서버로 요청 전달. (service로 가는 흐름)
 */