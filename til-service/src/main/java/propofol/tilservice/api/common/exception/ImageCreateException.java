package propofol.tilservice.api.common.exception;

// 이미지 생성 예외 추가
public class ImageCreateException extends RuntimeException{
    public ImageCreateException() {
    }

    public ImageCreateException(String message) {
        super(message);
    }

    public ImageCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageCreateException(Throwable cause) {
        super(cause);
    }

    public ImageCreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
