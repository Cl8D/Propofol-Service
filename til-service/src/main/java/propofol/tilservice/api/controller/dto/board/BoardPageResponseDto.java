package propofol.tilservice.api.controller.dto.board;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// 페이지에 대한 응답 정보를 담는 Dto
@Data
public class BoardPageResponseDto {
    // 총 페이지 수
    private Integer totalPageCount;

    // 총 게시글 수
    private Long totalCount;

    // 게시글 리스트
    private List<BoardResponseDto> boards = new ArrayList<>();
}
