package propofol.tilservice.api.controller.dto.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 하나의 댓글에 대한 응답 DTO
@Data
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String nickname;
    private String content;
    private Long groupId;
    private LocalDateTime createdDate;

    public CommentResponseDto(Long id, String nickname, String content, Long groupId, LocalDateTime createdDate) {
        this.id = id;
        this.nickname = nickname;
        this.content = content;
        this.groupId = groupId;
        this.createdDate = createdDate;
    }
}
