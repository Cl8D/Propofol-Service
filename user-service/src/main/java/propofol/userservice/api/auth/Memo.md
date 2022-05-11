프론트와 연결할 때 발생했던 문제점들
- CORS 관련 참고하기 좋은 블로그)  
https://evan-moon.github.io/2020/05/21/about-cors/

---

* **CORS = Cross-Origin Resource Sharing**   
= 교차 출처(다른 출처) 리소스 공유.


* 같은 출처란?  
URL의 구성 요소 중 Scheme, Host, Port가 동일한 경우.  
  - ex) http://localhost:3000/users?page=1#hi
  - http -> protocol(scheme)  
  - localhost:3000 -> host
  - /users -> path   
  - ?page -> query string
  - .#hi -> fragment.
  
  
참고로, 출처를 비교하는 로직은 서버단이 아닌 브라우저 단에서 구현되어 있기 때문에, 
CORS를 위반하는 요청이라도 서버는 정상 응답을 하고 브라우저가 해당 응답을 버리는 형태로 구성된다.

---

* CORS의 동작 과정  
  - 기본적으로 웹 클라이언트가 다른 출처의 리소스를 요청할 때는 HTTP 프로토콜을 사용하여 요청하는데, 이때 브라우저는 요청 헤더에 **Origin**이라는 필드에 요청을 보내는 출처를 함께 담아서 보낸다.  
  - 이후 서버가 요청에 대한 응답을 할 때 응답 헤더의 **Access-Control-Allow-Origin**이라는 값에 해당 리소스를 접근하는 것이 허용된 출처를 내려준다.
  - 그러면 응답을 받은 브라우저는 자신이 보낸 요청의 **Origin**과 서버가 보내준 응답의 **Access-Control-Allow-Origin**을 비교한 다음, 응답이 유효한지 판단한다.

---

* Preflight Request  
  - 브라우저는 요청을 한 번에 보내지 않고 예비 요청 + 본 요청으로 나누어서 서버로 전송한다.
  - 이때, 예비 요청을 preflight라고 하며, 여기서 HTTP 메소드 중에서 OPTIONS이 사용된다.
  - 브라우저는 서버에게 예비 요청을 보내고, 서버는 이에 대한 응답으로 자신이 어떤 것을 허용하고 금지하는지 응답 헤더에 담아서 보내준다.
  - 이후, 브라우저는 예비 요청과 서버가 전달한 응답의 허용 정책을 비교한 뒤 안전하다고 판단되면 같은 엔드포인트로 본 요청을 보내게 된다.
  - 최종적으로 서버가 이에 대해 응답을 하면 응답 데이터를 브라우저가 자바스크립트에 넘겨주는 것이다.

---

📌 0511) Api-gateway 관련 오류  
1) 프론트에서 응답 데이터의 필드명을 맞춰주지 않아서 오류 발생 - 해결
2) JSON 형태로 반환하지 않아서 400 Bad Request 발생 - 해결
3) CSRF + CORS 문제 발생  
   - 여기서 간과했던 점이 바로 Preflight!  
   - api-gateway는 WebFlux를 사용하기 때문에 WebSecurityConfig를 사용할 수 없어서 cors 관련 설정을 못했었음.
   - 그러다 globalcors.**add-to-simple-url-handler-mapping:true** 옵션을 알고 yml에 적용하였더니 성공하길래 된 건가...? 싶었지만 왜 이렇게 되는지 알 수 없어서 고민이었음... 😥
   - 그러다 options 관련 문제인 걸 알고 yml 파일에서 method마다 **OPTIONS**를 추가해주었다. 알고 보니 예비 요청을 계속 보내는데 메서드를 추가하지 않아줘서 
   (spring.cloud.gateway.routes.predicates.method) 계속해서 요청을 거절당해 오류가 났던 것... ^_^
   - add-to-simple-url-handler-mapping 옵션의 경우 *routes.predicate에서 처리되지 않는 요청*에 **동일한 CORS를 제공**하기 위해서 사용하는 것이었다!
