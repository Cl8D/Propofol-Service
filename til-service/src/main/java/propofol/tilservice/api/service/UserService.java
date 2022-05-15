package propofol.tilservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.tilservice.api.feign.UserServiceFeignClient;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceFeignClient userServiceFeignClient;

    // 유저의 닉네임을 가져오기 위한 함수 (feignClient 호출)
    public String getUserNickname(String token, String memberId) {
        return userServiceFeignClient.getMemberNickname(token, memberId);
    }
}
