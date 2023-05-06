package propofol.tagservice.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TagResponse {
    private String name;

    public static TagResponse of(final String name) {
        return new TagResponse(name);
    }
}
