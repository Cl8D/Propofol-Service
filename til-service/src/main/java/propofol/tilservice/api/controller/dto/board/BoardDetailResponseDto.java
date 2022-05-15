package propofol.tilservice.api.controller.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 게시글 세부 정보 응답 DTO 추가
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDetailResponseDto {
    private String title;
    private String content;
    private String nickname;
    // 하나의 게시글에 대한 모든 이미지 리스트와 타입 정보 리스트.
    private List<byte[]> images = new ArrayList<>();
    private List<String> imageTypes = new ArrayList<>();
    private Boolean open;
    private int recommend;
    private int commentCount;
    private LocalDateTime createdDate;
}
