package propofol.tilservice.domain.file.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import propofol.tilservice.domain.board.entity.Board;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Image {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="image_id")
    private Long id;

    // 업로드된 파일 이름
    private String uploadFileName;

    // 서버에 저장될 파일 이름
    private String storeFileName;

    // 타입
    private String contentType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public void changeBoard(Board board) {
        this.board = board;
    }

    @Builder(builderMethodName = "createImage")
    public Image(String uploadFileName, String storeFileName, String contentType) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.contentType = contentType;
    }



}
