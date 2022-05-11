package propofol.tilservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propofol.tilservice.api.common.annotation.Jwt;
import propofol.tilservice.api.common.annotation.Token;
import propofol.tilservice.api.common.exception.BoardCreateException;
import propofol.tilservice.api.common.exception.BoardUpdateException;
import propofol.tilservice.api.common.properties.FileProperties;
import propofol.tilservice.api.controller.dto.ResponseDto;
import propofol.tilservice.api.controller.dto.board.BoardCreateRequestDto;
import propofol.tilservice.api.controller.dto.board.BoardPageResponseDto;
import propofol.tilservice.api.controller.dto.board.BoardResponseDto;
import propofol.tilservice.api.controller.dto.board.BoardUpdateRequestDto;
import propofol.tilservice.api.controller.dto.comment.CommentPageResponseDto;
import propofol.tilservice.api.controller.dto.comment.CommentRequestDto;
import propofol.tilservice.api.controller.dto.comment.CommentResponseDto;
import propofol.tilservice.api.service.StreakService;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.Comment;
import propofol.tilservice.domain.board.service.BoardService;
import propofol.tilservice.domain.board.service.CommentService;
import propofol.tilservice.domain.board.service.RecommendService;
import propofol.tilservice.domain.board.service.dto.BoardDto;
import propofol.tilservice.domain.file.service.ImageService;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

    private final BoardService boardService;
    private final ModelMapper modelMapper;
    private final FileProperties fileProperties;
    private final ImageService imageService;
    private final RecommendService recommendService;
    private final CommentService commentService;
    private final StreakService streakService;

    // 테스트 데이터 추가
//    private final InitBoardService initBoardService;
//
//    @PostConstruct
//    public void initData() {
//        initBoardService.init();
//    }

    /*************/

    // 페이지별로 게시글 가져오기 (쿼리 파라미터로 받아오기. ?page=1)
    // 응답 결과) 총 페이지 수 + 게시글 수 + 게시글 목록 전달하기
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getPageBoards(@RequestParam Integer page) {
        // 페이지별로 게시글 목록 가져오기
        Page<Board> pageBoards = boardService.getPageBoards(page);

        // Dto 형태에 맞춰서 데이터 생성
        BoardPageResponseDto boardPageResponseDto = new BoardPageResponseDto();

        // 총 페이지 수
        boardPageResponseDto.setTotalCount(pageBoards.getTotalElements());
        // 총 게시글 수
        boardPageResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        // 해당 페이지에 담기는 게시글 목록
        pageBoards.forEach(board -> {
            boardPageResponseDto.getBoards().add(
                    // page<Board> 데이터에 있는 board들을 boardResponsetDto에 맞춰서 매핑해주기
                    modelMapper.map(board, BoardResponseDto.class));
        });

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 페이지 조회 성공!", boardPageResponseDto);
    }

    /**************************/

    // 게시판 글 쓰기 (파일 없이 단순 글쓰기)
    // @NotEmpty 등을 적용하기 위해서 @Validated 붙여주기
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createBoard(@Validated @RequestBody BoardCreateRequestDto requestDto,
                              @Jwt String token) {
        // domain 단의 board entity로 접근할 수 있게 하기 위해서 BoardDto 형태로 변경
        BoardDto boardDto = modelMapper.map(requestDto, BoardDto.class);
        Board board = boardService.createBoard(boardDto);

        // 게시글 생성 시 스트릭 저장
        try {
            streakService.saveStreak(token);
            boardService.saveBoard(board);
        } catch (Exception e) {
            throw new BoardCreateException("게시글 생성 시 오류가 발생하였습니다.");
        }
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 생성 성공!", "ok");
    }

    /************************/

    // 게시판 글 수정
    // 게시글 번호와 수정 내용이 함께 넘어오게 된다.
    // 게시글 작성자만 글 수정을 할 수 있도록 @Token 정보를 활용한다.
    @PostMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto updateBoard(@PathVariable Long boardId,
                              @RequestBody BoardUpdateRequestDto requestDto,
                              @Token String memberId,
                              @Jwt String token) {
        // domain 단의 board entity로 접근할 수 있게 하기 위해서 BoardDto 형태로 변경
        BoardDto boardDto = modelMapper.map(requestDto, BoardDto.class);

        // 게시글 수정 시에도 스트릭 채워지도록!
        try {
            boardService.updateBoard(boardId, boardDto, memberId);
            streakService.saveStreak(token);
        } catch (Exception e) {
            throw new BoardUpdateException("게시글 수정 시 오류가 발생하였습니다.");
        }
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 수정 성공!", "ok");
    }

    /*******************/

    // 게시글 글 정보 가져오기 (게시글 상세 보기)
    // 응답 정보) 게시글의 고유한 UUID + 글 제목 + 내용 + 추천 수 + 공개 여부
    @GetMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getBoardInfo(@PathVariable Long boardId) {
        Board board = boardService.getBoard(boardId);
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 조회 성공!", createBoardResponse(boardId));
    }

    private BoardResponseDto createBoardResponse(Long boardId) {
        Board board = boardService.getBoard(boardId);

        // board 정보를 바탕으로 응답 dto인 BoardResponseDto 객체 생성
        return getBoardResponseDto(board);
    }

    private BoardResponseDto getBoardResponseDto(Board board) {
        BoardResponseDto boardResponseDto = new BoardResponseDto();
        boardResponseDto.setBoardUUID(board.getId());
        boardResponseDto.setTitle(board.getTitle());
        boardResponseDto.setContent(board.getContent());
        boardResponseDto.setOpen(board.getOpen());
        boardResponseDto.setRecommend(board.getRecommend());
        return boardResponseDto;
    }

    /************************/

    // 게시글 글 삭제 - 역시 게시글 작성자만 가능하다.
    @DeleteMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteBoard(@PathVariable Long boardId, @Token String memberId) {
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 삭제 성공!", boardService.deleteBoard(boardId, memberId));
    }


    /*************/

    // 본인의 게시글만 조회하기
    // user-service의 FeignClient를 통해 요청이 들어왔을 때 처리되는 메서드
    // 응답 결과로 총 페이지수, 게시글 수, 게시글 리스트가 담긴 BoardListResponseDto를 넘겨주게 된다. (-> user-service로)
    @GetMapping("/myBoards")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getPageBoardsByMemberId(@RequestParam Integer page, @Token String memberId) {
        BoardPageResponseDto boardPageResponseDto = new BoardPageResponseDto();
        Page<Board> pageBoards = boardService.getPagesByMemberId(page, memberId);
        boardPageResponseDto.setTotalCount(pageBoards.getTotalElements());
        boardPageResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        pageBoards.forEach(board -> {
            boardPageResponseDto.getBoards().add(modelMapper.map(board, BoardResponseDto.class));
        });
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 조회 성공!", boardPageResponseDto);
    }

    /*************/

    // 게시글 추천 기능
    // 게시글의 id를 받아서 해당 게시글의 추천수를 조절해주기
    // 한 번 클릭하면 추천 업, 한 번 더 클릭하면 추천 다운.
    @PostMapping("/recommend/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createRecommend(@PathVariable(value="boardId") Long boardId,
                                  @Token String memberId) {
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "추천 생성 성공!", recommendService.createRecommend(memberId, boardId));
    }

    /*********************/

    // 게시글 작성 시 파일을 함께 업로드한다면
    // 업로드한 파일은 서버에 저장된다.
    @PostMapping("/files")
    /** TODO 이 부분 맞게 이해한 것이 맞는지...? */
    // transactional을 여기서 건 이유
    // imageService에서 board.addImage(image);로 변경감지가 발생하였으면, 해당 메서드가 종료하면서 flush 되면서
    // board + image(cascade)의 변경 내용이 함께 db에 반영된다.
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createBoardWithFiles(
            // @RequestParam을 통해서 받는다. 여러 개의 파일이 들어갈 수 있기 때문에
            @RequestParam("file") List<MultipartFile> files,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("open") Boolean open,
            @Jwt String token
    ) {

        // 요청 내용을 바탕으로 dto 생성해주기
        BoardDto boardDto = new BoardDto();
        boardDto.setTitle(title);
        boardDto.setContent(content);
        boardDto.setOpen(open);

        // 게시글 생성
        Board board = boardService.createBoard(boardDto);

        try {
            // 게시글 저장
            boardService.saveBoard(board);
            // 스트릭 저장
            streakService.saveStreak(token);
            // 파일 처리, 업로드할 디렉토리 경로 함께 전달하기
            imageService.saveBoardFile(fileProperties.getBoardDir(), files, board);
        } catch (Exception e) {
            throw new BoardCreateException("게시글 생성 시 오류가 발생하였습니다.");
        }

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 생성 성공!", "ok");
    }

    /*********************/

    // 부모 댓글 (대댓글이 존재하지 않는 가장 첫 댓글에 대해)
    @PostMapping("/{boardId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto createParentComment(@PathVariable(value = "boardId") Long boardId,
                                      @Validated @RequestBody CommentRequestDto requestDto,
                                      @Jwt String token) {
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "댓글 생성 성공!", commentService.saveParentComment(requestDto, boardId, token));
    }

    // 자식 댓글 (대댓글, 하나의 부모 댓글에 대해 여러 자식 댓글이 달린다.)
    // 에브리타임처럼 부모 댓글 밑에 하위로 여러 대댓글이 있는 (깊이 동일) 형태로 구현
    @PostMapping("/{boardId}/{parentId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto createChildComment(@PathVariable(value = "boardId") Long boardId,
                                     @PathVariable(value = "parentId") Long parentId,
                                     @Validated @RequestBody CommentRequestDto requestDto,
                                     @Jwt String token) {
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "대댓글 생성 성공!", commentService.saveChildComment(requestDto, boardId, parentId, token));
    }

    /*********************/

    // 댓글 정보 제공
    // 응답 정보 -> 게시글Id, 총 댓글 페이징 수, 댓글 수, 댓글DTO 리스트
    //  댓글 DTO -> id, 닉네임, 내용, 그룹id
    @GetMapping("/{boardId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getComments(@PathVariable(value = "boardId") Long boardId,
                                              @RequestParam("page") Integer page) {
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "댓글 조회 성공!", getCommentPageResponseDto(boardId, page));
    }

    private CommentPageResponseDto getCommentPageResponseDto(Long boardId, Integer page) {
        CommentPageResponseDto responseDto = new CommentPageResponseDto();
        Page<Comment> comments = commentService.getComments(boardId, page);
        responseDto.setBoardId(boardId);

        responseDto.setTotalCommentPageCount(comments.getTotalPages());
        responseDto.setTotalCommentCount(comments.getTotalElements());

        comments.forEach(comment -> {
            responseDto.getComments().add(
                    new CommentResponseDto(comment.getId(), comment.getNickname(),
                            comment.getContent(), comment.getGroupId()));
        });
        return responseDto;
    }

    /*********************/

    // 게시글 제목 검색 기능
    @GetMapping("/search/title/{keyword}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto findBoardByTitle(
            @PathVariable(value = "keyword") String keyword,
            @RequestParam(value = "page") Integer page) {
        Page<Board> pageBoards = boardService.getPageByTitleKeyword(keyword, page);
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 제목 조회 성공!", getBoardPageResponseDto(pageBoards));
    }

    private BoardPageResponseDto getBoardPageResponseDto(Page<Board> pageBoards) {
        BoardPageResponseDto boardPageResponseDto = new BoardPageResponseDto();

        boardPageResponseDto.setTotalCount(pageBoards.getTotalElements());
        boardPageResponseDto.setTotalPageCount(pageBoards.getTotalPages());

        List<BoardResponseDto> boards = boardPageResponseDto.getBoards();

        pageBoards.forEach(board -> {
            boards.add(new BoardResponseDto(board.getId(), board.getTitle(),
                    board.getContent(), board.getRecommend(), board.getOpen()));
        });

        /** TODO 코드 수정 - 본 프로젝트 set으로 board 정보 담아줘야 함!*/
        boardPageResponseDto.setBoards(boards);

        return boardPageResponseDto;
    }


}
