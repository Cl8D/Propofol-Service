package propofol.tagservice.application.dto;

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
public class TagPageResponse {
    private Long totalCount;
    private Integer pageTotalCount;
    private List<TagResponse> tags;

    public static TagPageResponse of(final Page<Tag> pageTags) {
        final long totalElements = pageTags.getTotalElements();
        final int totalPages = pageTags.getTotalPages();
        final List<TagResponse> tags = pageTags.stream()
            .map(tag -> TagResponse.of(tag.getName()))
            .collect(Collectors.toUnmodifiableList());
        return new TagPageResponse(totalElements, totalPages, tags);
    }
}
