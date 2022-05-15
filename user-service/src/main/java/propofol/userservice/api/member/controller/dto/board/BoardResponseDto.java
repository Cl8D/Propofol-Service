package propofol.userservice.api.member.controller.dto.board;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 하나의 게시글에 대한 정보 모음
// til-service의 domain 정보를 받아오기 위해 사용하는 Dto 느낌!
@Data
@NoArgsConstructor
public class BoardResponseDto {
    // 게시글 id
    private Long id;
    // 제목
    private String title;
    // 내용
    private String content;
    // 첨부 이미지 중 가장 처음 이미지 (썸네일)의 바이트
    private byte[] imageBytes;
    // 이미지 유형
    private String imageType;
    // 추천 수
    private Integer recommend;
    // 댓글 수
    private Integer commentCount;
    // 공개 여부
    private Boolean open;
    // 게시글 생성일
    private LocalDateTime createdDate;
}
