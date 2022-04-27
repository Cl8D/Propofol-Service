package propofol.tilservice.api.controller.dto;

import lombok.Data;

// 게시글 수정 시 오는 데이터를 담는 Dto
// 글 제목, 내용, 공개 여부를 수정할 수 있다.
// 이때, 생성과 다르게 수정의 경우 한 가지만 수정하는 경우가 존재할 수 있기 때문에
// @NotEmpty를 빼준다.
@Data
public class BoardUpdateRequestDto {
    private String title;
    private String content;
    private Boolean open;
}
