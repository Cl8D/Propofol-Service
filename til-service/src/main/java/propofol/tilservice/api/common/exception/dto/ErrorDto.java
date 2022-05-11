package propofol.tilservice.api.common.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// Error 정보 저장 Dto (상위 타입)
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorDto {
    // 메시지
    private String errorMessage;
    // 에러가 여러 개일 경우 리스트로 관리
    private List<ErrorDetailDto> errors = new ArrayList<>();
}
