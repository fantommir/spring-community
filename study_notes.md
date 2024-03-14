학습하면서 얻은 지식이나 헷갈렸던 부분을 정리합니다.

---
<!-- TOC -->
  * [Spring Security](#spring-security)
    * [Session 방식으로 로그인 구현](#session-방식으로-로그인-구현)
    * [UserDetails, UserDetailsService](#userdetails-userdetailsservice)
      * [UserDetails](#userdetails)
      * [UserDetailsService](#userdetailsservice)
    * [CSRF(Cross Site Request Forgery)](#csrfcross-site-request-forgery)
      * [CSRF 토큰 검증 원리](#csrf-토큰-검증-원리)
      * [CSRF 토큰의 작동 순서](#csrf-토큰의-작동-순서)
    * [Authorization](#authorization)
    * [OAuth2](#oauth2)
  * [조회수 중복 방지](#조회수-중복-방지)
    * [쿠키/로컬 스토리지 사용](#쿠키로컬-스토리지-사용)
    * [서버 측 세션 활용](#서버-측-세션-활용)
    * [IP 주소와 사용자 에이전트 기반](#ip-주소와-사용자-에이전트-기반)
    * [토큰 기반 인증 활용](#토큰-기반-인증-활용)
  * [ES Module](#es-module)
  * [JPA 최적화 전략](#jpa-최적화-전략)
    * [컬렉션에 size() 사용을 피하자](#컬렉션에-size-사용을-피하자)
  * [CascadeType.ALL / orphanRemoval](#cascadetypeall--orphanremoval)
    * [CascadeType](#cascadetype)
    * [orphanRemoval](#orphanremoval)
    * [CascadeType.REMOVE와 orphanRemoval 차이](#cascadetyperemove와-orphanremoval-차이)
  * [TEST](#test)
    * [MOCKITO](#mockito)
      * [Service 단위 테스트](#service-단위-테스트)
      * [Controller 단위 테스트](#controller-단위-테스트)
    * [@SpringBootTest](#springboottest)
    * [@WebMvcTest](#webmvctest)
  * [Java](#java)
    * [Record](#record)
  * [JWT(Json Web Token)](#jwtjson-web-token)
  * [SpringBoot 예외 처리](#springboot-예외-처리)
  * [Swagger](#swagger)
  * [OOP, AOP](#oop-aop)
  * [Querydsl](#querydsl)
  * [Docker](#docker)
  * [서버 배포](#서버-배포)
  * [GitHub Actions](#github-actions)
<!-- TOC -->

---
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
#### UserDetails
    Spring Security에서 사용자의 정보를 담는 인터페이스. 
    이 인터페이스를 통해 사용자의 정보를 저장하고, Spring Security의 다른 컴포넌트에서 이 정보를 참조할 수 있습니다.

    getUsername(): 사용자의 이름을 반환
    getPassword(): 사용자의 비밀번호를 반환
    getAuthorities(): 사용자의 권한을 반환. 권한은 GrantedAuthority 인터페이스를 구현한 객체의 컬렉션으로 표현
    isEnabled(), isAccountNonLocked(), isAccountNonExpired(), isCredentialsNonExpired(): 사용자 계정의 활성화 상태, 잠금 상태, 만료 상태 등을 확인하는 메소드
<br>

#### UserDetailsService
    Spring Security에서 사용자의 정보를 가져오는 인터페이스. 

    loadUserByUsername(String username): 로그인 시도 시 호출. 주어진 사용자 이름에 해당하는 사용자 정보를 UserDetails 객체로 반환
<br>

### CSRF(Cross Site Request Forgery)
    웹 어플리케이션에서 발생하는 취약점 중 하나. 로그인 한 사용자가 자신의 의지와 무관하게 공격자가 의도한 행동을 하도록 만드는 공격 방법
    REST API 서버라면 stateless하기 때문에 굳이 CSRF 보호 기능을 사용할 필요가 없다.

    예시) 사용자가 은행 웹사이트에서 송금을 하려고 합니다. 공격자는 다음과 같은 URL을 만들어 사용자가 클릭하도록 유도할 수 있습니다.
    http://bank.com/fund?acct=224224&amount=50000
    이 URL은 사용자가 은행 웹사이트에 로그인한 상태에서 방문하면, 사용자의 이름으로 $50,000를 공격자의 계좌(#224224)로 송금하게 됩니다.
<br>

#### CSRF 토큰 검증 원리
    CSRF 토큰을 클라이언트와 공유하는 일반적인 방법은 HTML 형식의 숨겨진 매개변수로 포함
    CSRF 토큰은 서버에서 임의의 난수를 생성하고 사용자의 세션에 저장합니다.
    사용자의 모든 요청에 이 난수 값을 포함하여 전송하도록 합니다.
    서버에서는 요청을 받을 때마다 세션에 저장된 토큰 값과 요청 파라미터에 전달된 토큰 값이 일치하는지 검증합니다.
    이 방법을 통해 서버는 사용자의 요청이 실제로 사용자가 전송한 것인지 확인합니다.
<br>

#### CSRF 토큰의 작동 순서
    1. 사용자가 웹사이트에 접속하면, 서버는 CSRF 토큰을 생성합니다. 이 토큰은 보통 HTML form의 hidden 필드에 포함되어 사용자에게 전달됩니다.
    2. 동시에, 서버는 같은 토큰 값을 사용자의 세션에 저장합니다.
    3. 사용자가 form을 제출하거나 어떤 요청을 보낼 때, 요청에는 세션에 저장된 CSRF 토큰이 포함됩니다. 이 토큰은 보통 HTTP header나 body에 포함됩니다.
    4. 서버는 요청을 받으면, 요청에 포함된 CSRF 토큰과 세션에 저장된 CSRF 토큰을 비교합니다. 두 토큰이 일치하면 요청이 유효한 것으로 판단하고, 그렇지 않으면 요청을 거부합니다.
<br>

### Authorization
> 모든 요청을 인터셉트하여 Filter Chain을 통해 인증 및 권한 부여를 수행  
> * 전역 인증
>   * .requestMatcher("/users").hasRole("USER")  
> * 메소드 인증
>   * @EnableMethodSecurity를 사용하면 @PreAuthorize, @PostAuthorize 등을 사용하여 메소드 단위로 권한을 부여할 수 있음  
>   * @PreAuthorize("hasRole('USER')")  

<br>

### OAuth2
> OAuth2는 인증 및 권한 부여 프레임워크로, 사용자의 리소스에 대한 접근 권한을 제어하는 프로토콜


---
## 조회수 중복 방지
<br>

### 쿠키/로컬 스토리지 사용
    사용자가 특정 페이지를 방문할 때, 해당 페이지의 고유 식별자를 쿠키나 로컬 스토리지에 저장합니다. 다음에 같은 페이지를 방문할 때, 저장된 식별자를 확인하여 조회수를 증가시키지 않습니다.

    장점
     - 구현이 간단하고 쉽습니다.
     - 서버 측 부하가 적습니다.
    단점
     - 사용자가 쿠키나 로컬 스토리지를 삭제하거나, 다른 브라우저를 사용할 경우 중복 카운트가 발생할 수 있습니다.
     - 보안 면에서 취약할 수 있습니다.
<br>

### 서버 측 세션 활용
    사용자의 세션에 페이지 방문 정보를 저장하여, 동일 세션 내에서는 페이지 조회수를 중복으로 증가시키지 않습니다.

    장점
     - 쿠키보다 보안이 강화됩니다.
     - 사용자가 쿠키를 삭제해도 영향을 받지 않습니다.

    단점
     - 서버에 부하를 줄 수 있습니다.
     - 사용자가 여러 기기나 브라우저를 사용할 경우 여전히 중복 카운트가 발생할 수 있습니다.
<br>

### IP 주소와 사용자 에이전트 기반
    용자의 IP 주소와 사용자 에이전트(User-Agent) 정보를 조합하여 고유 식별자를 생성하고, 이를 기반으로 중복 조회를 판단합니다.

    장점
     - 다양한 브라우저와 기기에서의 중복 조회수 증가를 어느 정도 방지할 수 있습니다.

    단점
     - 동일 네트워크(예: 학교, 회사)를 사용하는 사용자들은 같은 IP를 공유할 수 있어 정확도가 떨어질 수 있습니다.
     - 사용자의 개인정보 보호 정책에 따라 IP 주소 사용이 제한될 수 있습니다.
<br>

### 토큰 기반 인증 활용
    사용자 인증 시 발급되는 토큰(예: JWT)을 활용하여, 인증된 사용자의 각 요청을 식별하고 중복 조회수를 방지합니다.

    장점
     - 보안성이 높고, 사용자별 데이터 추적이 용이합니다.

    단점
     - 인증이 필요 없는 공개적인 컨텐츠에는 적합하지 않을 수 있습니다.
     - 구현 복잡도가 상대적으로 높습니다.
<br>

---
## ES Module
https://zubetcha.tistory.com/entry/Javascript-ES-Module  
https://github.com/mdn/js-examples/tree/main/module-examples

    ES Module : ES6에 도입된 모듈 시스템.
    import, export를 사용해 분리된 자바스크립트 파일끼리 서로 접근할 수 있다.

    <script type="module"> 
    모듈은 defer되어 문서 파싱 후 실행
    모듈은 전역 스코프가 아니라 자신의 스코프를 가지므로, import한 기능에만 접근

    이 태그 안에 함수를 정의했다가 전역 스코프가 아니라 에러가 발생한 적 있음.
<br>

---
## JPA 최적화 전략
<br>

### 컬렉션에 size() 사용을 피하자
    JPA에서 엔티티의 컬렉션에 size()를 호출하면, 해당 컬렉션에 속한 모든 엔티티가 지연 로딩되어 성능이 떨어짐.
    따라서, 컬렉션의 크기를 알아내려면 count 쿼리를 사용하는 것이 좋음.

<br>

---
## CascadeType.ALL / orphanRemoval
<br>

### CascadeType
    ALL, PERSIST, MERGE, REMOVE, REFRESH, DETACH
    부모 엔티티에 발생하는 모든 영속성 생명주기 이벤트를 관련된 자식 엔티티에도 적용
<br>

### orphanRemoval
    부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제
<br>

### CascadeType.REMOVE와 orphanRemoval 차이
    - CascadeType.REMOVE는 부모 엔티티의 삭제와 함께 자식 엔티티의 삭제를 관리
    - orphanRemoval=true는 부모 엔티티에서 자식 엔티티의 참조가 제거될 때만 자식 엔티티를 삭제
<br>

---
## TEST
<br>

### MOCKITO
    @Mock, @InjectMocks 어노테이션을 사용하여 테스트 코드 작성
    @Mock: 테스트 대상이 되는 클래스의 의존성을 Mocking
    @InjectMocks: 테스트 대상이 되는 클래스를 Mocking
<br>

#### Service 단위 테스트
    Service 단위 테스트에서는 Repository를 Mocking하여 테스트를 진행
<br>

```java
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    void 회원가입() {
        // given
        Member member = new Member();
        member.setEmail("temp@gmail.com");
        member.setPassword("1234");

        // when
        memberService.join(member);

        // then 
        verify(memberRepository, times(1)).save(member);
    }
}
```
<br>

#### Controller 단위 테스트
    Controller 단위 테스트에서는 Service를 Mocking하여 테스트를 진행
<br>

```java
@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    @Test
    void 회원가입() {
        // given
        Member member = new Member();
        member.setEmail("");
        member.setPassword("");

        // when
        memberController.join(member);
        
        // then
        verify(memberService, times(1)).join(member);
    }
}
```
<br>

### @SpringBootTest
    애플리케이션의 모든 컴포넌트를 로드하여 실제 애플리케이션의 실행 환경과 가장 유사한 테스트 환경을 구성
    여러 레이어(컨트롤러, 서비스, 데이터 액세스 등)와의 상호작용을 포함하는 테스트나, 전체 애플리케이션의 흐름을 테스트하는 데 적합
    
    @MockMvc를 빈으로 등록하지 않기 때문에 @AutoConfigureMockMvc 사용

    장점
      - 모든 빈을 로드하기 때문에 테스트 코드 작성이 간편
      - 실제 애플리케이션 환경과 동일하게 데이터베이스와 같은 외부 시스템과의 통합을 테스트할 수 있습니다.
    단점
      - 모든 스프링 컨텍스트를 로드하기 때문에 테스트 실행 시간이 오래 걸림
<br>

### @WebMvcTest
    Spring MVC 컨트롤러의 테스트에 특화된 어노테이션
    웹 계층에 집중하여 컨트롤러를 테스트할 수 있으며, 실제 HTTP 요청과 응답을 Mock하여 테스트
    예를 들어, HTTP 요청 처리, 모델 및 뷰 반환, HTTP 응답 상태 코드 등의 컨트롤러 로직을 테스트하는 데 적합

    @ExtendWith, @MockBean을 사용해야 함

    장점
      - 웹 레이어에 집중하여 테스트하기 때문에 테스트 실행 시간이 짧음
      - MockMvc 인스턴스를 자동으로 제공하므로, HTTP 요청과 응답에 대한 세밀한 제어와 검증이 가능
    단점
    - 웹 레이어에 집중하기 때문에, 서비스 레이어나 데이터 액세스 레이어와 같은 다른 레이어의 문제를 감지하지 못할 수 있다.
<br>

---
## Java
<br>

### Record
> 불변 데이터 객체를 쉽게 생성할 수 있도록 하는 새로운 유형의 클래스  
> 생성자, getter, equals, hashCode, toString 메소드를 자동으로 생성  
> 일반적으로 DTO(Data Transfer Object)로 사용

```java
public record Point(int x, int y) { }
```

---
## JWT(Json Web Token)
> 주요 목적은 보안이 아니라 **신뢰성 보장**  
> 클라이언트와 서버 간 정보를 안전하게 전달하기 위한 토큰 기반의 인증 방식  
> 일반적으로 stateless 서버에서 사용  
> 
> 예시  
> > 우편함(공개 키)은 누구나 접근할 수 있지만, 열쇠(개인 키)는 우편함의 주인만 가지고 있음  
> > 누군가가 우편함에 편지(JWT)를 넣으면(암호화하면), 그 편지는 우편함의 열쇠를 가진 사람만 열어볼 수 있다(복호화할 수 있다).  
>
> Header, payload, Signatrue 세 부분으로 구성되며, 'header.payload.signature' 형태
> * Header: 토큰의 타입과 해싱 알고리즘 정보  
> * Payload: 토큰에 담을 정보. 일반적으로 사용자에 관한 정보나 권한 등  
> * Signature: 토큰의 유효성 검증을 위한 암호화된 문자열  

<br>

---
## SpringBoot 예외 처리

<br>

---
## Swagger

<br>

---
## OOP, AOP

<br>

---
## Querydsl

<br>

---
## Docker

<br>

---
## 서버 배포

<br>

---
## GitHub Actions

<br>