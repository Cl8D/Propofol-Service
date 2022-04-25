package propofol.userservice.api.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// Error에 대한 정보를 저장한다.
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorDto {
    // 에러 상태 코드
    private Integer status;
    // 메시지
    private String message;
    // 에러가 여러 개일 경우 리스트로 관리
    private List<ErrorDetailDto> errors = new ArrayList<>();
}
