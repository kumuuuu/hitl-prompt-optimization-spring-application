# Project Overview

Spring Boot backend service for a human-in-the-loop prompt optimization workflow. The API authenticates Supabase JWT users, stores conversations and messages in PostgreSQL, detects ambiguity in user input through an external ML service, and generates ambiguity-aware responses using Google Gemini.

# Problem Statement

Ambiguous user prompts can lead to inconsistent AI responses. A backend is needed to:

- Authenticate users securely.
- Persist conversation history.
- Detect ambiguity in user messages.
- Produce a single, safe response that works across multiple interpretations.

# Solution Summary

This project implements a REST API that:

- Uses Spring Security OAuth2 resource server with a local JWKS file for JWT validation.
- Persists users, conversations, and messages with Spring Data JPA and PostgreSQL.
- Calls an external ambiguity detection service and builds a prompt that suppresses clarification requests.
- Generates AI responses through the Gemini SDK and streams the output to clients.

# Technical Architecture

- **API Layer**: `UserController` and `MessageController` expose authenticated endpoints (`/api/me`, `/api/messages`, `/api/debug`).
- **Service Layer**:
  - `AmbiguityAnalysisService` calls the external ML API.
  - `LlmPromptBuilder` formats ambiguity-aware prompts.
  - `GeminiService` generates responses with fallback behavior if the API key is missing.
  - `MessageService` orchestrates persistence and response generation.
- **Persistence Layer**: JPA repositories for `User`, `Conversation`, and `Message` entities.
- **Security**: `SecurityConfig` and `JwtConfig` configure stateless JWT validation using `supabase-jwks.json`.
- **External Dependencies**: PostgreSQL database, external ambiguity detection API, Google Gemini API.

# Key Features

- Supabase JWT authentication with a local JWKS file.
- Conversation and message persistence with timestamps.
- Ambiguity detection via external ML service.
- Prompt builder that enforces “no clarifying questions” response rules.
- Gemini-based response generation with streaming output.
- CORS configuration for local frontend origins.

# Tech Stack

- Java 21
- Spring Boot 4.0.x
- Spring Web MVC
- Spring Security OAuth2 Resource Server
- Spring Data JPA (Hibernate)
- PostgreSQL
- Google Gemini SDK (`com.google.genai:google-genai`)
- Maven

# Setup and Run Instructions

1. **Prerequisites**
   - Java 21
   - PostgreSQL
2. **Configure the application**
   - Update `src/main/resources/application.yaml` with your database settings.
   - Set `gemini.api-key` in `application.yaml` or provide `GEMINI_API_KEY` via environment variables.
   - Update `src/main/resources/supabase-jwks.json` if your Supabase JWKS rotates.
3. **Run the service**
   ```bash
   ./mvnw spring-boot:run
   ```
4. **Run tests**
   ```bash
   ./mvnw test
   ```

# Challenges Faced

- Coordinating stateless JWT validation with a local JWKS file for Supabase-authenticated requests.
- Streaming AI responses while still persisting the finalized message data.
- Combining external ambiguity detection with prompt construction to enforce a single-answer response.

# Key Learnings

- Implementing Spring Security OAuth2 resource server configuration for JWT-based APIs.
- Designing prompt templates that safely handle ambiguous user requests.
- Orchestrating multiple external services (ML detection and LLM generation) within a single request flow.
- Using JPA entities to model user, conversation, and message relationships.
