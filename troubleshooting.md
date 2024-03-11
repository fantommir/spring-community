개발 과정에서 발생한 문제와 해결 방법을 정리합니다.
이 문서는 프로젝트를 진행하면서 발생한 문제를 찾아보거나, 비슷한 문제가 발생했을 때 참고할 수 있는 자료로 활용됩니다.

---

## H2 DB 버전 오류

    Spring 3.12 버전을 사용하려면 H2 2.1. 이상 버전을 사용해야 함
    BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource
<img src="https://github.com/JeongKiSeong/springboot-community/assets/80134129/b2eeaf71-8bc4-459c-bb10-5378bffdfa82" width="500" alt=""/>
<br> 

### spring.io에서 Dependency Versions 확인
    https://spring.io/ -> Projects -> Spring Boot -> Learn -> 사용 버전의 Reference Doc. -> Dependency Versions
<br>

## 예약어, 식별자 오류
엔티티던 필드던 예약어, 식별자는 절대 안 됨!!!  
https://www.h2database.com/html/advanced.html?highlight=identifier&search=identifier#keywords

    User는 Entity 이름으로 사용하면 안 됨. User -> Member로 변경
    Post와 Comment 엔티티에서 like 필드를 사용해서 동일한 오류 발생
    
    SQL statement "\000d\000a drop table if exists [*]user cascade "; expected "identifier";]
<br>

## Checked Exception / Unchecked Exception
<img src="https://github.com/JungKiSung1012/springboot-community/assets/80134129/2c528196-32ed-4e94-a652-1ed37fd52cca" width="500" alt="">

    테스트 실행 후 DB에서 값을 확인하려고 @Rollback(value = false) 어노테이션 사용
    Service에서 IllegalArgumentException 예외처리를 한 후, 테스트 케이스에서 assertThrows로 검증하려고 함.
    즉, 내 예상대로라면 assertThorws에 의해 테스트는 성공하고, DB에 테스트 데이터가 남아 있어야 함.
    테스트 코드에서 @Rollback(value = false)가 설정되어 있음에도 불구하고, 서비스 코드에서 발생한 unchecked exception으로 인해 트랜잭션이 롤백되었고,
    이는 테스트 코드에서 예상하지 못한 상황이었기 때문에 UnexpectedRollbackException이 발생
    
    org.springframework.transaction.UnexpectedRollbackException: Transaction silently rolled back because it has been marked as rollback-only

<br>


