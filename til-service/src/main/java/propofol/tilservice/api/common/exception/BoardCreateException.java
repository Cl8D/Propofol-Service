package propofol.tilservice.api.common.exception;

public class BoardCreateException extends RuntimeException{
    public BoardCreateException() {
    }

    public BoardCreateException(String message) {
        super(message);
    }

    public BoardCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoardCreateException(Throwable cause) {
        super(cause);
    }

    public BoardCreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
