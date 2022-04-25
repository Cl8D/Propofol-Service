package propofol.userservice.api.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Error에 대한 세부 정보를 저장하는 필드 DTO
@Data
@AllArgsConstructor
public class ErrorDetailDto {
    private String field; // 필드
    private String errorMessage; // 에러 메시지
}
