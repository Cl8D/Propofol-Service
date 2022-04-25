package propofol.userservice.domain.exception;

import java.util.NoSuchElementException;

// 회원 조회 시 실패했을 때 처리되는 예외
public class NotFoundMember extends NoSuchElementException {
    public NotFoundMember() {
        super();
    }
    public NotFoundMember(String s) {
        super(s);
    }
}
