package propofol.userservice.domain.member.entity;

// Member 권한 설정 - spring security의 hasRole 적용하기 위해서 prefix는 ROLE_ 형태로 해야함!
public enum Authority {
    // admin, user
    ROLE_ADMIN, ROLE_BASIC
}
