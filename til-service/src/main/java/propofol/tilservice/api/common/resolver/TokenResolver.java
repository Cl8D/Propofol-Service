package propofol.tilservice.api.common.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import propofol.tilservice.api.common.annotation.Token;

// @Token annotation을 처리하기 위한 커스텀 리졸버.
// user-service와 다르게 til-service에서 오는 모든 요청은 "/api/v1/**" 경로로 들어오고,
// 이는 spring security에서 .authenticated()로 막아두었기 때문에 무조건 권한이 있어야 한다.
// 또한, Prefilter를 먼저 거치고 Authorization에 담긴 jwt token을 확인하여 Authentication 객체를 만들기 때문에
// 실질적으로 resolveArgument() 메서드에서 리턴되는 Authentication 객체는 null이 될 수 없기는 하다! ㅎㅎ
@Component
public class TokenResolver implements HandlerMethodArgumentResolver{

    // 컨트롤러의 메서드에 있는 파라미터 (우리 코드에서는 @Token Long memberId)를 지원하는지 여부 판단
    // 파라미터에 대한 작업을 수행하는 부분
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
       // 파라미터의 어노테이션이 Token class인지
        return parameter.hasParameterAnnotation(Token.class)
                // 실질적인 파라미터의 타입이 String인지
                && parameter.getParameterType().isAssignableFrom(String.class);
    }

    // 파라미터에 대한 실질적인 로직 처리
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 현재 시큐리티 컨텍스트에 존재하는 사용자 뽑아내기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 존재하지 않는다면 잘못된 접근으로 들어온 사용자
        if(authentication == null)
            throw new RuntimeException("잘못된 접근입니다.");

        return authentication.getName();
    }
}
