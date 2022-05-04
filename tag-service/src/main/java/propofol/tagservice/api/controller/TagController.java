package propofol.tagservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import propofol.tagservice.api.controller.dto.TagPageResponseDto;
import propofol.tagservice.api.controller.dto.TagResponseDto;
import propofol.tagservice.api.controller.dto.TagSliceResponseDto;
import propofol.tagservice.domain.tag.entity.Tag;
import propofol.tagservice.domain.tag.service.TagService;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;

    // 페이지 단위 태그 조회
    @GetMapping
    public TagPageResponseDto getPageTags(@RequestParam(value = "page") Integer page) {
        Page<Tag> pageTags = tagService.getPageTags(page);

        TagPageResponseDto pageResponseDto = new TagPageResponseDto();
        pageResponseDto.setTotalCount(pageTags.getTotalElements());
        pageResponseDto.setPageTotalCount(pageTags.getTotalPages());

        pageTags.forEach(tag-> {
            pageResponseDto.getTags().add(new TagResponseDto(tag.getName()));
        });

        return pageResponseDto;
    }

    /********************/

    // 슬라이스 단위 태그 조회 (사용자가 검색할 때 보여주는 태그 목록)
    // 사용자가 검색한 단어와 일치하는 태그 목록을 슬라이스 단위로 보여준다.
    // 이때, 사용자가 더보기 버튼을 누르면 태그 목록을 더 가져오는 형식!
    // 참고로, slice의 경우 page에서 카운트 쿼리에 많은 비용이 발생할 때 사용한다고 한다.
    // 다음 slice가 존재하는지 여부만 알고 있어서, 더보기 같은 곳에 쓰기 좋음!!
    @GetMapping("/slice")
    public TagSliceResponseDto getSliceTags(@RequestParam(value = "page") Integer page,
                                            @RequestParam(value = "keypoint") String keypoint) {
        // keypoint -> 사용자의 검색어!
        // 검색어에서 대소문자 구분을 없애기 위해서 다 대문자로 변경하기
        Page<Tag> sliceTags = tagService.getSliceTags(page, keypoint.toUpperCase(Locale.ROOT));

        TagSliceResponseDto tagSliceResponseDto = new TagSliceResponseDto();
        tagSliceResponseDto.setHasNext(sliceTags.hasNext());
        // 1부터 시작하도록!
        tagSliceResponseDto.setNowPageNumber(sliceTags.getNumber() + 1);

        sliceTags.forEach(tag -> {
            tagSliceResponseDto.getTags().add(new TagResponseDto(tag.getName()));
        });

        return tagSliceResponseDto;

    }

}
