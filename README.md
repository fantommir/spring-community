# springboot-community
Community Project for Spring Boot Study



### 프로젝트를 진행하며 문제가 생겼던 부분들


#### spring.io에서 Dependency Versions 확인
https://spring.io/ -> Projects -> Spring Boot -> Learn -> 사용 버전의 Reference Doc. -> Dependency Versions

#### H2 DB 버전 오류

    Spring 3.12 버전을 사용하려면 H2 2.1 이상의 버전을 사용해야 함
    BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource
![image](https://github.com/JeongKiSeong/springboot-community/assets/80134129/b2eeaf71-8bc4-459c-bb10-5378bffdfa82)


#### 예약어 오류
    User는 예약어라 Entity 이름으로 사용하면 안 됨. User -> Member로 변경
    SQL statement "\000d\000a drop table if exists [*]user cascade "; expected "identifier";]
