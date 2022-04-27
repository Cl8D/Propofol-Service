package propofol.tilservice.api.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// 하나의 게시글에 대한 정보를 담는 Dto
@Data
@NoArgsConstructor
public class BoardResponseDto {
    private Long boardUUID;
    private String title;
    private String content;
    private Integer recommend;
    private Boolean open;
}
