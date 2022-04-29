package propofol.tilservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import propofol.tilservice.api.controller.dto.ResponseImageDto;
import propofol.tilservice.api.controller.dto.ResponseImagesDto;
import propofol.tilservice.domain.file.entity.Image;
import propofol.tilservice.domain.file.service.ImageService;

import java.net.MalformedURLException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageService imageService;

    /**TODO 나중에 프론트로 연동 이후 확인해보기 **/

    // 게시글에 저장된 이미지 여러 개
    @GetMapping("/{boardId}")
    public ResponseImagesDto getImages(@PathVariable(value = "boardId") Long boardId) {
        return imageService.getImages(boardId);
    }

    // 이미지 1개 - 게시글에 있는 이미지 클릭 시
    @GetMapping("/{boardId}/{imageId}")
    public UrlResource getImage(@PathVariable("boardId") Long boardId,
                                @PathVariable("imageId") Long imageId) throws MalformedURLException {

        // 이미지 찾아오기
        Image image = imageService.findByImage(imageId);
        // 이미지의 서버 저장 이름 가져오기
        String storeFileName1 = image.getStoreFileName();
        // 게시글 경로 지정
        String boardPath = imageService.findBoardPath();

        // 이미지를 보여주는 UrlResource 리턴.
        UrlResource resource = new UrlResource("file:" + boardPath + "/" + boardId + "/" + storeFileName1);

        return resource;
    }
}
