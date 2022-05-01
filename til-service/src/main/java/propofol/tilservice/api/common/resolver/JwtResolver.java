package propofol.tilservice.api.common.resolver;

import org.apache.http.HttpHeaders;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import propofol.tilservice.api.common.annotation.Jwt;

@Component
public class JwtResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Jwt.class)
                && parameter.getParameterType().isAssignableFrom(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // header의 authorization 정보를 가져와서 jwt token을 가져오는 어노테이션!
        String token = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        return token;
    }
}
