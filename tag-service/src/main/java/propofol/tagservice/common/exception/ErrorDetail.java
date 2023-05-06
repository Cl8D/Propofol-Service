package propofol.tagservice.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorDetail {
    private String field;
    private String errorMessage;

    public static ErrorDetail of(final String field, final String errorMessage) {
        return new ErrorDetail(field, errorMessage);
    }
}
