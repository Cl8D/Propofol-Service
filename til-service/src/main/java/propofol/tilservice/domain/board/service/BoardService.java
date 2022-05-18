package propofol.tilservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.api.common.exception.NotMatchMemberException;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.repository.CommentRepository;
import propofol.tilservice.domain.board.repository.RecommendRepository;
import propofol.tilservice.domain.board.service.dto.BoardDto;
import propofol.tilservice.domain.exception.NotFoundBoardException;
import propofol.tilservice.domain.file.entity.Image;
import propofol.tilservice.domain.file.repository.ImageRepository;
import propofol.tilservice.api.service.ImageService;

import java.io.File;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final RecommendRepository recommendRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final CommentRepository commentRepository;

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
    // 여러 트랜잭션을 걸칠 때는 하나의 트랜잭션에 장애가 발생할 수 있어서, 웬만하면 transactional을 붙여주자!
    @Transactional
    public Board saveBoard(Board board) {
        // 게시글 저장
        return boardRepository.save(board);
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
    @Transactional // transactional을 붙여줘야 변경감지로 삭제 가능!
    public String deleteBoard(Long boardId, String memberId) {
        Board findBoard = getBoard(boardId);

        // 게시글 생성자가 아니라면 예외 발생
        if(!findBoard.getCreatedBy().equals(memberId))
            throw new NotMatchMemberException("글 작성자만 삭제할 수 있습니다.");

        // 게시판에 저장된 이미지 리스트 가져오기
        List<Image> findImages = imageService.getImagesByBoardId(boardId);

        // 서버에 저장된 이미지 삭제
        if(findImages != null) {
            findImages.forEach(image -> {
                // 저장된 이미지를 서버에서도 삭제하기 위해 파일 객체 만들어서 탐색해보기
                File savedFile = new File(
                        imageService.findBoardPath(imageService.getUploadDir()) + "/" +
                                image.getStoreFileName());

                // 저장된 파일을 발견했다면 삭제
                if(savedFile.exists())
                    savedFile.delete();
            });

            // DB에서도 삭제해주기
            imageService.deleteImages(boardId);
        }


        // 게시글 삭제 시 관련된 추천수 데이터도 함께 삭제해주기
        recommendRepository.deleteBulkRecommends(boardId);
        // 댓글도 함께 삭제
        commentRepository.deleteBulkComments(boardId);
        // 게시글 삭제
        boardRepository.delete(findBoard);

        return "ok";
    }

    /*******************/

    // 본인의 게시글만 가져오기 (혹은 다른 사용자 게시글 검색)
    public Page<Board> getPagesByMemberId(Integer pageNumber, String memberId) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, 10,
                Sort.by(Sort.Direction.DESC, "id"));
        Page<Board> result = boardRepository.findPagesByCreatedBy(pageRequest, memberId);
        return result;
    }

    /*******************/

    // 게시글 제목 조회
    public Page<Board> getPageByTitleKeyword(String keyword, Integer page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findPageByTitleKeyword(keyword, pageRequest);
    }



}
