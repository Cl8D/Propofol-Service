package propofol.ptfservice.domain.exception;

import java.util.NoSuchElementException;

public class NotFoundPortfolioException extends NoSuchElementException {
    public NotFoundPortfolioException() {
    }

    public NotFoundPortfolioException(String s) {
        super(s);
    }
}
