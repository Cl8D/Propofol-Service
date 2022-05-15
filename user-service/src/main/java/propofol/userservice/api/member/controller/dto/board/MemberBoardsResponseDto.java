package propofol.userservice.api.member.controller.dto.board;

// 멤버가 쓴 게시글에 대한 정보 Dto

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MemberBoardsResponseDto {
    // 총 페이지 수, 총 게시글 수, 글 목록
    private Integer totalPageCount;
    private Long totalCount;

    private List<BoardResponseDto> boards = new ArrayList<>();
}
