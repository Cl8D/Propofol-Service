package propofol.tagservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.tagservice.api.controller.dto.TagRequestDto;
import propofol.tagservice.domain.tag.entity.Tag;
import propofol.tagservice.domain.tag.service.TagService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/tags")
public class TagAdminController {

    private final TagService tagService;

    // 태그 생성 기능
    @PostMapping
    public String saveTag(@Validated @RequestBody TagRequestDto requestDto) {
        Tag tag = Tag.createTag().name(requestDto.getName()).build();
        return tagService.saveTag(tag);
    }

    // 태그 수정 기능
    @PostMapping("/{tagName}")
    public String updateTag(@PathVariable(value = "tagName") String tagName,
                            @Validated @RequestBody TagRequestDto requestDto) {
        return tagService.updateTag(tagName, requestDto.getName());
    }

    // 태그 삭제 기능
    @DeleteMapping("/{tagName}")
    public String deleteTag(@PathVariable(value = "tagName") String tagName) {
        return tagService.deleteTag(tagName);
    }

}
