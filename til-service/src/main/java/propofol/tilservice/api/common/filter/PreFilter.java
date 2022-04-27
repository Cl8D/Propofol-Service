package propofol.tilservice.api.common.filter;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import propofol.tilservice.api.common.jwt.JwtProvider;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
// once 어쩌고 -> 한 번의 요청에 대해 필터가 한 번만 실행된다.
public class PreFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    // jwt 토큰을 가진 사용자가 누구인지 검증하기
    // spring security를 거치는 요청 > FilterChainProxy class 거침 > doFilter method > doFilterInternal method 호출
    // 내부에서 getFilters 메서드를 통해 요청을 처리할 Filter 목록을 불러온 다음,
    // 적합한 필터를 못 찾으면 FilterChainProxy 종료, 찾았다면 Filter를 VirtualFilterChain 클래스에게 위임.
    // 위임받으면 순서대로 filter를 실행시켜준다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        // jwt 토큰이 담겨있고 bearer + 형식으로 시작한다면
        if(authorization != null && authorization.startsWith("Bearer ")) {
            // 순수 jwt token 뽑기
            String token = authorization.replaceAll("Bearer ", "").toString();
            // 토큰 검증을 통해 사용자 찾기
            // 이러면 사용자의 pricinpal / credential 정보를 Authentication 객체에 담고,
            // 이를 SecurityContext에 보관하게 되는데, 이를 또 SecurityContextHolder에 보관한다고 한다.
            Authentication authentication = jwtProvider.getUserInfo(token);

            // 홀더에 있는 context를 강제로 뽑아서 authentication 객체를 위에서 리턴받은 객체로 설정해주기.
            // cf) https://flyburi.com/584
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // request-response가 체인을 따라서 다음 필터로 이동
        filterChain.doFilter(request, response);
    }
}
