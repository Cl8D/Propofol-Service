package propofol.ptfservice.api.common.exception;

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
