# AEM-BLOG-BACKEND

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" />
</p>

REST API backend for **AEM Secrets**, a personal blog platform. It powers **posts**, **categories**, **comments**, **newsletter subscriptions**, **contact forms**, and authenticated **editorial** workflows.

This service is meant to be consumed by a separate frontend. Public readers hit read-only content APIs; editors authenticate with **JWT** to create and manage content.

---

## Why this repo exists 🎯

- Centralize blog domain logic (posts, comments, subscribers) behind a stable **JSON API**
- Keep secrets and **SMTP** credentials on the server (**JWT**, **reCAPTCHA** secret, mail)
- Separate public traffic (read + forms) from write traffic (authenticated editors)
- Stay portable across databases by preferring **JPA** / **JPQL** over vendor-specific SQL where practical

---

## Tech stack 🛠️

| Layer | Choice |
| --- | --- |
| Language | ![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white) |
| Framework | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot&logoColor=white) |
| Persistence | **Spring Data JPA** + ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white) |
| Security | **Spring Security** + **JWT** (OAuth2 resource server) |
| Mail | **Spring Mail** (`JavaMailSender`) |
| Docs | **springdoc-openapi** (Swagger UI) |
| Build | ![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white) |

---

## High-level architecture 🧭

```text
Client (frontend)
    |
    |  HTTP + optional Authorization: Bearer <jwt>
    |  public forms also send X-Recaptcha-Token
    v
Spring Security filter chain
    |-- RecaptchaFilter (selected public POSTs)
    |-- JWT resource server (protected routes)
    v
Controllers  ->  Services  ->  Repositories / Filesystem / SMTP
    |
    |  (subscriber welcome + contact mail)
    v
Application events  ->  @Async listener  ->  JavaMailSender
```

Typical request path:

1. **Security** decides if the route is public, read-protected, or write-protected
2. **Controllers** map `/api/...` endpoints and return `ResponseEntity`
3. **Services** hold business rules
4. **Repositories** talk to **PostgreSQL** via **JPA**
5. Cross-cutting concerns live in filters, `@ControllerAdvice`, and **async** listeners

---

## Project layout

Package-by-feature under `src/main/java/com/renatobonfim/aemblogbackend/`:

| Package | Responsibility |
| --- | --- |
| `post/` | Blog **posts**, **tags**, status (`DRAFT` / `PUBLISHED` / `UNPUBLISHED`), images |
| `category/` | **Categories** and post–category relationships |
| `comment/` | **Comments** on posts, moderation status |
| `subscription/` | Newsletter subscribe / unsubscribe + welcome email |
| `contact/` | Contact form API |
| `userx/` | Users and access levels (named `userx` to avoid clashes) |
| `security/` | **JWT** login, security filter chain, access matchers |
| `security/recaptcha/` | **reCAPTCHA v3** verification filter + `RestClient` call to Google |
| `notification/` | Async email events and listeners |
| `config/` | Shared constants, async/retry config |
| `customExceptions/` | Domain exceptions + global `ControllerAdvisor` |
| `dto/` | Shared response shapes (for example `ErrorResponseDTO`) |

Shared entry point:

- `AemBlogBackendApplication.java`

---

## How modules connect

### Content (posts / categories / comments)

```text
PostController
  -> PostServiceImpl
  -> PostRespository / CategoryRepository
  -> PostgreSQL (_post, _post_tags, _post_category, ...)

CommentController
  -> CommentServiceImpl
  -> CommentRepository + PostService
```

- Posts support filters: category, highlight, **tags** (case-insensitive), and `statusPost` (defaults to **`PUBLISHED`**)
- Tags are stored as an `@ElementCollection` in `_post_tags` (portable **JPQL** filtering)
- Post images are stored on disk under `app.config.images-post-root-path`

### Subscriptions

```text
POST /api/subscriber
  -> save Subscriber (token + statusChangeSource=SUBSCRIBE)
  -> publish WelcomeEmailRequestedEvent
  -> return 201 quickly

Async EmailNotificationListener
  -> SubscriberEmailService (SMTP, with retries)
```

Unsubscribe:

```text
Frontend link: {unsubscribe-url}?token=...
  -> PUT /api/subscriber/unsubscribe/{token}
  -> enableSubscription=false
  -> statusChangeSource=UNSUBSCRIBE_LINK
```

Editor updates via `PATCH /api/subscriber/{email}` set `statusChangeSource=EDITOR_PATCH`.

### Contact form

```text
POST /api/contact/send
  -> ContactService publishes ContactEmailRequestedEvent
  -> return 202 Accepted
  -> async listener delivers MIME email
```

### Auth

```text
POST /api/login  -> JWT
Protected write APIs require Authorization: Bearer <token>
Access levels: CAN_READ / CAN_WRITE
```

Route access is configured in `src/main/resources/application.properties` under `app.security.access.public|read|write`.

### Bot protection (reCAPTCHA v3)

Applied only to:

- `POST /api/subscriber`
- `POST /api/contact/send`
- `POST /api/comment`

Flow:

1. Filter reads header `X-Recaptcha-Token`
2. Server verifies with Google via `RestClient`
3. Requests with missing token or score below `app.config.recaptcha.min-score` (default `0.6`) are rejected

---

## API surface (main routes)

| Area | Examples |
| --- | --- |
| Auth | `POST /api/login` |
| Posts | `GET/POST /api/post`, `PUT/DELETE /api/post/{id}`, image upload/download |
| Categories | `GET/POST /api/category`, `PUT/DELETE /api/category/{id}` |
| Comments | `GET/POST /api/comment`, `PUT/DELETE /api/comment/{id}` |
| Subscribers | `POST /api/subscriber`, `PUT /api/subscriber/unsubscribe/{token}`, `PATCH /api/subscriber/{email}` |
| Contact | `POST /api/contact/send` |

Public vs protected routes are listed in `application.properties` (`app.security.access.*`).

Errors are returned as JSON:

```json
{
  "message": "...",
  "timestamp": "..."
}
```

Handled centrally by `ControllerAdvisor`.

---

## Configuration

Two config layers:

1. **Classpath** — `src/main/resources/application.properties`  
   App name + security access matchers (safe to commit).

2. **Local secrets** — project-root `config.properties`  
   Database, mail, **JWT** secret, CORS, **reCAPTCHA**, image paths, unsubscribe URL.

Import local config when starting:

```bash
--spring.config.import=file:./config.properties
```

Important local keys (examples, not real secrets):

```properties
server.port=8090
spring.datasource.url=jdbc:postgresql://localhost/aemsecrets
spring.mail.host=smtp.gmail.com
app.config.jwt.secret.key=...
app.config.cors.allowed-origins=http://localhost:4201
app.config.subscription.unsubscribe-url=http://localhost:4201/unsubscribe
app.config.recaptcha.secret-key=...
app.config.images-post-root-path=.\\WHERE-TO-STORE-IMAGES\\
```

> Never commit real passwords, JWT secrets, or reCAPTCHA secret keys.

---

## Running locally 🚀

### Prerequisites

- **JDK 17+**
- **Maven**
- **PostgreSQL** with a database matching your datasource URL
- Local `config.properties` at the project root

### Start

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.config.import=file:./config.properties"
```

Default API port (from local config): `http://localhost:8090`

### Tests

```bash
mvn test
```

---

## Key design decisions 📌

| Topic | Decision |
| --- | --- |
| Package style | Feature packages (`post`, `comment`, …), not layered-only |
| Tags | Join table via `@ElementCollection` for multi-DB **JPQL** |
| Post listing status | Defaults to **`PUBLISHED`**; optional multi-value `statusPost` |
| Email delivery | Async via application events + `@Async` so HTTP does not wait on **SMTP** |
| Unsubscribe | Token in DB + public `PUT` endpoint; source tracked (`UNSUBSCRIBE_LINK` vs `EDITOR_PATCH`) |
| Public forms | **reCAPTCHA v3** header check before controllers |
| Security rules | Declarative matchers in properties (`METHOD:/path`) |

---

## Useful entry files for new contributors

1. [`pom.xml`](pom.xml) — dependencies
2. [`src/main/resources/application.properties`](src/main/resources/application.properties) — public/read/write route matrix
3. [`security/SecurityConfig.java`](src/main/java/com/renatobonfim/aemblogbackend/security/SecurityConfig.java) — filter chain
4. One vertical slice: `post/PostController` → `PostServiceImpl` → `PostRespository` → `Post`
5. [`notification/EmailNotificationListener.java`](src/main/java/com/renatobonfim/aemblogbackend/notification/EmailNotificationListener.java) — async mail
6. [`customExceptions/ControllerAdvisor.java`](src/main/java/com/renatobonfim/aemblogbackend/customExceptions/ControllerAdvisor.java) — API error shape
