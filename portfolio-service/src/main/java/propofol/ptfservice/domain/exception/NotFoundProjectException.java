package propofol.ptfservice.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundProjectException extends NoSuchElementException {
    public NotFoundProjectException() {
    }

    public NotFoundProjectException(String s) {
        super(s);
    }
}
