# Demo Backend Application

Spring Boot application with Kotlin and PostgreSQL.

## Prerequisites

- Java 21
- Docker (for PostgreSQL database)

## Database Setup

Start the PostgreSQL database using Docker Compose from the project root:

```bash
# From project root directory
docker-compose up -d
```

Database connection (local profile):
- Host: `localhost:5433`
- Database: `demo_db`
- Username: `demo_user`
- Password: `demo_password`

## Build

```bash
./gradlew build
```

## Run

Run with local profile:

```bash
./gradlew bootRun
```

The application will start on port 8080 with the `local` profile active by default.

## Test

```bash
./gradlew test
```

## API Documentation

Swagger UI is available at: `http://localhost:8080/api-docs`

## Clean Build

```bash
./gradlew clean build
```