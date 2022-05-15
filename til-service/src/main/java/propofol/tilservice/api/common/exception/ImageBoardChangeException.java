package propofol.tilservice.api.common.exception;

// 이미지의 게시글 정보 추가 시 Exception 추가
public class ImageBoardChangeException extends RuntimeException{
    public ImageBoardChangeException() {
        super();
    }

    public ImageBoardChangeException(String message) {
        super(message);
    }

    public ImageBoardChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageBoardChangeException(Throwable cause) {
        super(cause);
    }

    protected ImageBoardChangeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}