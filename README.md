## 전체적인 확인이 필요함!!!

# SpringCommunity 🌱
![js](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![js](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![js](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white)  
![js](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![js](https://img.shields.io/badge/Microsoft_Azure-0089D6?style=for-the-badge&logo=microsoft-azure&logoColor=white)
![js](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)  
![js](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
---
[프로젝트를 진행하며 공부한 내용들](study_notes.md)  
[프로젝트를 진행하며 발생한 오류와 해결](troubleshooting.md)

---

<!-- TOC -->
* [SpringCommunity 🌱](#springcommunity-)
  * [프로젝트 개요 🌱](#프로젝트-개요-)
  * [프로젝트 소개 📖](#프로젝트-소개-)
  * [프로젝트 목적 🎯](#프로젝트-목적-)
  * [기술 스택 🛠](#기술-스택-)
  * [ERD 📊](#erd-)
  * [API 📝](#api-)
  * [주요 기능 ✨](#주요-기능-)
  * [배운 점 및 성장 경험 📚](#배운-점-및-성장-경험-)
  * [스크린샷 📸](#스크린샷-)
<!-- TOC -->

---
## 프로젝트 개요 🌱
* **프로젝트 명**: SpringCommunity
* **개발 기간**: 2023.08.17 ~ 2024.03.31
* **개발 인원**: 1명
* **주요 기능**
  * 사용자 : CRUD, Spring Security 회원가입 및 로그인, 권한 관리
  * 게시판 : CRUD, 좋아요/싫어요, 조회수, 페이징
  * 댓글 : CRUD, 대댓글, 페이징
  * 카테고리 : CRUD
* **개발 환경**: Java 17, Spring Boot 3.2.0, Gradle
* **간단한 소개**: Spring Boot로 개발한 커뮤니티 사이트


---
## 프로젝트 소개 📖
이 프로젝트는 김영한님의 스프링부트 강의를 시작으로 백엔드 개발의 세계에 입문한 저의 첫 주요 작업입니다.  
본 프로젝트는 Spring Boot를 기반으로 한 웹 애플리케이션으로, 다양한 CRUD 기능과 API 개발, 사용자 인증 및 권한 관리를 포함합니다.

---
## 프로젝트 목적 🎯
- **기술 습득**: 최신 백엔드 기술 스택을 직접 적용하여 실제 프로젝트 개발 과정을 경험함으로써, 개발자로서의 기술적 역량을 강화합니다.
- **문제 해결**: 사용자의 관점에서 출발하여 실질적인 문제를 이해하고, 이를 해결하기 위해 적절한 기술을 적용하여 개선과 효율을 추구합니다.
- **전 과정 이해**: 프로젝트 기획부터 배포에 이르기까지의 전 과정을 직접 수행함으로써, 개발 프로세스에 대한 깊은 이해를 얻습니다.

---
## 기술 스택 🛠
- **Backend**: Java, Spring Boot, Spring Data JPA
- **Database**: H2(개발), MySQL(배포)
- **DevOps**: Docker, GitHub Actions
- **Server**: Azure VM - Linux(Ubuntu 22.04 LTS)
- **Test**: JUnit5, Mockito
- **ETC**: Azure Blob Storage

---
## ERD 📊
![image](https://github.com/JeongGiSeong/spring-community/assets/80134129/e4fda01c-c4aa-4087-8162-be43e914735a)

---
## API 📝
![image](https://github.com/JeongGiSeong/spring-community/assets/80134129/1c190ef3-fcc7-467d-b70d-aeef65994622)

---
## 주요 기능 ✨
* **CRUD 기능 및 API 개발**: 카테고리, 게시글, 댓글, 대댓글, 좋아요/싫어요 등의 기능을 포함한 RESTful API 개발.
* **사용자 인증 및 권한 관리**: Spring Security를 이용하여 CSRF 보호, 사용자 로그인, 사용자 권한 관리 기능을 구현.
* **API 문서 자동화**: Swagger를 이용하여 API 문서를 자동으로 생성, 관리 및 업데이트를 용이하게 함.
* **DTO 활용 및 최적화**: DTO를 적극 활용하여 불필요한 정보 노출 방지.
* **예외 처리**: @RestControllerAdvice, @ControllerAdvice를 이용한 전역적인 예외 처리 구현.
* **조회수 중복 방지**: 쿠키를 이용하여 동일 사용자의 중복 조회수 증가 방지.
* **동적 웹 페이지**: Thymeleaf를 사용하여 동적으로 웹 페이지를 생성.
* **테스트 코드**: Mockito, JUnit5, AssertJ를 활용한 단위 테스트 및 통합 테스트 작성.
* **CI/CD 및 배포**: Docker를 사용하여 종속성에서 자유로운 환경 구성, GitHub Actions와 Azure VM을 통한 CI/CD 파이프라인 구축 및 자동 배포.

---
## 배운 점 및 성장 경험 📚
이 프로젝트를 통해 얻은 경험은 개발자로서의 지식과 기술, 그리고 문제 해결 능력의 성장에 큰 영향을 미쳤습니다. 복잡한 개발 환경에서의 실제 작업을 통해, 이론과 실습의 균형을 잡는 중요성을 깨달았으며, 다음과 같은 구체적인 성장 경험을 얻었습니다:

- **CI/CD 파이프라인의 구축과 자동화의 힘**: Docker와 GitHub Actions를 활용하여 CI/CD 파이프라인을 구축함으로써, 소프트웨어 개발 과정에서의 자동화가 얼마나 중요한지를 실감했습니다.

- **Linux 시스템 관리의 중요성**: Azure VM(Ubuntu)를 사용하여 서버를 운영하고 관리하는 과정에서 Linux 시스템에 대한 이해도가 크게 향상되었습니다.

- **RESTful API의 설계 및 문서화**: RESTful API를 설계하고 Swagger를 통해 문서화하는 과정을 통해, API 개발의 표준화와 유지 보수의 중요성을 더 깊이 이해하게 되었습니다.

- **사용자 중심의 개발 접근 방식**: 프로젝트를 진행하며 사용자의 관점에서 출발하여 기능을 구현하고 문제를 해결하는 과정을 통해, 사용자 중심의 개발이 얼마나 중요한지를 실감했습니다.

- **새로운 기술의 학습과 적응**: Spring Boot, Spring Security, JPA, Docker, GitHub Actions, Azure VM 등 새로운 기술을 배우고 적응하는 과정에서 기술적 유연성과 학습 능력이 크게 향상되었습니다.

이러한 경험과 성장은 저를 더욱 성숙한 개발자로 발전시켰으며, 앞으로의 개발 생활에 있어 소중한 자산이 될 것입니다.  
지속적인 학습과 성장을 통해 더 나은 개발자로 발전하기 위한 확고한 기반을 마련했다고 자부합니다.

---
## 스크린샷 📸
- 메인 페이지  ![image](https://github.com/JungKiSung1012/spring-community/assets/80134129/5ff50bff-2a6b-41b7-94f2-13efe79918ee)
- 로그인 페이지  ![image](https://github.com/JungKiSung1012/spring-community/assets/80134129/5c9013ab-5220-4be2-a286-24791523128a)
- 게시글 조회 페이지  ![image](https://github.com/JungKiSung1012/spring-community/assets/80134129/cafbfd6e-1a00-4261-9fc3-454346a4da6e)
- 댓글/대댓글 ![image](https://github.com/JungKiSung1012/spring-community/assets/80134129/a0338e0a-862b-4bb9-bc44-1221af3477ce)
- Swagger API 문서 ![image](https://github.com/JungKiSung1012/spring-community/assets/80134129/b4a4e232-1a21-4ebc-84db-7be1210629d5)
- GitHub Actions CI/CD
- 테스트 코드
---
