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

### H2 Console

The H2 database console is enabled for development and testing.

- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

## Seeded Users

The application seeds the following users on startup:

- **John Doe**
  - Email: john@example.com
  - Phone: 123456789
  - Subscribed Categories: SPORTS, FINANCE
  - Channels: EMAIL, SMS

- **Jane Smith**
  - Email: jane@example.com
  - Phone: 987654321
  - Subscribed Categories: MOVIES
  - Channels: PUSH

## Usage

- Go to http://localhost:8080
- Select a category and enter a message
- Click Send
- View the logs below

### Sample Output

After sending a notification, the web interface displays:

- A success message: "Message sent successfully"
- A table of notification logs showing:
  - Message ID
  - Category
  - User (human-readable name)
  - Recipient (email/phone depending on channel)
  - Channel
  - Timestamp (formatted as yyyy-MM-dd HH:mm:ss)
  - Status (SENT or FAILED)

Screenshots of the interface can be viewed by running the application and navigating to the URL.