# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Project Domain is a todo list application.

- Backend is stored in the `/be` directory
- Frontend is stored in the `/fe` directory
- Database is PostgreSQL 15 running in Docker

## Backend specific:

#### Tech stack:

- Spring Boot with Kotlin
- Spring Web, Spring Security, Spring Data JDBC
- Flyway for database migrations

#### Commands:

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

## Frontend specific

#### Tech stack:

- Next.js with TypeScript
- Tailwind CSS for styling
- React 18

## Database Management

Start PostgreSQL database:

```bash
docker-compose up -d
```

Stop database:

```bash
docker-compose down
```

## Workflow

[] Phase 1
    [] Understand the user's request and determine if it relates to backend, frontend
    [] If the request is touching both backend and frontend, split the task into two separate tasks
    [] Focus first on first task that is conntected to one module, when we finish we can move into the next one
[] Phase 2
    [] Analyze the relevant codebase to understand the context
    [] Look out for CLAUDE.md for project overview, tech stack, commands and conventions
[] Phase 3
    [] Build the plan via architect agent
    [] Create a step-by-step plan to address prepared plan via the architect
[] Phase 4
    [] Execute the plan via the executor agent
[] Phase 5
    [] Review the changes via the reviewer agent
    [] Run the app to validate if everything is working as expected
