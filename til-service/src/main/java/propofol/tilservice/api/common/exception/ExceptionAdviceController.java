package propofol.tilservice.api.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import propofol.tilservice.api.common.exception.dto.ErrorDetailDto;
import propofol.tilservice.api.common.exception.dto.ErrorDto;
import propofol.tilservice.domain.exception.NotFoundBoard;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ExceptionAdviceController {

    // @Controller나 @RestController가 적용된 bean에서 발생하는 예외를 캐치해서 처리할 수 있다.
    @ExceptionHandler
    // status를 설정할 수 있다.
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // SQL 에러가 발생했을 때
    public ErrorDto SQLException(ConstraintViolationException e){
        log.info("Message = {}", e.getMessage());
        return null;
    }

    /************/

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 잘못된 요청을 보냈을 때
    public ErrorDto badRequestType1Error(HttpMessageNotReadableException e) {
        ErrorDto errorDto = createError("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    /************/

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 게시글을 찾을 수 없을 때
    public ErrorDto NotFoundBoardException(NotFoundBoard e) {
        ErrorDto errorDto = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    /************/

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 게시글 생성 실패 - 필수적으로 들어가야 하는 필드가 누락되었을 때 발생
    public ErrorDto validationError(MethodArgumentNotValidException e) {
        ErrorDto errorDto = createError("게시글 생성 실패!", HttpStatus.BAD_REQUEST);
        // 누락된 필드들에 대한 에러 정보 생성
        e.getFieldErrors().forEach(error -> {
            errorDto.getErrors().add(new ErrorDetailDto(error.getField(), error.getDefaultMessage()));
        });
        return errorDto;
    }

    /************/

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 게시글 수정, 삭제를 생성한 유저가 하지 않을 때
    public ErrorDto NotMatchMemberException (NotMatchMemberException e) {
        ErrorDto errorDto = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    private ErrorDto createError(String errorMessage, HttpStatus status) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(errorMessage);
        errorDto.setStatus(status.value());
        return errorDto;
    }
}

