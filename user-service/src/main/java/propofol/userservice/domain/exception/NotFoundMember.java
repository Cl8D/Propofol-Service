package propofol.userservice.domain.exception;

import java.util.NoSuchElementException;

// 회원 조회 실패 시 처리되는 예외
public class NotFoundMember extends NoSuchElementException {
    public NotFoundMember() {
        super();
    }
    public NotFoundMember(String s) {
        super(s);
    }
}
