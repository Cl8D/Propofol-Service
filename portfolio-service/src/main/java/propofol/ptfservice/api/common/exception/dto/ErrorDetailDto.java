package propofol.ptfservice.api.common.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDetailDto {
    private String field; // 필드
    private String errorMessage; // 에러 메시지
}
