개발 과정에서 발생한 문제와 해결 방법을 정리합니다.
이 문서는 프로젝트를 진행하면서 발생한 문제를 찾아보거나, 비슷한 문제가 발생했을 때 참고할 수 있는 자료로 활용됩니다.

---
<!-- TOC -->
  * [Dependency Versions 확인 방법](#dependency-versions-확인-방법)
    * [H2 DB 버전 오류](#h2-db-버전-오류)
  * [예약어, 식별자 오류](#예약어-식별자-오류)
  * [Checked Exception / Unchecked Exception](#checked-exception--unchecked-exception)
  * [테스트](#테스트)
    * [@WebMvcTest](#webmvctest)
      * [401 Unauthorized](#401-unauthorized)
      * [@EnableJpaAuditing](#enablejpaauditing)
  * [Swagger](#swagger)
  * [Spring Security](#spring-security)
    * [권한](#권한)
  * [SpringBoot 버전 관리](#springboot-버전-관리)
  * [서버 배포(Azure VM)](#서버-배포azure-vm)
    * [Port, 방화벽](#port-방화벽)
<!-- TOC -->

---
## Dependency Versions 확인 방법
> https://spring.io/ -> Projects -> Spring Boot -> Learn -> 사용 버전의 Reference Doc. -> Dependency Versions

<br>

### H2 DB 버전 오류
> Spring 3.12 버전에서 H2 DB를 사용하려면 H2 2.1. 이상 버전을 사용해야 함  
BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource
<img src="https://github.com/JeongKiSeong/springboot-community/assets/80134129/b2eeaf71-8bc4-459c-bb10-5378bffdfa82" width="500" alt=""/>

<br> 

---
## 예약어, 식별자 오류
    SQL statement "drop table if exists [*]user cascade "; expected "identifier";]
원인
> Table, Entity, Field 등의 이름으로 예약어, 식별자를 사용했기 때문에 발생한 오류

해결
> User는 Entity와 like 필드 이름을 다른 이름으로 변경

<br>

---
## Checked Exception / Unchecked Exception

    org.springframework.transaction.UnexpectedRollbackException: Transaction silently rolled back because it has been marked as rollback-only
원인  
> 테스트 실행 후 DB에서 값을 확인하려고 @Rollback(value = false) 어노테이션 사용  
Service에서 IllegalArgumentException 예외처리를 한 후, 테스트 케이스에서 assertThrows로 검증하려고 함.  
즉, 내 예상대로라면 assertThorws에 의해 테스트는 성공하고, DB에 테스트 데이터가 남아 있어야 함.  
하지만 테스트 코드에서 @Rollback(value = false)가 설정되어 있음에도 불구하고, 서비스 코드에서 발생한 unchecked exception으로 인해 트랜잭션이 롤백되었고,  
이는 테스트 코드에서 예상하지 못한 상황이었기 때문에 UnexpectedRollbackException이 발생한 것
<img src="https://github.com/JungKiSung1012/springboot-community/assets/80134129/2c528196-32ed-4e94-a652-1ed37fd52cca" width="500" alt="">

<br>

---
## 테스트
<br>

### @WebMvcTest
[@WebMvcTest](study_notes.md#webmvctest)

<br>

#### 401 Unauthorized
원인
> @WebMvcTest를 사용하면 내 Spring Security Configuration을 무시하고, 기본적인 Spring Security 설정을 사용하게 된다.  

```java
// 기본 SpringBootWebSecurityConfiguration.java
// 모든 요청에 대하여 아무 권한을 가지고 있다면 허용되도록 기본적으로 설정되어 있음
// 즉, 아무런 권한이 없다면 401 Unauthorized 에러가 발생
@Configuration(proxyBeanMethods = false)
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = Type.SERVLET)
class SpringBootWebSecurityConfiguration {

	@Bean
	@Order(SecurityProperties.BASIC_AUTH_ORDER)
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
		return http.build();
	}

}
```
<br>

해결
> 1. @WithMockUser, @WithAnonymousUser 등의 어노테이션 사용
> 2. @TestConfiguration을 사용하여 SecurityConfig를 재정의
> 3. ~~ExcludeFilters를 사용하여 SecurityConfig를 제외하고 테스트~~

<br>

#### @EnableJpaAuditing
```
Error creating bean with name 'jpaAuditingHandler': Cannot resolve reference to bean 'jpaMappingContext' while setting constructor argument
...
Caused by: java.lang.IllegalArgumentException: JPA metamodel must not be empty!
```    
원인  
> Application 클래스에서 사용하고 있던 @EnableJpaAuditing에 의해 jpaMappingContext Bean 생성 정보를 Bean Factory가 로드했기 때문

해결
```java
// 1. JpaMetamodelMappingContext를 MockBean으로 등록
@WebMvcTest(PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PostControllerTest { }

// 2. @EnableJpaAuditing 을 따로 분리
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig { }

// 3. @WebMvcTest 대신 @SpringBootTest 사용
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest { }
```
<br>

---
## Swagger

___
## Spring Security
<br>

### 권한
상황
> 권한 기능을 구현하는데 있어서, 분명히 권한을 부여했는데도 403 Forbidden 에러가 발생  
> @PreAuthorize("hasRole('ADMIN')")을 사용하여 권한을 확인하는데, "ADMIN" 권한을 가진 사용자가 403 에러를 받음  

원인
> Spring Security에서는 주로 역할(권한)을 설정할 때 "ROLE_" 접두사를 사용하는 관례가 있습니다.  
> 따라서 "ROLE_USER"나 "ROLE_ADMIN"과 같이 설정하면, Spring Security는 실제로는 "USER"와 "ADMIN"만을 인식  
> 예를 들어, hasRole("USER")와 같이 권한을 확인할 때 "ROLE_"은 생략하고 "USER"만을 사용.  

해결
```java
// Member.java
public class Member implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
```
<br>

---
## SpringBoot 버전 관리
상황
> 스프링부트 3.1.7 -> 3.2.0으로 업그레이드 후, 기존에 잘 동작하던 테스트에서 에러 발생  
> Resolved [org.springframework.http.converter.HttpMessageNotWritableException: Could not write JSON: (was java.lang.UnsupportedOperationException)]

원인 및 해결
> 3.2.0에선 PageImpl<> 생성자에 Pageable 인스턴스와 리스트의 크기를 추가로 전달해야 한다.
```java
// 3.1.7
Page<MemberDto> memberPage = new PageImpl<>(members);

// 3.2.0
Pageable pageable = PageRequest.of(0, 10);
Page<MemberDto> memberPage = new PageImpl<>(members, pageable, members.size());
```

<br>

---
## 서버 배포(Azure VM)

### Port, 방화벽
> HTTP(80), HTTPS(443)만 열고 SSH(22) 포트를 안 열어서 연결에 문제가 있었음  
> SSH(22) 포트를 여니까 cmd에서 SSH 접속 성공

<br>