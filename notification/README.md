# Notification System

This is a Java Spring Boot application for sending notifications to users based on categories and channels. 

## Features

- Send notifications by category (SPORTS, FINANCE, MOVIES)
- Users subscribe to categories and choose notification channels (SMS, EMAIL, PUSH)
- Logs all notification attempts
- Web UI for sending messages and viewing logs

## Requirements

- Java 17
- Maven

## How to Run

1. Clone the repository
2. Run `mvn clean install`
3. Run `mvn spring-boot:run` or use the VS Code task "Run Application"

The application will start on http://localhost:8080

## Usage

- Go to http://localhost:8080
- Select a category and enter a message
- Click Send
- View the logs below

## Architecture

- **Models**: User, Message, NotificationLog, Category, Channel
- **Repositories**: JPA repositories for data access
- **Services**: NotificationService with Strategy pattern for channels
- **Controllers**: Web controller for UI
- **Tests**: Unit tests for services

## Database

Uses H2 in-memory database. Data is seeded on startup.

## Tests

Run `mvn test` to execute unit tests.
