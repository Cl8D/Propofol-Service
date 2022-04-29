package propofol.tilservice.domain.board.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// api-domain 분리를 위한 댓글 DTO
@Data
@NoArgsConstructor
public class CommentDto {
    private String content;
}