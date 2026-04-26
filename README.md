# LinkScope

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Redis](https://img.shields.io/badge/Redis-7-red)
![Build](https://img.shields.io/badge/build-Gradle-blueviolet)

`LinkScope` is a production-like REST API service for URL shortening and click analytics.
It allows creating short links, redirecting users, and collecting actionable traffic statistics.

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- PostgreSQL
- Redis
- Flyway
- Swagger / OpenAPI (`springdoc`)
- Gradle
- Lombok
- Bean Validation (`jakarta.validation`)
- JUnit 5, Mockito, Testcontainers
- Docker / Docker Compose

## Key Features

- Create short links with optional custom alias
- Resolve and redirect by short code (`302 Found`)
- Soft delete links (`active=false`)
- Update link settings (URL, expiration, active flag)
- Paginated links listing with `Pageable`
- Per-link click analytics:
  - total clicks
  - unique IP count
  - last click timestamp
  - last 10 click events
- Redis caching by key pattern `links:{shortCode}`
- Global exception handling with consistent JSON error model
- OpenAPI documentation via Swagger UI

## Architecture

Layered architecture with separation of concerns:

- `controller` - REST endpoints
- `service` - business logic
- `repository` - data access
- `entity` - persistence model
- `dto` - API contracts
- `mapper` - entity/DTO mapping
- `exception` - typed exceptions and global handler
- `config` - Redis/OpenAPI/properties configuration
- `util` - short code generator

## Project Structure

```text
com.example.linkscope
├── config
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── exception
├── mapper
├── repository
├── service
├── util
└── LinkScopeApplication.java
```

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/links` | Create short link |
| GET | `/api/v1/links` | List links with pagination |
| GET | `/api/v1/links/{shortCode}` | Get link details |
| PATCH | `/api/v1/links/{shortCode}` | Update link |
| DELETE | `/api/v1/links/{shortCode}` | Soft delete link |
| GET | `/api/v1/links/{shortCode}/stats` | Get analytics |
| GET | `/r/{shortCode}` | Redirect to original URL |
| GET | `/api/v1/health` | Health check |

## Example: Create Link

### Request

`POST /api/v1/links`

```json
{
  "originalUrl": "https://example.com/some/long/url",
  "customAlias": "my-link",
  "expiresAt": "2026-12-31T23:59:59"
}
```

### Response (`201 Created`)

```json
{
  "id": "42d35ddf-6a4b-4f7d-a9f9-a8328ab54f57",
  "originalUrl": "https://example.com/some/long/url",
  "shortCode": "my-link",
  "shortUrl": "http://localhost:8080/r/my-link",
  "createdAt": "2026-04-26T14:45:10.126",
  "expiresAt": "2026-12-31T23:59:59",
  "active": true,
  "clickCount": 0
}
```

## Error Response Format

```json
{
  "timestamp": "2026-04-26T14:49:32.435+03:00",
  "status": 404,
  "error": "Not Found",
  "message": "Link not found: abc123",
  "path": "/api/v1/links/abc123"
}
```

## Local Run

### 1. Run dependencies with Docker Compose

```bash
docker compose up -d postgres redis
```

### 2. Run application

```bash
./gradlew bootRun
```

## Full Run via Docker Compose

```bash
docker compose up --build
```

Application will be available at `http://localhost:8080`.

## Swagger / OpenAPI

- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Configuration

Environment-aware `application.yml` supports:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATA_REDIS_HOST`
- `SPRING_DATA_REDIS_PORT`
- `APP_BASE_URL`

`APP_BASE_URL` is used to build public short URLs:

`{APP_BASE_URL}/r/{shortCode}`

## Testing

Run tests:

```bash
./gradlew test
```

Included tests:

- `ShortCodeGeneratorTest`
- `LinkServiceTest`
- `RedirectServiceTest`
- `StatsServiceTest`
- `LinkRepositoryIntegrationTest` (Testcontainers + PostgreSQL)

## Future Improvements

- AuthN/AuthZ for link ownership and private analytics
- Rate limiting and anti-abuse protection
- Async click ingestion (Kafka/RabbitMQ)
- Geo/IP enrichment for analytics dashboard
- Scheduled archival for old click events
- Better observability: metrics + tracing + structured logs
