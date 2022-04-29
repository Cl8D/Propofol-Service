package propofol.tilservice.domain.exception;

import java.util.NoSuchElementException;

// 파일을 찾을 수 없을 때
public class NotFoundFileException extends NoSuchElementException {
    public NotFoundFileException() {
        super();
    }

    public NotFoundFileException(String s) {
        super(s);
    }
}