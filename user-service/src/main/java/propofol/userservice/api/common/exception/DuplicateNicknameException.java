package propofol.userservice.api.common.exception;

// 닉네임 중복 체크 예외
public class DuplicateNicknameException extends RuntimeException{
    public DuplicateNicknameException() {
        super();
    }

    public DuplicateNicknameException(String message) {
        super(message);
    }

    public DuplicateNicknameException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateNicknameException(Throwable cause) {
        super(cause);
    }

    protected DuplicateNicknameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}