# Board API (Spring Boot 3.1, JWT, JPA)

간단한 게시판/회원 인증 백엔드입니다. JWT 기반 로그인·리프레시 토큰, Redis를 이용한 리프레시 토큰 관리, JPA 기반 게시글 CRUD, Swagger(OpenAPI) UI를 제공합니다. Vue 등 프론트엔드에서 쉽게 붙일 수 있도록 CORS/Swagger/표준 응답  
포맷을 갖추고 있습니다.

## 기술 스택
- Java 17, Spring Boot 3.1.5
- Spring Web, Spring Data JPA, Spring Security, Spring Validation
- MySQL 8.x, Redis 7.x (리프레시 토큰 저장)
- JWT (io.jsonwebtoken 0.11.5)
- Swagger UI (springdoc-openapi 2.0.2)
- Gradle, Lombok

## 프로젝트 구조 (주요)
- `BoardApplication.java` : JPA Auditing, Method Security 활성화
- `config/` : Swagger, 공통 응답(`ApiResponse`, `ErrorResponse`, `PageResponse`), 예외(`CustomException`, `GlobalExceptionHandler`), Bean 설정(ObjectMapper 등), 커스텀 Validation
- `domain/board` : 게시글 Entity/Repository/Service/Controller, DTO
- `domain/member` : 회원 Entity/Repository/Service/Controller, DTO
- `security/` : `JwtProvider`, `LoginFilter`(/login), `JwtFilter`(Authorization 헤더), `CookieProvider`(HttpOnly Refresh), Redis 기반 `RefreshTokenService`, Security 설정
- `resources/application.yaml` : 외부 환경변수로 DB/Redis/JWT 키 주입

## 실행 준비
1) 필수 설치: Docker 또는 로컬 MySQL 8.x, Redis 7.x / JDK 17
2) 환경변수 설정 (.env 예시)
   ```bash                                                                                                                                                                                                                        
   DB_URL=jdbc:mysql://localhost:3307/board?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8                                                                                                                        
   DB_USER=user                                                                                                                                                                                                                   
   DB_PASSWORD=1234                                                                                                                                                                                                               
   REDIS_HOST=localhost                                                                                                                                                                                                           
   REDIS_PORT=6379                                                                                                                                                                                                                
   JWT_KEY=로컬개발용_32바이트_이상_랜덤문자열                                                                                                                                                                                    
   REFRESH_KEY=리프레시용_32바이트_이상_랜덤문자열                                                                                                                                                                                

3. (선택) Docker로 의존성 실행

   docker-compose up -d   # MySQL(3307), Redis(6379), Redis Insight(5540)
4. 애플리케이션 실행

   ./gradlew bootRun      # Windows는 .\gradlew.bat bootRun
5. 확인
    - Swagger UI: http://localhost:8080/swagger-ui/index.html
    - H2 콘솔은 사용하지 않음. MySQL로 접근.

## 데이터 모델

- member (Soft Delete: deleted_at)
    - id (PK), username(Unique), password(BCrypt), nickname, role(ROLE_USER/ADMIN), status(ACTIVE 등), created_at/updated_at/deleted_at
- board (Soft Delete)
    - id (PK), member_id(FK), title, content, created_at/updated_at/deleted_at

## 인증/보안 흐름

- 로그인: POST /login (form-urlencoded username, password)
    - 성공: Body에 ApiResponse{data.accessToken="Bearer ..."}
    - Refresh 토큰은 HttpOnly & Secure 쿠키(Refresh)로 내려가며 Redis에 refresh:{memberId} 키로 TTL 저장 (기본 15일).
- Access 토큰 만료 시: POST /api/auth/refresh (쿠키에 Refresh 포함)
    - 새 Access 토큰 + 새 Refresh 쿠키 발급, Redis 갱신.
- 로그아웃: POST /api/auth/logout (인증 필요) → Redis 키 삭제 + Refresh 쿠키 만료.
- JWT 검증: Authorization: Bearer {token} 헤더. 상태가 ACTIVE가 아니면 401.
- CORS: http://localhost:5173, http://localhost:5174 허용, Credentials 허용.

## API 요약

공통 응답 래퍼 ApiResponse{status, code, message, data} 사용. 에러는 ErrorResponse{status, code, message, errors}.

### 인증

- POST /api/auth/signup : 회원가입
    - Body(JSON): { "username": "user01", "password": "Aa!23456", "confirmPassword": "...", "nickname": "닉네임" }
    - 검증: username 5~15 영숫자, password 8~15(영문/숫자/특수문자 조합), nickname 1~15자.
- POST /login : 로그인 (form-urlencoded)
    - Body: username=user01&password=Aa!23456
    - Response: data.accessToken + Refresh 쿠키(HttpOnly/Secure).
- POST /api/auth/refresh : Refresh 토큰으로 Access 재발급 (쿠키 필요)
    - Response: 새 data.accessToken + 새 Refresh 쿠키.
- POST /api/auth/logout : 로그아웃 (인증 필요) → Refresh 삭제.
- GET /api/auth/cookie-test : 수신 쿠키 확인용(개발 디버그).

### 게시판

- POST /api/board : 게시글 생성 (인증 필요)
    - Body(JSON): { "title": "...", "content": "..." }
- GET /api/board : 게시글 페이지 목록
    - Query: page, size, sort(기본 createdAt, DESC)
    - Response: PageResponse{content:[{id,title,writer,createdAt,updatedAt}], page,size,totalElements,totalPages,hasNext,...}
- GET /api/board/{boardId} : 게시글 단건 조회
- PUT /api/board/{boardId} : 게시글 수정 (인증+작성자 본인)
    - Body(JSON): { "title": "...", "content": "..." }
- DELETE /api/board/{boardId} : 게시글 삭제 (인증+작성자 본인, Soft Delete)

## 오류 코드 예시

- 401 AUTH001 인증 필요 / AUTH003 토큰 만료 / AUTH004 아이디·비번 불일치
- 403 AUTH100 권한 없음(작성자 불일치 등)
- 404 NOT404 리소스 없음
- 409 CON409 중복 리소스 (username 중복 등)
- 400 REQ002 Validation 실패 시 필드별 errors 포함

## 개발 팁

- 비밀번호 암호화: Spring Security PasswordEncoder 자동 구성 사용.
- Soft Delete: @SQLDelete + @Where로 삭제 시 deleted_at 업데이트, 조회 시 필터링.
- 테스트: 기본 contextLoads만 존재. 필요 시 Controller/Service 단위 테스트 추가 추천.
- Swagger 보호: @PreAuthorize가 붙은 메서드는 UI에서 Authorize 후 테스트 가능.

## 주요 포인트

- JWT Access/Refresh 분리 및 Redis 기반 토큰 무효화 구현.
- HttpOnly+Secure Refresh 쿠키와 Header 기반 Access 토큰 병행으로 보안/편의성 균형.
- 게시글 소유자 검증, Soft Delete 적용으로 데이터 이력 보존.
- 공통 응답/에러 래퍼와 예외 처리 일원화로 일관된 API 계약 제공.
- CORS, Swagger 세팅으로 프론트엔드 연동 및 문서화 편의 제공.