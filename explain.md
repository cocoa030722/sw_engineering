# 프로젝트 설계 설명 (explain.md)

이 문서는 `sw_engineering` 프로젝트의 전체 구조와, React 프로덕션 빌드를
Spring Boot의 정적 리소스 폴더에 포함시킨 방식을 정리한다.

## 1. 전체 개요

이 프로젝트는 하나의 저장소 안에 두 개의 모듈이 공존한다.

- **백엔드**: Spring Boot 4 기반 REST API (`src/main/java/com/group/sw_engineering`)
- **프론트엔드**: React 19 + Vite 8 기반 SPA (`frontend/`)

개발 중에는 두 서버가 각각 별도 포트(백엔드 8080, 프론트엔드 5173)에서
동작하고, 프로덕션에서는 프론트엔드를 정적 파일로 빌드해 백엔드 하나가
API와 화면을 모두 서빙하는 **단일 배포 아티팩트** 구조를 지향한다.

```
sw_engineering/
├── build.gradle                     # 백엔드 빌드 설정
├── src/main/java/.../               # 백엔드 소스
├── src/main/resources/
│   ├── application.properties
│   └── static/                      # 프론트엔드 빌드 산출물 (오늘 추가)
└── frontend/                        # React 소스 (별도 npm 프로젝트)
```

## 2. 백엔드 구조 (계층형 아키텍처)

패키지는 책임에 따라 다음과 같이 계층화되어 있다.

```
controller  → service → repository → domain
                 ↑
               dto (요청/응답 경계)
exception   (계층 전체에서 공통으로 사용)
config      (스프링 설정)
```

| 계층 | 클래스 | 역할 |
|---|---|---|
| controller | `ItemController` | HTTP 요청/응답만 담당. `/api/items` 하위 라우팅 |
| service | `ItemService` | 비즈니스 로직. 컨트롤러는 repository를 직접 호출하지 않음 |
| repository | `ItemRepository` (인터페이스), `InMemoryItemRepository` (구현체) | 저장소 접근 계약과 구현 분리 |
| domain | `Item` | 순수 도메인 모델. JPA 애노테이션이 없어 저장 기술에 종속되지 않음 |
| dto | `ItemRequest`, `ItemResponse` | API 계약을 도메인 모델과 분리 (검증 애노테이션은 요청 DTO에만 존재) |
| exception | `ResourceNotFoundException`, `GlobalExceptionHandler`, `ApiError` | `@RestControllerAdvice`로 모든 컨트롤러의 예외를 동일한 JSON 형식으로 응답 |
| config | `WebConfig` | `/api/**`에 대한 CORS 허용 (dev 서버용) |

### 왜 이렇게 나눴는가

- **`ItemRepository` 인터페이스**: 지금은 `InMemoryItemRepository`(휘발성,
  `ConcurrentHashMap` 기반) 하나뿐이지만, 서비스 계층이 인터페이스에만
  의존하므로 나중에 `app.repository.type=jpa` 설정과 JPA 구현체 추가만으로
  실제 DB로 교체할 수 있다. `InMemoryItemRepository`는
  `@ConditionalOnProperty(name = "app.repository.type", havingValue = "memory", matchIfMissing = true)`
  로 게이트되어 있다.
- **DTO 분리**: `Item`(도메인)과 `ItemRequest`/`ItemResponse`(API 계약)를
  분리해 두어서, 도메인 모델이 바뀌어도 API 응답 형태를 독립적으로
  유지할 수 있다.
- **`GlobalExceptionHandler`**: 예외 발생 시 Spring 기본 에러 페이지
  (Whitelabel Error Page) 대신 일관된 JSON(`ApiError`)을 반환하도록 한다.
  단, 이는 컨트롤러 내부에서 발생한 예외에만 적용되며, 매핑되지 않은
  URL에 대한 404(정적 리소스 포함)는 이 핸들러를 거치지 않는다.

### API 엔드포인트

```
GET    /api/items       목록 조회
GET    /api/items/{id}  단건 조회
POST   /api/items       생성
PUT    /api/items/{id}  수정
DELETE /api/items/{id}  삭제
```

## 3. 프론트엔드 구조

```
frontend/src/
├── main.jsx              # BrowserRouter로 App을 감싸는 엔트리 포인트
├── App.jsx                # 최상위 컴포넌트
├── api/
│   ├── client.js          # axios 인스턴스, baseURL: "/api"
│   └── items.js           # /api/items 래퍼 (list/get/create/update/remove)
├── components/Layout.jsx  # 네비게이션 바 + <Outlet/> 레이아웃 라우트
└── pages/
    ├── HomePage.jsx
    ├── AboutPage.jsx
    ├── ItemsListPage.jsx
    ├── ItemDetailPage.jsx
    └── NotFoundPage.jsx
```

`api/client.js`의 `baseURL`이 상대 경로(`/api`)인 것이 핵심 설계 포인트다.
프론트엔드 코드는 백엔드의 호스트/포트를 전혀 알 필요가 없다.

- **개발 모드**(`npm run dev`, 5173번 포트): `vite.config.js`의
  `server.proxy` 설정이 `/api/*` 요청을 `http://localhost:8080`으로
  전달한다.
- **프로덕션 모드**: 아래 4번 항목처럼 백엔드가 프론트엔드 정적 파일을
  직접 서빙하므로 `/api/*`도 같은 오리진이 되어 별도 프록시나 CORS 설정이
  필요 없다.

`App.jsx`는 `react-router-dom`의 `<Routes>`로 `Layout`과 각 페이지를
아래와 같이 배선한다.

```
/            → Layout > HomePage       (index route)
/about       → Layout > AboutPage
/items       → Layout > ItemsListPage
/items/:id   → Layout > ItemDetailPage
/*           → Layout > NotFoundPage   (catch-all)
```

`Layout`의 네비게이션에 있는 `/items/new`는 아직 대응하는 페이지
컴포넌트가 없어 catch-all(`NotFoundPage`)로 떨어진다 — 생성/수정 폼은
아직 구현되지 않은 범위다.

## 4. 정적 리소스 통합 (오늘 진행한 작업)

Spring Boot는 클래스패스의 `/static`, `/public` 등에 있는 파일을
별도 설정 없이 웹 루트(`/`)에서 그대로 서빙한다. 이 특성을 이용해
React 빌드 산출물을 백엔드 리소스에 포함시켰다.

```
frontend$ npm run build          # vite build → frontend/dist/ 생성
$ cp -r frontend/dist/* src/main/resources/static/
```

결과로 `src/main/resources/static/`에 다음이 생성되었다.

```
static/
├── index.html
├── favicon.svg
├── icons.svg
└── assets/
    ├── index-*.js
    ├── index-*.css
    └── hero-*.png 등
```

이제 `./gradlew bootRun` 또는 빌드된 jar를 실행하면:

- `http://localhost:8080/` → `static/index.html` 서빙 (React 앱 진입)
- `http://localhost:8080/api/items` → `ItemController`가 처리하는 REST API

두 가지가 **같은 포트, 같은 오리진**에서 동시에 응답한다. 이전에 `/`에서
Whitelabel Error Page가 떴던 이유는 정확히 이 `static/`이 비어 있었기
때문이며, 지금은 해소되었다.

### 배포 시 주의할 점

- `static/`에 들어간 파일은 **빌드 산출물이므로 소스 코드가 아니다.**
  `frontend/src`를 수정한 뒤에는 반드시 `npm run build`를 다시 실행하고
  결과를 `static/`에 재복사해야 반영된다. (원하면 Gradle 빌드 태스크에
  `npm run build` + 복사를 연결해 자동화할 수 있다.)
- 라우터는 `BrowserRouter`(경로 기반)를 쓴다. `/items`처럼 하위 경로가
  있는 상태에서 브라우저로 그 경로에 **직접 진입하거나 새로고침**하면,
  Spring은 `/items`에 대응하는 정적 파일이나 `@RestController` 매핑을
  찾지 못해 원래는 다시 Whitelabel(404)이 뜬다. 이를 막기 위해
  `SpaForwardController`(`src/main/java/.../config/SpaForwardController.java`)
  를 추가했다. React Router가 실제로 사용하는 경로들(`/items`,
  `/items/**`, `/about`)만 명시적으로 `forward:/index.html`로 돌려보내
  React Router가 클라이언트 쪽에서 라우팅을 이어받게 한다. `/api/**`와
  정적 자산 경로는 이 컨트롤러보다 먼저 해석되므로 영향받지 않는다.
  새 라우트를 추가할 때는 이 컨트롤러의 `@GetMapping` 목록도 함께
  갱신해야 한다.

## 5. 개발 환경 vs 프로덕션 환경 요약

| 항목 | 개발 (`npm run dev`) | 프로덕션 (빌드 후 jar 실행) |
|---|---|---|
| 프론트엔드 서버 | Vite dev server (5173) | 없음 — 백엔드가 정적 파일로 서빙 |
| 백엔드 서버 | `./gradlew bootRun` (8080) | 동일 (8080) |
| API 호출 경로 | `/api/*` (Vite 프록시가 8080으로 전달) | `/api/*` (같은 오리진, 프록시 불필요) |
| CORS | `WebConfig`가 `http://localhost:5173` 허용 | 같은 오리진이라 불필요 |
| 저장소 | `InMemoryItemRepository` (JVM 재시작 시 초기화) | 동일 (JPA 전환 전까지) |

## 6. 향후 확장 지점

- **영속 저장소 전환**: `spring-boot-starter-data-jpa` + DB 드라이버 추가,
  `ItemRepository`의 JPA 구현체 작성, `app.repository.type=jpa` 설정만으로
  서비스/컨트롤러 계층 변경 없이 전환 가능하도록 설계되어 있다.
- **아이템 생성/수정 폼**: `/items/new`, `/items/:id/edit` 페이지 컴포넌트를
  만들고 `App.jsx`의 라우트 및 `SpaForwardController`의 매핑에 추가.
- **빌드 자동화**: Gradle에 프론트엔드 빌드를 연결하는 태스크(예:
  `com.github.node-gradle.node` 플러그인 또는 커스텀 `Exec` 태스크)를
  추가해 `./gradlew build` 한 번으로 `frontend/dist` → `static/` 복사까지
  자동화할 수 있다.
