# springboot-community
Community Project for Spring Boot Study
<br><br>

---

# 프로젝트를 진행하며 문제가 생겼던 부분들
<br>

## spring.io에서 Dependency Versions 확인
https://spring.io/ -> Projects -> Spring Boot -> Learn -> 사용 버전의 Reference Doc. -> Dependency Versions
<br> <br>

## H2 DB 버전 오류

    Spring 3.12 버전을 사용하려면 H2 2.1. 이상 버전을 사용해야 함
    BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource
![image](https://github.com/JeongKiSeong/springboot-community/assets/80134129/b2eeaf71-8bc4-459c-bb10-5378bffdfa82)
<br> <br>

## 예약어, 식별자 오류
*엔티티던 필드던 예약어, 식별자는 절대 안 됨!!!*   
[https://www.h2database.com/html/advanced.html?highlight=identifier&search=identifier#keywords](https://www.h2database.com/html/advanced.html?highlight=Keywords%2C%2C%2CReserved%2CWords&search=Keywords%20%2F%20Reserved%20Words#keywords)

    User는 Entity 이름으로 사용하면 안 됨. User -> Member로 변경
    Post와 Comment 엔티티에서 like 필드를 사용해서 동일 오류 발생
    
    SQL statement "\000d\000a drop table if exists [*]user cascade "; expected "identifier";]
<br> <br>

## @Transacional과 @Rollback(value = false)
*트랜잭션의 전파와 Rollback Flow에 대해 공부하기!!!*

    학습용 프로젝트라 Test 실행 후 H2 DB에서 값을 확인하려고 @Rollback(value = false) 어노테이션 사용
    Service에서 예외처리를 한 후, 테스트 케이스에서 assertThrows로 검증하려고 함
    Service의 트랜잭션 안에서 계획대로 Unchecked Exception(IllegalArgumentException)이 발생해서 롤백
    롤백하려니 Test의 트랜잭션이 전파돼서 @Rollback(value = false)도 전파
    
    org.springframework.transaction.UnexpectedRollbackException: Transaction silently rolled back because it has been marked as rollback-only
<br><br>

## Spring Security
<br>

### Session 방식으로 로그인 구현
    로그인을 통해 사용자를 확인한 후 회원 정보를 세션에 저장한 후 이와 연결되는 세션 ID를 발급.
    이후 클라이언트는 매 요청마다 header에 세션 ID가 담긴 쿠키를 넣어 통신하는 방식으로 인증.
    
    장점
    - 서버에서 사용자의 인증 상태를 관리하기 때문에 보안성이 높다.
    - 쿠키에는 세션 ID만 저장되므로 개인 정보가 노출될 위험이 적다.
    - 세션 ID를 통해 사용자의 정보를 쉽게 조회할 수 있다.
    
    단점
    - 서버의 메모리를 많이 차지하므로 부하가 커질 수 있다.
    - 세션 ID가 탈취되면 보안이 취약해질 수 있다.
    - 세션 ID가 만료되면 다시 로그인해야 한다. 
<br>

### UserDetails, UserDetailsService
    
<br>


### CSRF(Cross Site Request Forgery) 
<br>

### UserDetails
<br>

## 조회수 중복 방지(Cookie)
<br>

## Azure Blob Storage
    image가 포함된 게시글을 만들기 위해 Azure Blob Storage를 사용.
    Connection string은 계정 이름, 계정 키, 엔드포인트 세부 정보 포함 -> Storage Account에 연결하는 데 필요한 모든 정보.
    application.yml에 Connection string만 추가해도 자동으로 Configuration 해줌.
<br>

### 파일명 중복 방지
    UUID를 사용해서 파일명 중복을 방지


## CKEditor (WYSIWYG Editor)
<br>

### Custom Upload Adapter
    https://jjong-factory.tistory.com/85
    처음엔 CKFinder Upload Adapter나 Ssimple Upload Adapter를 사용하려고 했으나, 삽입된 이미지를 움직이는 등의 동작을 하면 403 (Forbidden) 에러 발생.
    CSRF 토큰을 헤더에도 추가해봤으나 여전히 안돼서 Custom Upload Adapter를 사용해야겠다고 판단.
    
    위 블로그를 참고하여 Custom Upload Adapter 구현 후 403 (Forbidden) 에러 해결.
    409 (Public access is not permitted on this storage account.) 에러가 발생하였으나 Azure Blob Storage와 Container의 액세스 레벨 조정 후 해결.
<br>

### <script type="module">
