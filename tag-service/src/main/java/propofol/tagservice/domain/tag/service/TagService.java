package propofol.tagservice.domain.tag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tagservice.domain.exception.NotFoundTagException;
import propofol.tagservice.domain.tag.entity.Tag;
import propofol.tagservice.domain.tag.repository.TagRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {
    private final TagRepository tagRepository;

    // 태그 저장
    public String saveTag(Tag tag) {
        tagRepository.save(tag);
        return "ok";

    }

    /***************/

    // 태그 수정
    @Transactional
    public String updateTag(String tagName, String name) {
        Tag tag = findTag(tagName);
        tag.changeTag(name);
        return "ok";
    }

    private Tag findTag(String tagName) {
        Tag findTag = tagRepository.findByName(tagName).orElseThrow(() -> {
            throw new NotFoundTagException("태그를 찾을 수 없습니다.");
        });
        return findTag;
    }

    /***************/

    // 태그 삭제
    public String deleteTag(String tagName) {
        Tag tag = findTag(tagName);
        tagRepository.delete(tag);
        return "ok";
    }


    /***************/

    // 페이지 단위로 태그 조회하기
    public Page<Tag> getPageTags(Integer page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.ASC, "id"));
        return tagRepository.findPageTags(pageRequest);
    }


    /***************/

    // 사용자 입력(keyPoint) + 페이지 단위로 태그 조회
    public Page<Tag> getSliceTags(Integer page, String keypoint) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.ASC, "id"));
        return tagRepository.findSliceTags(pageRequest, keypoint);
    }
}
