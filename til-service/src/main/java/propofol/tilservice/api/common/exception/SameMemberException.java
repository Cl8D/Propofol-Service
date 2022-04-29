package propofol.tilservice.api.common.exception;

// 게시글 작성자가 추천을 누르려고 할 때 발생하는 예외
public class SameMemberException extends RuntimeException{
    public SameMemberException() {
    }

    public SameMemberException(String message) {
        super(message);
    }

    public SameMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public SameMemberException(Throwable cause) {
        super(cause);
    }

    public SameMemberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
