package propofol.tilservice.domain.exception;

// 파일 저장 오류 시 발생하는 예외
public class NotSaveFileException extends RuntimeException{
    public NotSaveFileException() {
    }

    public NotSaveFileException(String message) {
        super(message);
    }

    public NotSaveFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSaveFileException(Throwable cause) {
        super(cause);
    }

    public NotSaveFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
