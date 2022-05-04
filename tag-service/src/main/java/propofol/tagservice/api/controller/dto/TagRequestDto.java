package propofol.tagservice.api.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class TagRequestDto {
    @NotBlank
    private String name;

}
