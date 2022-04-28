package propofol.userservice.api.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import propofol.userservice.api.feign.TilServiceFeignClient;
import propofol.userservice.api.member.controller.dto.MemberBoardsResponseDto;

// user-service <-> til-service 간 통신을 위해 사용되는 서비스 계층.
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberBoardService {

    private TilServiceFeignClient tilServiceFeignClient;

    // 컨트롤러단에서 넘겨준 page 정보와 token 정보를 이용해 feignClient 호출
    public MemberBoardsResponseDto getMyBoards(Integer page, String token) {
        // 응답 결과로 총 페이지수, 게시글수, 게시글 리스트가 담긴 Dto를 return 받게 된다.
        return tilServiceFeignClient.getMyBoards(token, page);
    }
}
