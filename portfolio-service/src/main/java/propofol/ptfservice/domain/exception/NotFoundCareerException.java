package propofol.ptfservice.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundCareerException extends NoSuchElementException {
    public NotFoundCareerException() {
    }

    public NotFoundCareerException(String s) {
        super(s);
    }
}
