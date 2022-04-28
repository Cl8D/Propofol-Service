# propofol-service

- 캡스톤 프로젝트 개발 개인 백업용 레포지토리 😊
- Backend) spring, spring cloud (MSA Service)
  - 세부 기술) Eureka, FeignClient
  - 배포) Jenkins, Docker
- Frontend) React 
- README.md는 계속 수정 예정, 프로젝트 종료 후 각 서비스별로 별도의 문서로 분리할 예정😚
- 📁 Project Repo) https://github.com/1917Years

---

### ✔ Schedule (~ing)
- user-service (0423~)
    - Spring Security + JWT token 활용
    - 회원가입, 로그인, 회원 수정
    - Oauth2 카카오 로그인 
    - til-service와 통신하여 사용자의 글 정보 얻어오기
  

- api-gateway (0425~)
    - 사용자의 JWT token 검증 + 인증된 사용자만 service 가능하도록


- discovery-server (0425~)
    - 일종의 주소록 역할
    

- config-server (0427~)
  - 설정 정보 프로퍼티 통합 관리 (.yml)
  - 암호화 완료


- til-service (0427~)
  - 게시판 CRUD, 페이징
  - 게시글 수정, 삭제 시 권한 확인 추가 