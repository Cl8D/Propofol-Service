package propofol.tilservice.api.controller.dto;

import lombok.Data;

// 이미지 한 개에 대한 DTO, 이미지 클릭 시 보여지기 위한 정보 DTO
@Data
public class ResponseImageDto {
    // 이미지를 바이트 형태로 표현
    private byte[] image;
    // 이미지 타입
    private String imageType;
}
