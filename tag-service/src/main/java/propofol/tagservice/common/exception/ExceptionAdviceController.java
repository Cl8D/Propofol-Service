package propofol.tagservice.common.exception;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionAdviceController {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> validationError(final MethodArgumentNotValidException e){
        final List<ErrorDetail> errorDetails = createErrorDetails(e);
        return ResponseEntity.badRequest().body(ErrorResponse.of("잘못된 요청입니다.", errorDetails));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notFoundTagError(final NotFoundTagException e){
        return ResponseEntity.badRequest().body(ErrorResponse.of(e.getMessage(), null));
    }

    private List<ErrorDetail> createErrorDetails(final MethodArgumentNotValidException e) {
        return e.getFieldErrors().stream()
            .map(error -> ErrorDetail.of(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toUnmodifiableList());
    }
}
