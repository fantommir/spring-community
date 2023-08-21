# springboot-community
Community Project for Spring Boot Study
<br><br>

---

### 프로젝트를 진행하며 문제가 생겼던 부분들
<br>

#### spring.io에서 Dependency Versions 확인
https://spring.io/ -> Projects -> Spring Boot -> Learn -> 사용 버전의 Reference Doc. -> Dependency Versions
<br> <br>

#### H2 DB 버전 오류

    Spring 3.12 버전을 사용하려면 H2 2.1. 이상 버전을 사용해야 함
    BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource
![image](https://github.com/JeongKiSeong/springboot-community/assets/80134129/b2eeaf71-8bc4-459c-bb10-5378bffdfa82)
<br> <br>

#### 예약어, 식별자 오류
*엔티티던 필드던 예약어, 식별자는 절대 안 됨!!!*   
[https://www.h2database.com/html/advanced.html?highlight=identifier&search=identifier#keywords](https://www.h2database.com/html/advanced.html?highlight=Keywords%2C%2C%2CReserved%2CWords&search=Keywords%20%2F%20Reserved%20Words#keywords)

    User는 Entity 이름으로 사용하면 안 됨. User -> Member로 변경
    SQL statement "\000d\000a drop table if exists [*]user cascade "; expected "identifier";]

    Post와 Comment 엔티티에서 like 필드를 사용해서 오류 발생. 
    오류문의 좌우스크롤을 움직이지 않고 앞 부분만 짧게 읽은 게 문제가 됨.
    친절하게 expected "identifier"가 있던 걸 3시간만에 발견.
