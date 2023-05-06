package propofol.tagservice.application;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tagservice.application.dto.TagPageResponse;
import propofol.tagservice.application.dto.TagRequest;
import propofol.tagservice.application.dto.TagResponse;
import propofol.tagservice.application.dto.TagSliceResponse;
import propofol.tagservice.application.dto.TagsResponse;
import propofol.tagservice.domain.Tag;
import propofol.tagservice.domain.TagRepository;
import propofol.tagservice.common.exception.NotFoundTagException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    /**
     * 태그 저장
     */
    @Transactional
    public TagResponse saveTag(final TagRequest tagRequest) {
        final Tag createdTag = Tag.createTag().name(tagRequest.getName()).build();
        final Tag savedTag = tagRepository.save(createdTag);
        return TagResponse.of(savedTag.getName());
    }

    /**
     * 태그 수정
     */
    @Transactional
    public void updateTag(final Long tagId, String name) {
        final Tag findTag = getTagById(tagId);
        findTag.changeTag(name);
    }

    /**
     * 태그 삭제
     */
    @Transactional
    public void deleteTag(final Long tagId) {
        final Tag findTag = getTagById(tagId);
        tagRepository.delete(findTag);
    }

    /**
     * 페이지 단위 태그 조회
     */
    public TagPageResponse getPageTags(final int page) {
        final PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.ASC, "id"));
        final Page<Tag> pageTags = tagRepository.findPageTags(pageRequest);
        return TagPageResponse.of(pageTags);
    }


    /**
     * 사용자 입력 + 페이지 단위 태그 조회
     */
    public TagSliceResponse getSliceTags(final int page, final String keyword) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.ASC, "id"));
        // 대소문자 구분 제거
        final Page<Tag> sliceTags = tagRepository.findSliceTags(pageRequest, keyword.toUpperCase(Locale.ROOT));
        return TagSliceResponse.of(sliceTags);
    }

    /**
     * 아이디로 태그 리스트 조회
     */
    public TagsResponse getTagsByIds(final Set<Long> ids) {
        final List<Tag> tags = tagRepository.findByIds(ids);
        return TagsResponse.of(tags);
    }

    private Tag getTagById(final Long id) {
        return tagRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundTagException("태그를 찾을 수 없습니다.");
        });
    }
}
