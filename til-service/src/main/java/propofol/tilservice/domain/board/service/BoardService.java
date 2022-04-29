package propofol.tilservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.api.common.exception.NotMatchMemberException;
import propofol.tilservice.api.common.exception.SameMemberException;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.Recommend;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.repository.RecommendRepository;
import propofol.tilservice.domain.board.service.dto.BoardDto;
import propofol.tilservice.domain.exception.NotFoundBoardException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final RecommendRepository recommendRepository;

    // 페이지 단위로 게시글 가져오기
    public Page<Board> getPageBoards(Integer pageNumber) {
        // pageRequest -> pageable의 구현체
        // https://catchdream.tistory.com/181
        // .of(페이지 번호-0부터 시작, 페이지당 데이터의 수, 정렬 방향)
        // 가장 나중에 올라온 글이 가장 위로 가야하기 때문에 내림차순 정렬
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, 10,
                Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findAll(pageRequest);
    }

    /*********************/

    // 게시판 글 쓰기
    public String saveBoard(Board board) {
        // 게시글 저장
        boardRepository.save(board);
        return "ok";
    }

    public Board createBoard(BoardDto boardDto) {
        Board board = Board.createBoard()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .recommend(0)
                .open(true)
                .build();
        return board;
    }

    /***************************/

    // 게시글 수정
    @Transactional // 업데이트는 jpa를 활용하지 않기 때문에 변경 감지 기능을 통한 db에 반영
    public String updateBoard(Long boardId, BoardDto boardDto, String memberId) {
        Board findBoard = getBoard(boardId);
        // 게시글에 저장된 createdBy (=작성자)와 넘어온 memberId가 동일하다면 수정 진행
        if(findBoard.getCreatedBy().equals(memberId))
            findBoard.updateBoard(boardDto.getTitle(), boardDto.getContent(), boardDto.getOpen());
        // 아니라면 예외 처리
        else
            throw new NotMatchMemberException("권한이 없습니다.");
        return "ok";
    }

    // 하나의 게시글 가져오기
    public Board getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("게시글을 찾을 수 없습니다.");
        });
        return board;
    }

    /*********************/

    // 게시글 삭제 - 게시글에 저장된 createdBy와 마찬가지로 비교
    public String deleteBoard(Long boardId, String memberId) {
        Board findBoard = getBoard(boardId);
        // 게시글 삭제 시 관련된 추천수 데이터도 함께 삭제해주기
        recommendRepository.bulkDeleteAll(boardId);

        if(findBoard.getCreatedBy().equals(memberId))
            // JPA가 기본으로 제공하는 문법에서는 @Transactional 어노테이션을 붙이기 때문에 따로 설정해줄 필요가 없다.
            boardRepository.delete(findBoard);
        else
            throw new NotMatchMemberException("권한이 없습니다.");
        return "ok";
    }

    /*******************/

    // 본인의 게시글만 가져오기
    public Page<Board> getPagesByMemberId(Integer pageNumber, String memberId) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, 10,
                Sort.by(Sort.Direction.DESC, "id"));
        Page<Board> result = boardRepository.findPagesByCreatedBy(pageRequest, memberId);
        return result;
    }

    /*******************/




}
