package propofol.tilservice.api.common.exception.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import propofol.tilservice.api.common.exception.BoardCreateException;
import propofol.tilservice.api.common.exception.BoardUpdateException;
import propofol.tilservice.api.common.exception.NotMatchMemberException;
import propofol.tilservice.api.common.exception.SameMemberException;
import propofol.tilservice.api.common.exception.dto.ErrorDetailDto;
import propofol.tilservice.api.common.exception.dto.ErrorDto;
import propofol.tilservice.api.controller.dto.ResponseDto;
import propofol.tilservice.domain.exception.NotFoundBoardException;
import propofol.tilservice.domain.exception.NotFoundCommentException;
import propofol.tilservice.domain.exception.NotFoundFileException;
import propofol.tilservice.domain.exception.NotSaveFileException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ExceptionAdviceController {

    // @Controller나 @RestController가 적용된 bean에서 발생하는 예외를 캐치해서 처리할 수 있다.
    @ExceptionHandler
    // status를 설정할 수 있다.
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 잘못된 요청을 보냈을 때
    public ErrorDto badRequestType1Error(HttpMessageNotReadableException e) {
        ErrorDto errorDto = createError("잘못된 요청입니다.");
        return errorDto;
    }

    /************/

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 게시글을 찾을 수 없을 때
    public ResponseDto NotFoundBoardException(NotFoundBoardException e){
        ErrorDto errorDto = createError(e.getMessage());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "게시글 조회 실패!", errorDto);
    }
    /************/

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 게시글 생성 실패 - 필수적으로 들어가야 하는 필드가 누락되었을 때 발생
    public ResponseDto validationError(MethodArgumentNotValidException e){
        ErrorDto errorDto = createError("게시글 생성 실패!");
        // 누락된 필드들에 대한 에러 정보 생성
        e.getFieldErrors().forEach(error -> {
            errorDto.getErrors().add(new ErrorDetailDto(error.getField(), error.getDefaultMessage()));
        });
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "검증 오류", errorDto);
    }

    /************/

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 게시글 수정, 삭제를 생성한 유저가 하지 않을 때
    public ResponseDto NotMatchMemberException(NotMatchMemberException e){
        ErrorDto errorDto = createError(e.getMessage());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "잘못된 요청", errorDto);
    }

    /************/

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 동일한 인물이 추천을 하려고 할 때
    public ResponseDto SameMemberException(SameMemberException e){
        ErrorDto errorDto = createError(e.getMessage());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "잘못된 요청", errorDto);
    }

    /************/

    // 파일 저장 오류
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto NotSaveFileException(NotSaveFileException e){
        ErrorDto errorDto = createError(e.getMessage());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "파일 저장 실패!", errorDto);
    }

    /************/

    // 파일을 찾을 수 없을 때
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto NotFoundFileException(NotFoundFileException e){
        ErrorDto errorDto = createError(e.getMessage());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "파일 조회 실패!", errorDto);
    }

    /************/

    // 댓글을 찾을 수 없을 때
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto NotFoundCommentException(NotFoundCommentException e){
        ErrorDto errorDto = createError(e.getMessage());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "댓글 조회 실패!", errorDto);
    }

    /************/

    // 게시글 생성 실패
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto BoardCreateException(BoardCreateException e){
        ErrorDto errorDto = createError(e.getMessage());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "게시글 생성 실패!", errorDto);
    }

    /************/

    // 게시글 수정 실패
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto BoardUpdateException(BoardUpdateException e){
        ErrorDto errorDto = createError(e.getMessage());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "게시글 수정 실패!", errorDto);
    }


    // 에러 생성 메서드
    private ErrorDto createError(String errorMessage) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setErrorMessage(errorMessage);
        return errorDto;
    }
}

