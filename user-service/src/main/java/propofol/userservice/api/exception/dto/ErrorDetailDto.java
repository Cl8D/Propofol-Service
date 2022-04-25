package propofol.userservice.api.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Error 정보 저장 Dto
@Data
@AllArgsConstructor
public class ErrorDetailDto {
    private String field; // 필드
    private String errorMessage; // 에러 메시지
}
