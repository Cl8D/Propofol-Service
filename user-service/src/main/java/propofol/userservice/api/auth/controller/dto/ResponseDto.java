package propofol.userservice.api.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 응답 DTO 통일
@Data
@AllArgsConstructor
public class ResponseDto<T> {
    // 상태 코드
    private int status;
    // 결과 (fail/success)
    private String result;
    // 결과 메시지
    private String message;
    // 응답 데이터
    private T data;
}
