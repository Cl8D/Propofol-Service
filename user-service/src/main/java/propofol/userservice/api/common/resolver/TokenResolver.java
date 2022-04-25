package propofol.userservice.api.common.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import propofol.userservice.api.common.annotation.Token;

// @Token annotation을 처리하기 위한 커스텀 리졸버.
@Component
public class TokenResolver implements HandlerMethodArgumentResolver{

    // 컨트롤러의 메서드에 있는 파라미터 (우리 코드에서는 @Token Long memberId)를 지원하는지 여부 판단
    // 파라미터에 대한 작업을 수행하는 부분
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
       // 파라미터의 어노테이션이 Token class인지
        return parameter.hasParameterAnnotation(Token.class)
                // 실질적인 파라미터의 타입이 Long인지
                && parameter.getParameterType().isAssignableFrom(Long.class);
    }

    // 파라미터에 대한 실질적인 로직 처리
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 현재 시큐리티 컨텍스트에 존재하는 사용자 뽑아내기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 존재하지 않는다면 잘못된 접근으로 들어온 사용자
        if(authentication == null)
            throw new RuntimeException("잘못된 접근입니다.");

        // 유효한 객체라면 해당 authentication에 존재하는 PK값 뽑아서 (username 자리에 pk값을 넣었으니까) 리턴!
        return Long.valueOf(authentication.getName());
    }
}
