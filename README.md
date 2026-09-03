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
- Spring Security (JWT)
- Spring Batch
- Spring Data JPA
- MyBatis
- MySQL
- WebClient
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
- WebClient 기반 외부 API 연동, 4xx/5xx 상태 코드별 예외 처리 및 응답 검증
- 공공데이터 갱신 주기를 고려한 Scheduler 적용 전략 검토

### 트러블슈팅

- 공공데이터가 문서상 최신월 기준으로 제공된다고 되어 있었지만, 실제 호출 시 최신월 데이터가 불완전하거나 누락되는 경우를 확인
- 이를 반영해 검증된 안정 데이터월을 사용하도록 조정하고, 자동 스케줄링 대신 데이터 상태를 확인한 뒤 수동으로 실행하는 방식으로 운영 전략을 변경

---

## Spring Boot REST API

커뮤니티 게시판(게시글/댓글/좋아요)과 마이페이지 REST API를 개발했습니다.

### 담당 업무 - 커뮤니티

- 게시글 CRUD API 구현 (첨부파일 포함)
- 댓글 CRUD API 구현 (첨부파일 포함, 대댓글 구조 지원)
- 게시글 / 댓글 좋아요 등록·취소 API 구현
- Controller-Service-Repository 계층 구조 구현
- Spring Data JPA를 활용한 데이터 처리
- Request / Response DTO 분리
- 게시글 및 댓글 수정 시 첨부파일 선택 삭제 기능 구현

### 담당 업무 - 마이페이지

- 내가 쓴 글 목록 조회 API 구현 (페이지네이션)
- 내가 좋아요한 글 목록 조회 API 구현 (페이지네이션)

### 인증

- JWT 기반 인증 연동, `@AuthenticationPrincipal`로 인증된 사용자를 식별해 게시글/댓글 작성자 본인 확인 로직 적용

### 설계 포인트

- 좋아요 기능을 `REFERENCE_TYPE` / `REFERENCE_ID` 조합의 공용 테이블(`TBL_LIKE`)로 설계해, 게시글·댓글 외의 다른 도메인에도 확장 가능하도록 구성
- 중복 좋아요는 DB unique 제약조건(`USER_ID`, `REFERENCE_TYPE`, `REFERENCE_ID`)으로 방지
- 댓글/대댓글 트리는 단일 쿼리 조회 후 애플리케이션 레벨에서 조립하는 방식으로 N+1 문제 회피

---

## Repository 안내

본 Repository는 팀 프로젝트에서 담당한
Backend 및 Batch 개발 영역을 포트폴리오 목적으로 정리한 Repository입니다.

실제 프로젝트 전체 코드는 팀 Repository에서 관리됩니다.
