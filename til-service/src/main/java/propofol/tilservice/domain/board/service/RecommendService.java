package propofol.tilservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.api.common.exception.SameMemberException;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.Recommend;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.repository.RecommendRepository;
import propofol.tilservice.domain.exception.NotFoundBoardException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {

    private final BoardRepository boardRepository;
    private final RecommendRepository recommendRepository;

    // 게시글 추천 기능
    @Transactional
    public String createRecommend(String memberId, Long boardId) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("게시글을 찾을 수 없습니다.");
        });

        // 만약 게시글 작성자랑 추천인이 동일하다면 (자기추천) 막기
        if(findBoard.getCreatedBy().equals(memberId))
            throw new SameMemberException("글쓴이는 추천을 누를 수 없습니다.");

        // 게시글에 대한 추천 리스트 가져오기
        List<Recommend> recommends = findBoard.getRecommends();
        for (Recommend recommend : recommends) {
            // 동일 인물은 아니지만, 게시글의 추천 리스트에 이미 해당 멤버가 존재한다면
            if(recommend.getMemberId().equals(memberId)) {
                // 추천수를 내려준다. (2번 누르면 추천 취소니까)
                findBoard.setRecommendDown();
                // 추천 테이블에서도 해당 유저-게시글 정보를 지워주기
                recommendRepository.delete(recommend);
                return "cancel";
            }
        }

        // 만약 동일 인물도 아니고, 해당 게시글에 처음으로 추천을 누른다면 추천수 업.
        findBoard.setRecommendUp();
        Recommend recommend = Recommend.createRecommend().memberId(memberId).build();
        findBoard.addRecommend(recommend);
        recommendRepository.save(recommend);

        return "ok";
    }

    // 게시글이 삭제되면 해당 게시글에 대한 추천 역시 전부 삭제된다.
    @Transactional
    public void bulkDelete(Long boardId) {
        recommendRepository.bulkDeleteAll(boardId);
    }
}
