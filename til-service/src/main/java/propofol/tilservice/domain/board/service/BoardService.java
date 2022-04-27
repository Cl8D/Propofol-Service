package propofol.tilservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.service.dto.BoardDto;
import propofol.tilservice.domain.exception.NotFoundBoard;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;

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
    public String saveBoard(BoardDto boardDto) {
        // boardDto 형을 바탕으로 Board형의 객체 생성
        Board board = createBoard(boardDto);

        // 게시글 저장
        boardRepository.save(board);

        return "ok";
    }

    private Board createBoard(BoardDto boardDto) {
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
    @Transactional
    public String updateBoard(Long boardId, BoardDto boardDto) {
        Board findBoard = getBoard(boardId);
        findBoard.updateBoard(boardDto.getTitle(), boardDto.getContent(), boardDto.getOpen());
        return "ok";
    }

    // 하나의 게시글 가져오기
    public Board getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoard("게시글을 찾을 수 없습니다.");
        });
        return board;
    }

    /*********************/

    // 게시글 삭제
    public String deleteBoard(Long boardId) {
        Board findBoard = getBoard(boardId);
        boardRepository.delete(findBoard);
        return "ok";
    }


}
