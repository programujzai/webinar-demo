# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot Kotlin application with a PostgreSQL database, consisting of:
- Backend: Spring Boot 3.5.5 with Kotlin 1.9.25, located in `/be` directory
- Frontend: Empty directory at `/fe` (not yet implemented)
- Database: PostgreSQL 15 running in Docker

## Essential Commands

### Backend Development

Build the project:
```bash
cd be && ./gradlew build
```

Run the application:
```bash
cd be && ./gradlew bootRun
```

Run tests:
```bash
cd be && ./gradlew test
```

Clean build:
```bash
cd be && ./gradlew clean build
```

### Database Management

Start PostgreSQL database:
```bash
docker-compose up -d
```

Stop database:
```bash
docker-compose down
```

Database connection details (local profile):
- Host: localhost:5433
- Database: demo_db
- Username: demo_user
- Password: demo_password

## Architecture

### Backend Structure
- **Package**: `ai.programujz.demo`
- **Main Application**: `be/src/main/kotlin/ai/programujz/demo/DemoApplication.kt`
- **Resources**: `be/src/main/resources/`
  - `application.yml` - Main configuration
  - `application-local.yml` - Local development configuration
  - `db/migration/` - Flyway database migrations (currently empty)

### Key Technologies
- Spring Boot with Spring Web, Spring Security, Spring Data JDBC
- Flyway for database migrations
- PostgreSQL database
- Gradle build system
- Java 21 toolchain

### Configuration
- Active profile: `local`
- Swagger UI available at: `/api-docs`
- Database schema validation: Flyway with Spring JPA validation