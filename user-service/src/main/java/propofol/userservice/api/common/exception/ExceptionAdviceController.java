package propofol.userservice.api.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import propofol.userservice.api.common.exception.dto.ErrorDto;
import propofol.userservice.api.common.exception.dto.ErrorDetailDto;
import propofol.userservice.domain.exception.NotFoundMember;

import javax.validation.ConstraintViolationException;

// 자체 Exception 처리
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 멤버를 찾지 못했을 때
    public ErrorDto NotFoundMemberException(NotFoundMember e){
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(e.getMessage());
        errorDto.setStatus(HttpStatus.BAD_REQUEST.value());
        return errorDto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 회원 가입 실패 시 - 필수적으로 들어가야 하는 필드가 누락되었을 때
    // 해당 필드 각각에 대한 에러 정보를 넘겨줘야 하기 때문에 list 형태로 처리
    public ErrorDto validationError(MethodArgumentNotValidException e){
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage("회원 가입 실패!");
        errorDto.setStatus(HttpStatus.BAD_REQUEST.value());

        e.getFieldErrors().forEach(
                error -> {
                    errorDto.getErrors().add(new ErrorDetailDto(error.getField(), error.getDefaultMessage()));
                }
        );
        return errorDto;
    }
}
