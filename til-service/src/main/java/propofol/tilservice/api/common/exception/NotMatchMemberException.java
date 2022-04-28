package propofol.tilservice.api.common.exception;

// 게시글 수정 및 삭제 처리 시 해당 게시글을 작성한 유저만 가능.
// 이때 해당 유저가 게시글 작성 유저가 아니라면 발생하는 예외 처리
public class NotMatchMemberException extends RuntimeException{
    public NotMatchMemberException() {
        super();
    }

    public NotMatchMemberException(String message) {
        super(message);
    }

    public NotMatchMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotMatchMemberException(Throwable cause) {
        super(cause);
    }

    protected NotMatchMemberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
