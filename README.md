# HITL Backend (Spring Boot)

Human-in-the-Loop backend service built with Spring Boot. It authenticates users via Supabase JWTs, stores users/conversations/messages in PostgreSQL, runs an external ambiguity detector, then generates an “ambiguity-aware” response using Google Gemini.

## Tech Stack

- Java 21
- Spring Boot 4.0.x
- Spring Web (MVC)
- Spring Security (OAuth2 Resource Server JWT)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Google Gemini SDK (`com.google.genai:google-genai`)

## Repo Layout

- [hitl-backend/pom.xml](pom.xml) — Maven module (the actual Spring Boot app)
- [hitl-backend/src/main/resources/application.yaml](src/main/resources/application.yaml) — app config (port, DB, logging, Gemini key)
- [supabase-test.html](../supabase-test.html) — simple browser test for Supabase OAuth login

> Note: There is a nested module folder: the runnable Spring Boot project lives in `hitl-backend/`.

## What This Service Does

### High-level flow (Send message)

1. A user calls `POST /api/messages` with a Supabase JWT bearer token.
2. The backend creates a conversation if `conversationId` is not provided.
3. The user message is persisted.
4. The message text is sent to an external ML service for ambiguity detection.
5. The backend builds a “silent ambiguity” prompt (no clarification questions, no listing ambiguities).
6. The prompt is sent to Gemini.
7. The AI response is persisted and returned.

## Authentication & Security

### JWT verification (Supabase)

The app is configured as a **Resource Server**.

- Requests to these endpoints require authentication:
  - `GET /api/debug`
  - `GET /api/me`
  - `POST /api/messages`

JWT verification is implemented via a local JWKS file:

- [hitl-backend/src/main/resources/supabase-jwks.json](src/main/resources/supabase-jwks.json)
- Configured in [hitl-backend/src/main/java/com/kumuditha/hitl/config/JwtConfig.java](src/main/java/com/kumuditha/hitl/config/JwtConfig.java)
- Algorithm: `ES256`

If your Supabase project rotates keys, update `supabase-jwks.json` accordingly (or change the code to use Supabase’s remote JWKS endpoint).

### CORS

Allowed origins are currently hard-coded in [hitl-backend/src/main/java/com/kumuditha/hitl/config/CorsConfig.java](src/main/java/com/kumuditha/hitl/config/CorsConfig.java):

- `http://localhost:3000`
- `http://127.0.0.1:3000`
- `http://localhost:5500`

## Data Model (JPA)

- `users` → [hitl-backend/src/main/java/com/kumuditha/hitl/entity/User.java](src/main/java/com/kumuditha/hitl/entity/User.java)
  - `supabaseUserId` is unique
- `conversations` → [hitl-backend/src/main/java/com/kumuditha/hitl/entity/Conversation.java](src/main/java/com/kumuditha/hitl/entity/Conversation.java)
  - belongs to a `User`
- `messages` → [hitl-backend/src/main/java/com/kumuditha/hitl/entity/Message.java](src/main/java/com/kumuditha/hitl/entity/Message.java)
  - belongs to a `Conversation`
  - `sender`: `USER | AI | SYSTEM`
  - stores `promptUsed` and `ambiguityResultJson` (fields exist; only `promptUsed` is set in current code)

Schema is managed via Hibernate with `ddl-auto: update` in `application.yaml`.

## External Dependencies

### Ambiguity detection service

The backend calls a hosted service:

- URL: `https://babyrider-hitl-prompt-optimization-api.hf.space/analyze/`
- Code: [hitl-backend/src/main/java/com/kumuditha/hitl/service/AmbiguityAnalysisService.java](src/main/java/com/kumuditha/hitl/service/AmbiguityAnalysisService.java)

Expected response maps to DTOs in:

- [hitl-backend/src/main/java/com/kumuditha/hitl/dto/ml](src/main/java/com/kumuditha/hitl/dto/ml)

### Gemini

Gemini integration:

- Service: [hitl-backend/src/main/java/com/kumuditha/hitl/service/GeminiService.java](src/main/java/com/kumuditha/hitl/service/GeminiService.java)
- Model name currently used: `gemini-3-flash-preview`

If `gemini.api-key` is not configured, GeminiService logs a warning and will not be able to generate real responses.

## Configuration

Primary config is in [hitl-backend/src/main/resources/application.yaml](src/main/resources/application.yaml).

### Server

- `server.port` (default: `8080`)

### Database

Defaults (local dev):

- `spring.datasource.url=jdbc:postgresql://localhost:5432/hitl_db`
- `spring.datasource.username=hitl_user`
- `spring.datasource.password=hitl_pass`

You can override these using environment variables (Spring Boot relaxed binding):

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

### Gemini API Key

Configure via one of:

- `gemini.api-key` in `application.yaml`
- environment variable `GEMINI_API_KEY`

Recommended for local dev (PowerShell):

```powershell
$env:GEMINI_API_KEY = "<your_key>"
```

## Run Locally (Windows)

### Prerequisites

- Java 21 installed and on PATH
- PostgreSQL running locally

### Create a database

Create a database/user matching `application.yaml` (or change the config).

Example (psql):

```sql
CREATE DATABASE hitl_db;
CREATE USER hitl_user WITH PASSWORD 'hitl_pass';
GRANT ALL PRIVILEGES ON DATABASE hitl_db TO hitl_user;
```

### Start the backend

From the module folder:

```powershell
cd .\hitl-backend
.\mvnw.cmd spring-boot:run
```

App runs on `http://localhost:8080`.

### Run tests

```powershell
cd .\hitl-backend
.\mvnw.cmd test
```

## API

Base URL: `http://localhost:8080`

All endpoints below require a Supabase JWT access token:

- Header: `Authorization: Bearer <access_token>`

### GET /api/debug

Returns the decoded JWT (useful for debugging).

Example:

```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/debug
```

### GET /api/me

Creates or fetches the current user from the database using JWT claims.

Response: [hitl-backend/src/main/java/com/kumuditha/hitl/dto/UserMeResponse.java](src/main/java/com/kumuditha/hitl/dto/UserMeResponse.java)

Example:

```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/me
```

### POST /api/messages

Sends a message. If `conversationId` is `null`/omitted, a new conversation is created.

Request body: [hitl-backend/src/main/java/com/kumuditha/hitl/dto/CreateMessageRequest.java](src/main/java/com/kumuditha/hitl/dto/CreateMessageRequest.java)

```json
{
  "conversationId": 123,
  "content": "My message here"
}
```

Example:

```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"conversationId":null,"content":"Explain the steps to set up a PostgreSQL user"}'
```

Response: currently returns the saved `Message` entity for the AI response.

## Supabase Auth Test Page

For quick manual testing of OAuth login and seeing the returned session/token, open:

- [supabase-test.html](../supabase-test.html)

This page uses `@supabase/supabase-js` in the browser. It’s intended as a local dev helper.

## Troubleshooting

- **401 Unauthorized**: Ensure you are sending `Authorization: Bearer <token>` and that the token was issued by your Supabase project (and matches the JWKS in `supabase-jwks.json`).
- **JWT fails to verify after key rotation**: Update [hitl-backend/src/main/resources/supabase-jwks.json](src/main/resources/supabase-jwks.json).
- **DB connection errors**: Check `SPRING_DATASOURCE_*` values and that PostgreSQL is reachable.
- **Gemini not responding**: Set `GEMINI_API_KEY` (or `gemini.api-key`) and confirm outbound internet access.

## Notes / Next Improvements (Optional)

- Return DTOs instead of JPA entities from controllers (avoids lazy-loading / serialization issues).
- Store the ambiguity response JSON in `Message.ambiguityResultJson` (field exists, not currently set).
- Make CORS and JWKS configuration environment-driven for easier deployment.
