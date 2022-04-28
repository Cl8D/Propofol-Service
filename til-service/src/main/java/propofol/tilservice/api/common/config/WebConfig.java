package propofol.tilservice.api.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import propofol.tilservice.api.common.resolver.TokenResolver;

import java.util.List;

// WebConfig 설정
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final TokenResolver tokenResolver;

    // @Token 처리 리졸버를 추가해주기
    // 등록은 보통 스프링 시작할 때 진행된다!
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(tokenResolver);
    }
    /*
        Argument Resolver 동작 방식
        1) Client에서 요청을 보낸다.
        2) 요청은 디스패처 서블릿에서 처리
        3) 요청에 대한 HandlerMapping을 처리한다.
        - 이때, 처리할 수 있는 커스텀 리졸버를 실행해준다.
        4) 내부에서 뭐 이런 저런 함수를 실행해준다
        5) 컨트롤러 메서드를 실행해준다.

        참고) https://blog.advenoh.pe.kr/spring/HandlerMethodArgumentResolver-%EC%9D%B4%EB%9E%80/

     */
}
