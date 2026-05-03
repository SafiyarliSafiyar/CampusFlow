# CampusFlow

CampusFlow is a campus community platform for ADA University students. It combines a Spring Boot API with a React web app to support announcements, events, study groups, messaging, notifications, moderation, and student profile workflows.

## Features

- User registration, login, JWT authentication, email verification, and password reset with OTP codes
- Role-based access for students, moderators, and admins
- Campus feed with announcements, event updates, general posts, and moderation reports
- Event creation, search, updates, cancellation, and RSVP tracking
- Study group discovery, join requests, member management, and group chat
- Real-time group messaging through WebSocket and SSE support
- File uploads for profile photos and group messages
- Notification center and browser web push subscription support
- Seeded demo data for local development
- OpenAPI/Swagger documentation for backend endpoints

## Tech Stack

### Backend

- Java 17
- Spring Boot 3.5
- Spring Web, Spring Security, Spring Data JPA, Spring Data MongoDB
- PostgreSQL for relational data
- MongoDB for messages, OTPs, read receipts, and notifications
- Gradle

### Frontend

- React 19
- Create React App
- CSS modules through the app stylesheet
- Service worker/web push support

### Infrastructure

- Docker and Docker Compose
- Nginx for the production frontend container

## Project Structure

```text
.
+-- src/main/java/com/campusflow
|   +-- application       # Use cases, services, DTOs
|   +-- domain            # Domain models, ports, exceptions
|   +-- infrastructure    # Persistence, security, config, email, storage, websocket
|   +-- presentation      # REST controllers and request/response models
+-- src/main/resources    # Spring configuration
+-- frontend              # React application
+-- Dockerfile            # Backend container image
+-- docker-compose.yaml   # Full local stack
+-- build.gradle          # Backend build configuration
```

## Prerequisites

- Java 17
- Node.js and npm
- Docker Desktop, if running the full stack with Compose

## Run With Docker Compose

The easiest way to run everything is Docker Compose:

```bash
docker compose up --build
```

Services will be available at:

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs
- PostgreSQL: localhost:5433
- MongoDB: localhost:27017

To stop the stack:

```bash
docker compose down
```

To remove persisted database/upload volumes as well:

```bash
docker compose down -v
```

## Run Locally Without Docker

Start PostgreSQL and MongoDB first. The default backend configuration expects:

- PostgreSQL database: `campusflowdb`
- PostgreSQL user/password: `postgres` / `postgres`
- PostgreSQL port: `5433`
- MongoDB URI: `mongodb://mongo:mongo@localhost:27017/campusflowdb?authSource=admin`

Then start the backend:

```bash
./gradlew bootRun
```

On Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

Start the frontend in another terminal:

```bash
cd frontend
npm install
npm start
```

In development, the React app proxies API calls to `http://localhost:8080`.

## Demo Accounts

Demo data is created automatically when the backend starts.

| Role | Email | Password |
| --- | --- | --- |
| Admin | `admin.demo@ada.edu.az` | `CampusFlow123!` |
| Moderator | `moderator.demo@ada.edu.az` | `CampusFlow123!` |
| Student | `student.demo@ada.edu.az` | `CampusFlow123!` |

## Main API Areas

Most backend endpoints are under `/api/v1`.

- Users: `/api/v1/users`
- Feed posts: `/api/v1/feed`
- Events: `/api/v1/events`
- Study groups: `/api/v1/study-groups`
- Group messages: `/api/v1/study-groups/{groupId}/messages`
- Notifications: `/api/v1/notifications`
- Web push: `/api/v1/push`
- Moderation: `/api/v1/moderation`

Use Swagger UI at `http://localhost:8080/swagger-ui.html` for request and response details.

## Tests

Run backend tests:

```bash
./gradlew test
```

Run frontend tests:

```bash
cd frontend
npm test
```

## Configuration Notes

The checked-in `application.properties` is suitable for local development only. Before deploying, move secrets and passwords into environment variables or a secrets manager, especially:

- JWT secret
- SMTP username/password
- VAPID private key
- Database credentials

The Docker Compose backend service already overrides database connection settings through environment variables.

## Build

Build the backend JAR:

```bash
./gradlew bootJar
```

Build the frontend:

```bash
cd frontend
npm run build
```

Build production containers:

```bash
docker compose build
```
