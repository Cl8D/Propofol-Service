package propofol.ptfservice.api.controller.dto.archive;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArchiveResponseDto {
    private String link;
    private String content;
}
