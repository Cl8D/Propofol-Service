package propofol.userservice.domain.exception;

public class ExistFollowingException extends IllegalStateException{
    public ExistFollowingException() {
        super();
    }

    public ExistFollowingException(String s) {
        super(s);
    }

    public ExistFollowingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExistFollowingException(Throwable cause) {
        super(cause);
    }
}