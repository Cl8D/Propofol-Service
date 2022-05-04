package propofol.tagservice.api.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TagSliceResponseDto {
    // 더보기 옵션을 위해서 다음 슬라이스가 존재하는지 체크
    private Boolean hasNext;
    // 현재 페이지 수
    private Integer nowPageNumber;
    // 태그 목록
    private List<TagResponseDto> tags = new ArrayList<>();

}
