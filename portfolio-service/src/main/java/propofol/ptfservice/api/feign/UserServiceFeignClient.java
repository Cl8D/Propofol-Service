package propofol.ptfservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import propofol.ptfservice.api.controller.dto.MemberInfoResponseDto;

// ptf-service -> user-service 정보 요청 (포폴에 들어갈 내 정보 요청)
@FeignClient(name="user-service")
public interface UserServiceFeignClient {

    // 유저 정보 요청
    @GetMapping("/api/v1/members")
    MemberInfoResponseDto getMemberInfo(
            @RequestHeader(name = "authorization", required = true) String token);


}
