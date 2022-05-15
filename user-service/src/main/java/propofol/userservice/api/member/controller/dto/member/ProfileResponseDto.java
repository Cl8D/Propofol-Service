package propofol.userservice.api.member.controller.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 회원의 프로필 정보 응답 DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDto {
    private Long memberId; // 멤버 정보
    private byte[] profileBytes; // 이미지 바이트
    private String profileType; // 이미지 타입
}