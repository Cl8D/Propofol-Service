package propofol.userservice.api.common.exception;

// JWT 토큰이 아직 만료되지 않았을 때 예외 처리
public class NotExpiredAccessTokenException extends RuntimeException{
    public NotExpiredAccessTokenException() {
        super();
    }

    public NotExpiredAccessTokenException(String message) {
        super(message);
    }

    public NotExpiredAccessTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExpiredAccessTokenException(Throwable cause) {
        super(cause);
    }

    protected NotExpiredAccessTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}