package propofol.tagservice.api.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TagResponseDto {
    private String name;

    public TagResponseDto(String name) {
        this.name = name;
    }
}
