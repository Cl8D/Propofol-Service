package propofol.userservice.api.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 파라미터단에서 사용하기 위해서 -> getMemberByEmail(@Token Long memberId) 이렇게!
@Target(ElementType.PARAMETER)
// 생명주기 설정
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {
}
