package propofol.tagservice.presentation;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import propofol.tagservice.application.TagService;
import propofol.tagservice.application.dto.TagPageResponse;
import propofol.tagservice.application.dto.TagSliceResponse;
import propofol.tagservice.application.dto.TagsResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    /**
     * 페이지 단위 태그 조회
     */
    @GetMapping
    public ResponseEntity<TagPageResponse> getPageTags(@RequestParam(value = "page", defaultValue = "1") int page) {
        final TagPageResponse tagPageResponse = tagService.getPageTags(page);
        return ResponseEntity.ok(tagPageResponse);
    }

    /**
     * 슬라이스 단위 태그 조회
     * 사용자가 검색한 단어와 일치하는 태그 목록을 슬라이스 단위로 보여준다. - 더보기 버튼을 누르면 태그 목록 조회
     */
    @GetMapping("/slice")
    public ResponseEntity<TagSliceResponse> getSliceTags(@RequestParam(value = "page", defaultValue = "1") int page,
                                                         @RequestParam(value = "keyword") String keyword) {
        final TagSliceResponse tagSliceResponse = tagService.getSliceTags(page, keyword);
        return ResponseEntity.ok(tagSliceResponse);
    }

    /**
     * 태그 이름 List 조회
     */
    @GetMapping("/ids")
    public ResponseEntity<TagsResponse> getTagsByNames(@RequestParam("ids") Set<Long> ids) {
        final TagsResponse tagsResponse = tagService.getTagsByIds(ids);
        return ResponseEntity.ok(tagsResponse);
    }
}
