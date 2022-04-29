package propofol.tilservice.api.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

// 댓글에 대한 정보를 담는 DTO
@Data
public class CommentRequestDto {
    @NotBlank
    private String content;
}
