package propofol.tilservice.domain.board.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// api-domain 분리를 위한 Dto
// 게시글 생성 시 존재하는 boardCreateRequestDto를 domain 단의 board와 맞추기 위해 존재하는 Dto이다.
@Data
@NoArgsConstructor
public class BoardDto {
    private String title;
    private String content;
    private Boolean open;
}
