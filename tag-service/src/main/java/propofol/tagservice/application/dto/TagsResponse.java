package propofol.tagservice.application.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import propofol.tagservice.domain.Tag;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TagsResponse {
    private List<TagResponse> tags = new ArrayList<>();

    public static TagsResponse of(final List<Tag> tags) {
        final List<TagResponse> tagResponses = tags.stream()
            .map(tag -> TagResponse.of(tag.getName()))
            .collect(Collectors.toUnmodifiableList());
        return new TagsResponse(tagResponses);
    }
}
