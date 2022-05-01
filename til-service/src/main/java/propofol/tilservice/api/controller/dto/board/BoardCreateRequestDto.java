package propofol.tilservice.api.controller.dto.board;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

// 게시글 작성 시 넘어오는 정보를 담는 dto
@Data
public class BoardCreateRequestDto {
    // 빈 제목, 빈 본문 불가능
    // 공개여부 역시 필수적으로 선택해야 한다.
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    @NotNull
    private Boolean open;
}
