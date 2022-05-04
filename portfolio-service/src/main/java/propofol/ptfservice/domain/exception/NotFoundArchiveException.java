package propofol.ptfservice.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundArchiveException extends NoSuchElementException {
    public NotFoundArchiveException() {
    }

    public NotFoundArchiveException(String s) {
        super(s);
    }
}
