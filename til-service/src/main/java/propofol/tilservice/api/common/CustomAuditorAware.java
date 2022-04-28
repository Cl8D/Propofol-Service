package propofol.tilservice.api.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

// BaseEntity의 생성자, 수정자 자동 주입하는 기능 추가
// @createdBy 같은 어노테이션을 여기서 처리하게 된다.
@Component
@RequiredArgsConstructor
public class CustomAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // 현재 작업 대상을 가져오기. (Authentication은 prefilter로 인해서 생성이 된 상태)
        // 여기서는 Authentication 객체의 getName -> 사용자의 Db의 pk값인 id를 가져왔다.
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 여기서 작업 대상은 member의 id로 설정하였다.
        // DB 생성 시 = 즉, 게시글 생성 시 해당 member의 id로 초기화되도록 하였다.
        // createdBy 정보를 통해 게시글 수정 및 삭제 같은 권한을 설정하기!
        return Optional.ofNullable(memberId);
    }
}
