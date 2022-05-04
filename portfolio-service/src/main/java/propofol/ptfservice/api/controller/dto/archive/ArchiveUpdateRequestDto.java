package propofol.ptfservice.api.controller.dto.archive;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class ArchiveUpdateRequestDto {
    @Pattern(regexp="^((http(s?))\\:\\/\\/)([0-9a-zA-Z\\-]+\\.)+[a-zA-Z]{2,6}(\\:[0-9]+)?(\\/\\S*)?$",
            message = "Link 형식이 유효하지 않습니다.")
    private String link;
    private String content;
}
