package propofol.tilservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propofol.tilservice.api.common.annotation.Jwt;
import propofol.tilservice.api.common.annotation.Token;
import propofol.tilservice.api.common.exception.BoardCreateException;
import propofol.tilservice.api.common.exception.BoardUpdateException;
import propofol.tilservice.api.common.exception.ImageCreateException;
import propofol.tilservice.api.common.properties.FileProperties;
import propofol.tilservice.api.controller.dto.ResponseDto;
import propofol.tilservice.api.controller.dto.board.BoardDetailResponseDto;
import propofol.tilservice.api.controller.dto.board.BoardPageResponseDto;
import propofol.tilservice.api.controller.dto.board.BoardResponseDto;
import propofol.tilservice.api.controller.dto.board.BoardUpdateRequestDto;
import propofol.tilservice.api.controller.dto.comment.CommentPageResponseDto;
import propofol.tilservice.api.controller.dto.comment.CommentRequestDto;
import propofol.tilservice.api.controller.dto.comment.CommentResponseDto;
import propofol.tilservice.api.service.StreakService;
import propofol.tilservice.api.service.UserService;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.Comment;
import propofol.tilservice.domain.board.service.BoardService;
import propofol.tilservice.api.service.CommentService;
import propofol.tilservice.domain.board.service.RecommendService;
import propofol.tilservice.domain.board.service.dto.BoardDto;
import propofol.tilservice.api.service.ImageService;
import propofol.tilservice.domain.file.entity.Image;

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
    private final UserService userService;

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
        BoardPageResponseDto boardPageResponseDto = getBoardPageResponseDto(page, null, null);

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 페이지 조회 성공!", boardPageResponseDto);
    }

    // 게시글 응답 정보 DTO 리턴해주는 함수 (여러 곳에서 쓰인다!) - 페이지 수 + 게시글 수 + 게시글 리스트 전달
    private BoardPageResponseDto getBoardPageResponseDto(Integer page, String memberId, String keyword) {
        // Dto 형태에 맞춰서 데이터 생성
        BoardPageResponseDto boardPageResponseDto = new BoardPageResponseDto();

        Page<Board> pageBoards = null;

        // 만약 member 정보, keyword 정보 없이 단순히 전체 글 정보를 다 가져오는 거라면
        if(memberId == null && keyword == null)
            // 페이지별로 게시글 목록 가져오기
            pageBoards = boardService.getPageBoards(page);

        // 키워드 검색을 진행한다면!
        else if (memberId == null && keyword != null)
            pageBoards = boardService.getPageByTitleKeyword(keyword, page);

        // 멤버 게시글 조회라면!
        else
            pageBoards = boardService.getPagesByMemberId(page, memberId);

        // 총 페이지 수
        boardPageResponseDto.setTotalCount(pageBoards.getTotalElements());
        // 총 게시글 수
        boardPageResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        // 해당 페이지에 담기는 게시글 목록
        pageBoards.forEach(board -> {
            // DTO에 일치하는 필드 몇 개만 우선 담아주고
            BoardResponseDto responseDto = modelMapper.map(board, BoardResponseDto.class);
            // 채워지지 않은 댓글 정보, 이미지 정보 등은 추가적으로 담아준다.
            responseDto.setCommentCount(commentService.getCommentCount(responseDto.getId()));
            Image findImage = imageService.getTopImage(board.getId());
            responseDto.setImageBytes(imageService.getTopImageBytes(findImage));
            responseDto.setImageType(imageService.getImageType(findImage));

            boardPageResponseDto.getBoards().add(responseDto);
        });
        return boardPageResponseDto;
    }

    /**************************/

    /** 게시판 글 쓰기 파일 포함 / 비포함으로 나눴으나 코드 합침 */
    /** 동작 흐름 변경 : 사용자가 작성하기 버튼을 눌렀을 때 게시글 정보가 한 번에 날라오도록!
     * 파일만 먼저 보내고 + 그 뒤로 게시글 정보도 한 번 더 보내주는 식으로 진행! */
    // 게시판 글 쓰기
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    /** DTO 사용 -> 파라미터 직접 사용 */
    public ResponseDto createBoard(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("open") Boolean open,
            // 파일의 경우 파일 이름을 리스트 형태로 전달받는 식으로 진행
            @RequestParam(value="fileName", required = false) List<String> fileNames,
            @Jwt String token) {

        // boardDto 생성해주기
        BoardDto boardDto = createBoardDto(title, content, open);

        try {
            Board board = boardService.createBoard(boardDto);
            Board savedBoard = boardService.saveBoard(board);

            // 만약 파일도 함께 온다면
            if(fileNames !=null) {
                // 해당 파일에 대한 게시글 정보 세팅
                imageService.changeImageBoardId(fileNames, savedBoard);
            }
            // 게시글 생성 시 스트릭 저장
            streakService.saveStreak(token);
        } catch (Exception e) {
            throw new BoardCreateException("게시글 생성 시 오류가 발생하였습니다.");
        }

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 생성 성공!", "ok");
    }

    private BoardDto createBoardDto(String title, String content, Boolean open) {
        BoardDto boardDto = new BoardDto();
        boardDto.setTitle(title);
        boardDto.setContent(content);
        boardDto.setOpen(open);
        return boardDto;
    }

    /************************/

    /** 기존 코드는 사용자가 파일 첨부를 누르기만 하면 서버에 저장이 되었었는데,
     * 만약 사용자가 게시글을 작성하지 않고 바로 웹사이트를 나가버리면 서버에 저장된 걸 삭제할 수 없는 문제 발생.
     * 이를 해결하기 위해서 그냥 작성하기를 완전히 눌러야 클라이언트가 1차적으로 파일들만 먼저 뽑아서 전송하도록 변경!*/
    // 클라이언트는 일차적으로 파일들만 뽑아서 먼저 전달해준다. 이때 이 함수 호출!
    @PostMapping("/image")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveImage(
            @RequestParam(value = "file", required = false) List<MultipartFile> files) {
        List<String> storeImageNames = null;
//        // 이때 서버에서는 path + 파일명 형태로 경로 지정
//        // 여기서 localhost:8000 부분은 클라이언트에서 SERVER_URL:8000 형태로 처리해줌
//        String path = "http://localhost:8000/til-service/api/v1/images";
//        String saveFileName = null;

        try {
//            saveFileName = imageService.saveImage(file);

            // 파일이 존재한다면 서버에 저장되는 파일 이름들을 리스트에 담아주기
            if(files != null)
                storeImageNames = imageService.getStoreImageNames(files);
        } catch (Exception e) {
            throw new ImageCreateException("이미지 저장 오류 발생!");
        }
//        path = path + "/" + saveFileName;

        // 리턴 시 dto의 data 부분에 path를 전달해주는데, 클라이언트는 해당 path를 받아서 imageController의 getImages를 호출하게 된다.
        // 왜냐면 path에 api/v1/images가 들어가있으니까... 클라이언트의 insertEmbed 함수에 의해 자동으로 해당 url로 호출이 된다.
        // 클라이언트에게 서버에 저장된 이미지 이름 리스트를 리턴해준당!
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "이미지 생성 성공!", storeImageNames);
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
    // 응답 정보) 제목 + 내용 + 닉네임 + 이미지(바이트) 리스트 + 이미지 타입 리스트 + 공개 여부 + 추천 수 + 댓글 수 + 글 작성일
    @GetMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getBoardInfo(@PathVariable Long boardId,
                                    @Jwt String token) {
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 조회 성공!", createBoardDetailResponse(token, boardService.getBoard(boardId)));
    }

    private BoardDetailResponseDto createBoardDetailResponse(String token, Board board) {
        BoardDetailResponseDto responseDto = modelMapper.map(board, BoardDetailResponseDto.class);
        // 나머지 정보 채워주기
        responseDto.setCommentCount(commentService.getCommentCount(board.getId()));
        // 여기서 feignClient 때문에 token을 활용하는 것. 또, 매개변수로 memberId 정보가 필요한데
        // memberId는 여기서 곧 게시글의 작성자이기 때문에 createdBy 정보 활용!
        responseDto.setNickname(userService.getUserNickname(token, board.getCreatedBy()));

        List<byte[]> images = responseDto.getImages();
        List<String> imageTypes = responseDto.getImageTypes();

        // 게시글에 존재하는 모든 이미지 리스트
        List<Image> findImages = imageService.getImagesByBoardId(board.getId());

        // 각 이미지 리스트를 바이트 변환 + 타입 추출해주기
        findImages.forEach(image -> {
            images.add(imageService.getImageBytes(image.getStoreFileName()));
            imageTypes.add(image.getContentType());
        });
        return responseDto;
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
    // 응답 결과로 총 페이지수, 게시글 수, 게시글 리스트가 담긴 BoardPageResponseDto를 넘겨주게 된다. (-> user-service로)
    @GetMapping("/myBoards")
    @ResponseStatus(HttpStatus.OK)
    public BoardPageResponseDto getPageBoardsByMemberId(@RequestParam Integer page,
                                                        @Token String memberId) {
        return getBoardPageResponseDto(page, memberId, null);
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
//    // 게시글 작성 시 파일을 함께 업로드한다면
//    // 업로드한 파일은 서버에 저장된다.
//    @PostMapping("/files")
//    // transactional을 여기서 건 이유
//    // imageService에서 board.addImage(image);로 변경감지가 발생하였으면, 해당 메서드가 종료하면서 flush 되면서
//    // board + image(cascade)의 변경 내용이 함께 db에 반영된다.
//    @Transactional
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseDto createBoardWithFiles(
//            // @RequestParam을 통해서 받는다. 여러 개의 파일이 들어갈 수 있기 때문에
//            @RequestParam("file") List<MultipartFile> files,
//            @RequestParam("title") String title,
//            @RequestParam("content") String content,
//            @RequestParam("open") Boolean open,
//            @Jwt String token
//    ) {
//
//        // 요청 내용을 바탕으로 dto 생성해주기
//        BoardDto boardDto = new BoardDto();
//        boardDto.setTitle(title);
//        boardDto.setContent(content);
//        boardDto.setOpen(open);
//
//        // 게시글 생성
//        Board board = boardService.createBoard(boardDto);
//
//        try {
//            // 게시글 저장
//            boardService.saveBoard(board);
//            // 스트릭 저장
//            streakService.saveStreak(token);
//            // 파일 처리, 업로드할 디렉토리 경로 함께 전달하기
//            imageService.saveBoardFile(fileProperties.getBoardDir(), files, board);
//        } catch (Exception e) {
//            throw new BoardCreateException("게시글 생성 시 오류가 발생하였습니다.");
//        }
//
//        return new ResponseDto<>(HttpStatus.OK.value(), "success",
//                "게시글 생성 성공!", "ok");
//    }

    /*********************/

    // 부모 댓글 (대댓글이 존재하지 않는 가장 첫 댓글에 대해)
    @PostMapping("/{boardId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto createParentComment(@PathVariable(value = "boardId") Long boardId,
                                      @Validated @RequestBody CommentRequestDto requestDto,
                                      @Token String memberId,
                                      @Jwt String token) {
        // DTO를 리턴하도록 수정 (안 하면 무한참조 걸림! - 엔티티를 직접 접근하는 일은 없어야 한다!)
        Comment comment = commentService.saveParentComment(requestDto, boardId, token, memberId);
        CommentResponseDto responseDto  = modelMapper.map(comment, CommentResponseDto.class);

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "댓글 생성 성공!", responseDto);
    }

    // 자식 댓글 (대댓글, 하나의 부모 댓글에 대해 여러 자식 댓글이 달린다.)
    // 에브리타임처럼 부모 댓글 밑에 하위로 여러 대댓글이 있는 (깊이 동일) 형태로 구현
    @PostMapping("/{boardId}/{parentId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto createChildComment(@PathVariable(value = "boardId") Long boardId,
                                     @PathVariable(value = "parentId") Long parentId,
                                     @Validated @RequestBody CommentRequestDto requestDto,
                                     @Token String memberId,
                                     @Jwt String token) {

        Comment comment = commentService.saveChildComment(requestDto, boardId, parentId, token, memberId);
        CommentResponseDto responseDto  = modelMapper.map(comment, CommentResponseDto.class);

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "대댓글 생성 성공!", responseDto);
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
                            comment.getContent(), comment.getGroupId(), comment.getCreatedDate()));
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
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 제목 조회 성공!", getBoardPageResponseDto(page, null, keyword));
    }



}
