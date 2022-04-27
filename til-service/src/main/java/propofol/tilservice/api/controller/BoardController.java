package propofol.tilservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.tilservice.api.InitBoardService;
import propofol.tilservice.api.controller.dto.BoardCreateRequestDto;
import propofol.tilservice.api.controller.dto.BoardListResponseDto;
import propofol.tilservice.api.controller.dto.BoardResponseDto;
import propofol.tilservice.api.controller.dto.BoardUpdateRequestDto;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.service.BoardService;
import propofol.tilservice.domain.board.service.dto.BoardDto;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

    private final BoardService boardService;
    private final ModelMapper modelMapper;

    // 테스트 데이터 추가
    private final InitBoardService initBoardService;

    @PostConstruct
    public void initData() {
        initBoardService.init();
    }

    /*************/

    // 페이지별로 게시글 가져오기 (쿼리 파라미터로 받아오기. ?page=1)
    // 응답 결과) 총 페이지 수 + 게시글 수 + 게시글 목록 전달하기
    @GetMapping
    public BoardListResponseDto getPageBoards(@RequestParam Integer page) {
        // 페이지별로 게시글 목록 가져오기
        Page<Board> pageBoards = boardService.getPageBoards(page);

        // Dto 형태에 맞춰서 데이터 생성
        BoardListResponseDto boardListResponseDto = new BoardListResponseDto();

        // 총 페이지 수
        boardListResponseDto.setTotalCount(pageBoards.getTotalElements());
        // 총 게시글 수
        boardListResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        // 해당 페이지에 담기는 게시글 목록
        pageBoards.forEach(board -> {
            boardListResponseDto.getBoards().add(
                    // page<Board> 데이터에 있는 board들을 boardResponsetDto에 맞춰서 매핑해주기
                    modelMapper.map(board, BoardResponseDto.class));
        });

        return boardListResponseDto;
    }

    /**************************/

    // 게시판 글 쓰기
    // @NotEmpty 등을 적용하기 위해서 @Validated 붙여주기
    @PostMapping
    public String createBoard(@Validated @RequestBody BoardCreateRequestDto requestDto) {
        // domain 단의 board entity로 접근할 수 있게 하기 위해서 BoardDto 형태로 변경
        BoardDto boardDto = modelMapper.map(requestDto, BoardDto.class);
        return boardService.saveBoard(boardDto);
    }

    /************************/

    // 게시판 글 수정
    // 게시글 번호와 수정 내용이 함께 넘어오게 된다.
    @PostMapping("/{boardId}")
    public String updateBoard(@PathVariable Long boardId, @Validated @RequestBody BoardUpdateRequestDto requestDto) {
        /**TODO 본 프로젝트에서도 boardDto 넘길 수 있도록 코드 수정해주기 */
        // domain 단의 board entity로 접근할 수 있게 하기 위해서 BoardDto 형태로 변경
        BoardDto boardDto = modelMapper.map(requestDto, BoardDto.class);

        return boardService.updateBoard(boardId, boardDto);
    }

    /*******************/

    // 게시글 글 정보 가져오기 (게시글 상세 보기)
    // 응답 정보) 게시글의 고유한 UUID + 글 제목 + 내용 + 추천 수 + 공개 여부
    @GetMapping("/{boardId}")
    public BoardResponseDto getBoardInfo(@PathVariable Long boardId) {
        return createBoardResponse(boardId);
    }

    private BoardResponseDto createBoardResponse(Long boardId) {
        Board board = boardService.getBoard(boardId);

        // board 정보를 바탕으로 응답 dto인 BoardResponseDto 객체 생성
        BoardResponseDto boardResponseDto = new BoardResponseDto();

        boardResponseDto.setBoardUUID(board.getId());
        boardResponseDto.setTitle(board.getTitle());
        boardResponseDto.setContent(board.getContent());
        boardResponseDto.setOpen(board.getOpen());
        boardResponseDto.setRecommend(board.getRecommend());
        return boardResponseDto;
    }

    /************************/

    // 게시글 글 삭제
    @DeleteMapping("/{boardId}")
    public String deleteBoard(@PathVariable Long boardId) {
        return boardService.deleteBoard(boardId);
    }


}
