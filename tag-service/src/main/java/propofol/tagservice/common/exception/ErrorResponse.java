package propofol.tagservice.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {
    private String message;
    private List<ErrorDetail> errors = new ArrayList<>();

    public static ErrorResponse of(final String message, final List<ErrorDetail> errors) {
        return new ErrorResponse(message, errors);
    }
}
