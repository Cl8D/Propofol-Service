package propofol.tagservice.domain.exception;

import java.util.NoSuchElementException;

// 태그를 찾을 수 없을 때 생기는 예외
public class NotFoundTagException extends NoSuchElementException {
    public NotFoundTagException() {
        super();
    }

    public NotFoundTagException(String s) {
        super(s);
    }
}
