package propofol.tagservice.api.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TagPageResponseDto {
    private Long totalCount;
    private Integer pageTotalCount;
    private List<TagResponseDto> tags = new ArrayList<>();
}
