package propofol.tilservice.api.controller.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 게시글 목록을 띄워줄 때 보여주는 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
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
