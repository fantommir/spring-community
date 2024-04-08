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
  * [프로젝트 소개 📖](#프로젝트-소개-)
  * [프로젝트 목적 🎯](#프로젝트-목적-)
  * [기술 스택 🛠](#기술-스택-)
  * [주요 기능 ✨](#주요-기능-)
  * [배운 점 및 성장 경험 📚](#배운-점-및-성장-경험-)
  * [스크린샷 📸](#스크린샷-)
<!-- TOC -->

---
## 프로젝트 소개 📖
이 프로젝트는 김영한님의 스프링부트 강의를 시작으로 백엔드 개발의 세계에 입문한 저의 첫 주요 작업입니다.  
본 프로젝트는 Spring Boot와 Spring Security를 기반으로 한 웹 애플리케이션으로, 사용자 인증 및 권한 관리, API 문서 자동화, 다양한 CRUD 기능과 API 개발을 포함합니다.

---
## 프로젝트 목적 🎯
- **기술 습득**: 최신 백엔드 기술 스택을 직접 적용하여 실제 프로젝트 개발 과정을 경험함으로써, 개발자로서의 기술적 역량을 강화합니다.
- **문제 해결**: 사용자의 관점에서 출발하여 실질적인 문제를 이해하고, 이를 해결하기 위해 적절한 기술을 적용하여 개선과 효율을 추구합니다.
- **전 과정 이해**: 프로젝트 기획부터 배포에 이르기까지의 전 과정을 직접 수행함으로써, 개발 프로세스에 대한 깊은 이해를 얻습니다.

---
## 기술 스택 🛠
- **Backend**: Spring, Spring Boot, Spring Security, JPA, QueryDsl
- **DevOps**: Docker, GitHub Actions, Azure VM(Ubuntu)
- **Database**: H2(개발), MySQL(배포)
- **Frontend**: Thymeleaf
- **ETC**: Azure Blob Storage

---
## 주요 기능 ✨
* **사용자 인증 및 권한 관리**: Spring Security를 이용하여 CSRF 보호, 사용자 로그인 및 세션 기반 자동 로그인(remember-me), 사용자 권한 관리 기능을 구현.
* **API 문서 자동화**: Swagger를 이용하여 API 문서를 자동으로 생성, 관리 및 업데이트를 용이하게 함.
* **CRUD 기능 및 API 개발**: 카테고리, 게시글, 댓글, 대댓글, 좋아요/싫어요 등의 기능을 포함한 RESTful API 개발.
* **DTO 활용 및 최적화**: DTO를 적극 활용하여 불필요한 정보 노출 방지 및 n+1 문제 해결.
* **예외 처리**: @RestControllerAdvice, @ControllerAdvice를 이용한 전역적인 예외 처리 구현.
* **이미지 업로드**: Azure Blob Storage를 이용한 이미지 파일 업로드 기능.
* **조회수 중복 방지**: 쿠키를 이용하여 동일 사용자의 중복 조회수 증가 방지.
* **동적 웹 페이지**: Thymeleaf를 사용하여 동적으로 웹 페이지를 생성.
* **테스트 코드**: Mockito, JUnit5, AssertJ를 활용한 단위 테스트 및 통합 테스트 작성.
* **CI/CD 및 배포**: Docker를 사용하여 종속성에서 자유로운 환경 구성, GitHub Actions와 Azure VM을 통한 CI/CD 파이프라인 구축 및 자동 배포.

---
## 배운 점 및 성장 경험 📚
- Docker와 GitHub Actions를 활용한 CI/CD 파이프라인 구축을 통해, 개발과 배포 과정의 자동화와 효율성을 경험했습니다.
- Linux 환경에서의 서버 운영 및 관리를 통해, 시스템 운영에 대한 이해도를 높였습니다.
- REST API 설계 및 Swagger를 통한 문서화 작업을 통해 API 개발의 표준화 및 유지 보수의 효율성을 향상시킴.
- 사용자의 관점에서 출발하여 기능을 구현하고, 문제를 해결하는 과정에서 개발자로서의 문제 해결 능력을 향상시켰습니다.
- 다양한 기술 스택을 적용하며, 새로운 기술 학습의 중요성과 적응력을 깨달음.

프로젝트를 통해 얻은 이러한 경험과 성과들은 앞으로의 개발자 생활에 있어 소중한 자산이 될 것입니다. 또한, 지속적인 학습과 성장을 통해 더 나은 개발자가 되기 위한 발판을 마련하였습니다.

---
## 스크린샷 📸
- 메인 페이지  ![image](https://github.com/JungKiSung1012/spring-community/assets/80134129/5ff50bff-2a6b-41b7-94f2-13efe79918ee)
- 로그인 페이지  ![image](https://github.com/JungKiSung1012/spring-community/assets/80134129/5c9013ab-5220-4be2-a286-24791523128a)
- 게시글 조회 페이지(이미지 있는 걸로 변경)  ![image](https://github.com/JungKiSung1012/spring-community/assets/80134129/2b5342b5-119e-4939-8852-8c433309269c)
- 댓글 및 대댓글 ![image](https://github.com/JungKiSung1012/spring-community/assets/80134129/a0338e0a-862b-4bb9-bc44-1221af3477ce)
- Swagger API 문서 ![image](https://github.com/JungKiSung1012/spring-community/assets/80134129/b4a4e232-1a21-4ebc-84db-7be1210629d5)
- GitHub Actions CI/CD
- 테스트 코드
---