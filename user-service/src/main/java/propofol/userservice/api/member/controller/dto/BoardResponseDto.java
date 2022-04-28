package propofol.userservice.api.member.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// 하나의 게시글에 대한 정보 모음
// til-service의 domain 정보를 받아오기 위해 사용하는 Dto 느낌!
@Data
@NoArgsConstructor
public class BoardResponseDto {
    // id, 글 제목, 내용, 추천수, 공개여부
    private Long boardUUID;
    private String title;
    private String content;
    private Integer recommend;
    private Boolean open;
}
