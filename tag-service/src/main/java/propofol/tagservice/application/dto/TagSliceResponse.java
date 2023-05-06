package propofol.tagservice.application.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import propofol.tagservice.domain.Tag;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TagSliceResponse {
    // 더보기 옵션을 위해서 다음 슬라이스가 존재하는지 체크
    private boolean hasNext;
    // 현재 페이지 수
    private int currentPage;
    // 태그 목록
    private List<TagResponse> tags = new ArrayList<>();

    public static TagSliceResponse of(final Page<Tag> sliceTags) {
        final boolean hasNext = sliceTags.hasNext();
        final int currentPage = sliceTags.getNumber() + 1;
        final List<TagResponse> tags = sliceTags.stream()
            .map(tag -> TagResponse.of(tag.getName()))
            .collect(Collectors.toUnmodifiableList());
        return new TagSliceResponse(hasNext, currentPage, tags);
    }
}
