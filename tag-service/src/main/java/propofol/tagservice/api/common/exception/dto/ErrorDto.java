package propofol.tagservice.api.common.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorDto {
    private Integer status;
    private String message;
    private List<ErrorDetailDto> errors = new ArrayList<>();
}
