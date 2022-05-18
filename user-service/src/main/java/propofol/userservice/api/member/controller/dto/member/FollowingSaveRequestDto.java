package propofol.userservice.api.member.controller.dto.member;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FollowingSaveRequestDto {
    // 팔로우할 사용자의 id 저장
    @NotBlank
    private Long followingMemberId;
}
