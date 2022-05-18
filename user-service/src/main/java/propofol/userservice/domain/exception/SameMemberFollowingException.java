package propofol.userservice.domain.exception;

public class SameMemberFollowingException extends IllegalStateException{
    public SameMemberFollowingException() {
        super();
    }

    public SameMemberFollowingException(String s) {
        super(s);
    }

    public SameMemberFollowingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SameMemberFollowingException(Throwable cause) {
        super(cause);
    }
}
