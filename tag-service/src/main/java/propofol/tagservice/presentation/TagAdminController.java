package propofol.tagservice.presentation;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import propofol.tagservice.application.dto.TagRequest;
import propofol.tagservice.application.dto.TagResponse;
import propofol.tagservice.application.TagService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/tags")
public class TagAdminController {

    private final TagService tagService;

    /**
     * 태그 생성
     */
    @PostMapping
    public ResponseEntity<TagResponse> saveTag(@Valid @RequestBody final TagRequest tagRequest) {
        final TagResponse tagResponse = tagService.saveTag(tagRequest);
        return ResponseEntity.ok().body(tagResponse);
    }

    /**
     * 태그 수정
     */
    @PostMapping("/{tagId}")
    public ResponseEntity<Void> updateTag(@PathVariable(value = "tagId") final Long tagId,
                                          @Valid @RequestBody TagRequest tagRequest) {
        tagService.updateTag(tagId, tagRequest.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * 태그 삭제
     */
    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable(value = "tagId") final Long tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }
}
