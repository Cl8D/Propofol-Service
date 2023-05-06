package propofol.tagservice.common.exception;

import java.util.NoSuchElementException;

public class NotFoundTagException extends NoSuchElementException {
    public NotFoundTagException(String s) {
        super(s);
    }
}
