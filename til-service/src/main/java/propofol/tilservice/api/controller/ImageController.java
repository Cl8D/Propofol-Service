package propofol.tilservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import propofol.tilservice.api.service.ImageService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageService imageService;

    // 게시글에 저장된 이미지 여러 개
//    @GetMapping("/{boardId}")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseDto getImages(@PathVariable(value = "boardId") Long boardId) {
//        return new ResponseDto<>(HttpStatus.OK.value(), "success",
//                "이미지 조회 성공!", imageService.getImages(boardId));
//    }
//
//    // 이미지 1개 - 게시글에 있는 이미지 클릭 시
//    @GetMapping("/{boardId}/{imageId}")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseDto getImage(@PathVariable("boardId") Long boardId,
//                                @PathVariable("imageId") Long imageId) throws MalformedURLException {
//
//        // 이미지 찾아오기
//        Image image = imageService.findByImage(imageId);
//        // 이미지의 서버 저장 이름 가져오기
//        String storeFileName1 = image.getStoreFileName();
//        // 게시글 경로 지정
//        String boardPath = imageService.findBoardPath();
//
//        // 이미지를 보여주는 UrlResource 리턴.
//        UrlResource resource = new UrlResource("file:" + boardPath + "/" + boardId + "/" + storeFileName1);
//
//        return new ResponseDto<>(HttpStatus.OK.value(), "success",
//                "이미지 조회 성공!", resource);
//    }

    @GetMapping("/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public byte[] getImages(@PathVariable("fileName") String fileName) {
        // 파일 이름을 전달해주면 해당 파일을 바이트 배열로 리턴 -> 블로그 에디터에 첨부한 사진을 뜨도록 하기 위해서!
        // 대충 http://SERVER_URL:8000/til-service/api/v1/images/파일명.png > 여기서 pathVariable로 인해서 파일명.png만 fileName에 담긴다.
        // 함수 내부에서 디렉토리의 절대 경로 + 파일 이름으로 접근해줘서 여기서는 파일 이름만 전달해주면 된다.
        return imageService.getImageBytes(fileName);
    }
}
