# Coffee Machine Application

A Spring Boot application that simulates a vending machine with product management, coin handling, and state management.

## Features

- Product management (add, update, remove, buy)
- Coin handling (insert, return)
- Machine state management (balance, inventory, change)
- RESTful API endpoints
- PostgreSQL database integration
- Docker support

## Prerequisites

- Java 21
- Maven 3.8.x
- Docker and Docker Compose (for containerized deployment)
- PostgreSQL (if running without Docker)

## Database

The application uses PostgreSQL for data persistence. When running with Docker, the database is automatically configured. For local development, you can use the following default settings:

- Database: vending
- Username: postgres
- Password: postgres
- Port: 5432

## Getting Started

### Local Development

1. Clone the repository:
```bash
git clone https://github.com/hlnet1/coffee-machine.git
cd coffee-machine
```

2. Build the application:
```bash
mvn clean install
```
2. Run the database:
```bash
docker-compose up
```
3. Run the application:
```bash
mvn spring-boot:run
```
## Testing

Run the tests using Maven:
```bash
mvn test
```

### Docker Deployment

1. Build and start the containers:
```bash
docker-compose up --build
```


### Access the application

- Application: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html




