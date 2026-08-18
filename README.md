# amwohaji-backend

> 여행 계획이 번거로운 사용자를 위한 여행 코스 추천 서비스

한국관광콘텐츠랩 공모전 출품을 위해 개발 중인 (26.10.28 최종 심사)
여행 코스 추천 서비스의 백엔드 개발을 담당했습니다.

공공데이터 API를 활용한 관광 데이터 수집·적재와
Spring Boot 기반 REST API 개발을 진행했습니다.

---

## 기술 스택

- Java
- Spring Boot
- Spring Batch
- Spring Data JPA
- MyBatis
- MySQL
- Git

---

## Spring Batch

공공데이터포털 관광 API를 활용하여
관광 데이터를 수집하고 DB에 적재하는 Batch를 설계 및 구현했습니다.

### 담당 업무

- 전국 관광지 정보 API 연동 및 데이터 적재
- 지자체 중심 관광지 정보 API 연동 및 데이터 적재
- 지역별 관광 자원수요 API 연동 및 데이터 적재
- Spring Batch Chunk 기반 Reader / Processor / Writer 구현
- MyBatis를 활용한 데이터 적재
- API Response / DTO / Entity / Mapper 구성
- 공공데이터 갱신 주기를 고려한 Scheduler 적용 전략 검토

---

## Spring Boot REST API

커뮤니티 게시판의 게시글 및 댓글 REST API를 개발했습니다.

### 담당 업무

- 게시글 CRUD API 구현
- 댓글 CRUD API 구현
- Controller-Service-Repository 계층 구조 구현
- Spring Data JPA를 활용한 데이터 처리
- Request / Response DTO 분리
- 게시글 및 댓글 수정 시 첨부파일 선택 삭제 기능 구현

---

## Repository 안내

본 Repository는 팀 프로젝트에서 담당한
Backend 및 Batch 개발 영역을 포트폴리오 목적으로 정리한 Repository입니다.

실제 프로젝트 전체 코드는 팀 Repository에서 관리됩니다.
