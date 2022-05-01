package propofol.tilservice.api.controller.dto.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// 댓글 응답 정보 DTO (페이징 포함!)
@Data
@NoArgsConstructor
public class CommentPageResponseDto {
    // 게시글 ID
    private Long boardId;
    // 총 댓글 페이징 개수
    private Integer totalCommentPageCount;
    // 총 댓글 개수
    private Long totalCommentCount;
    // 댓글 리스트
    List<CommentResponseDto> comments = new ArrayList<>();
}
