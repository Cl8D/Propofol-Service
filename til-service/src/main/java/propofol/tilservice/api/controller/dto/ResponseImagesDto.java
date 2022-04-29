package propofol.tilservice.api.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// 게시글에 올라온 image에 대한 정보 DTO
@Data
public class ResponseImagesDto {
    private List<byte[]> images = new ArrayList<>();
    private String imageType;
}
