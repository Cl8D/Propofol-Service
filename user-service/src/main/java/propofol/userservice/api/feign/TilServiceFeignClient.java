package propofol.userservice.api.feign;

// sevice <-> service 간 통신을 위해 feign Client 사용
// feign client의 annotation은 기존의 rest API와 반대처럼 생각해주면 좋다.

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import propofol.userservice.api.member.controller.dto.MemberBoardsResponseDto;

// user-service -> til-service에게 정보를 요청하기 위해 만든 구현체.
// 이때, discovery-server가 있으면 url을 따로 지정해줄 필요 없이 eureka에 올라온 application-name을 지정해주면 된다.
// 없으면 url을 지정해주면 된다. 실제 호출할 서비스의 url을 적용해주면 되며, 여기서는 http://localhost:8081을 적용해주면 된다.
// 자세한 건 https://techblog.woowahan.com/2630/ 참고해주기
@FeignClient(name="til-service")
public interface TilServiceFeignClient {

    // axios와 비슷한 느낌이다. GET으로 요청.
    // 해당 유저가 쓴 글 목록을 요청한다.
    @GetMapping("/api/v1/boards/myBoards")
    MemberBoardsResponseDto getMyBoards(
            // 서비스 계층에서 넘어온 token 값을 http의 Authorization 헤더에 넣어준다.
            @RequestHeader(name = "Authorization", required = true) String token,
            // 페이지 정보는 쿼리 파라미터로 함께 넘겨준다.
            @RequestParam(value = "page") Integer page);
    // 이는 곧, authorization 정보와 page 정보를 넘겨주며 GET method로 til-service에게 요청하는 형태이다.
    // 그리고 요청의 응답 결과로 MemberBoardsResponseDto를 받는 형태 (총 페이지/게시글 수/글 목록)

}
