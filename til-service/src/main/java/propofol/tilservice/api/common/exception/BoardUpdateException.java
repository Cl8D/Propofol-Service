package propofol.tilservice.api.common.exception;

public class BoardUpdateException extends RuntimeException{
    public BoardUpdateException() {
    }

    public BoardUpdateException(String message) {
        super(message);
    }

    public BoardUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoardUpdateException(Throwable cause) {
        super(cause);
    }

    public BoardUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
