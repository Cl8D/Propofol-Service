package propofol.tilservice.domain.exception;

import java.util.NoSuchElementException;

// 댓글을 찾을 수 없을 때 발생하는 예외
public class NotFoundCommentException extends NoSuchElementException {
    public NotFoundCommentException() {
        super();
    }

    public NotFoundCommentException(String s) {
        super(s);
    }
}