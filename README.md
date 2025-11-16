# Devices API

REST API for managing device resources with full CRUD operations, filtering, and comprehensive validation.

## Overview

The Devices API is a Spring Boot 3.x application that provides a REST interface for managing device resources. It includes features such as:

- Create, read, update (full and partial), and delete devices
- Filter devices by brand and state
- Pagination support for list endpoints
- Comprehensive domain validations
- Full API documentation via Swagger/OpenAPI
- Database persistence with PostgreSQL
- Docker containerization
- Extensive unit and integration tests

## Technology Stack

- **Framework**: Spring Boot 3.3.4
- **Language**: Java 21
- **Build Tool**: Maven 3.9+
- **Database**: PostgreSQL 15
- **Testing**: JUnit 5, Mockito, TestContainers
- **Documentation**: SpringDoc OpenAPI 3.0 (Swagger)
- **Containerization**: Docker & Docker Compose
- **ORM**: Spring Data JPA / Hibernate

## Project Structure

```
devices-api/
├── src/
│   ├── main/
│   │   ├── java/com/devices/
│   │   │   ├── controller/        # REST endpoints
│   │   │   ├── service/           # Business logic
│   │   │   ├── entity/            # JPA entities
│   │   │   ├── repository/        # Data access layer
│   │   │   ├── dto/               # Data transfer objects
│   │   │   ├── exception/         # Custom exceptions
│   │   │   ├── config/            # Configuration classes
│   │   │   ├── util/              # Utility classes
│   │   │   └── DevicesApiApplication.java  # Main class
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-test.properties
│   └── test/
│       ├── java/com/devices/
│       │   ├── service/           # Service unit tests
│       │   ├── controller/        # Controller unit & integration tests
│       │   └── AbstractIntegrationTest.java
│       └── resources/
│           └── application-test.properties
├── Dockerfile                     # Application container image
├── Dockerfile.postgres            # PostgreSQL container image
├── docker-compose.yml             # Multi-container orchestration
├── .dockerignore                  # Docker build exclusions
├── .gitignore                     # Git exclusions
└── pom.xml                        # Maven configuration
```

## Device Domain Model

### Entity: Device

```java
{
  "id": Long,                    // Unique identifier (auto-generated)
  "name": String,                // Device name (required, not blank)
  "brand": String,               // Device brand (required, not blank)
  "state": String,               // State: AVAILABLE, IN_USE, INACTIVE
  "creationTime": LocalDateTime   // Immutable creation timestamp
}
```

### Device States

- **AVAILABLE**: Device is available for use
- **IN_USE**: Device is currently in use
- **INACTIVE**: Device is inactive and unavailable

## API Endpoints

### Base URL
```
http://localhost:8080/api/v1/devices
```

### Create Device
```
POST /api/v1/devices
Content-Type: application/json

{
  "name": "Laptop",
  "brand": "Dell"
}

Response: 201 Created
{
  "id": 1,
  "name": "Laptop",
  "brand": "Dell",
  "state": "AVAILABLE",
  "creationTime": "2025-11-15T20:00:00"
}
```

### Get Device by ID
```
GET /api/v1/devices/{id}

Response: 200 OK
{
  "id": 1,
  "name": "Laptop",
  "brand": "Dell",
  "state": "AVAILABLE",
  "creationTime": "2025-11-15T20:00:00"
}
```

### Get All Devices (with pagination)
```
GET /api/v1/devices?page=0&size=10&sort=id,desc

Response: 200 OK
{
  "content": [...],
  "pageable": {...},
  "totalPages": 1,
  "totalElements": 5,
  ...
}
```

### Get Devices by Brand
```
GET /api/v1/devices/brand/{brand}?page=0&size=10

Response: 200 OK
{
  "content": [...],
  "pageable": {...},
  ...
}
```

### Get Devices by State
```
GET /api/v1/devices/state/{state}?page=0&size=10

Valid states: AVAILABLE, IN_USE, INACTIVE

Response: 200 OK
{
  "content": [...],
  "pageable": {...},
  ...
}
```

### Update Device (Full Update)
```
PUT /api/v1/devices/{id}
Content-Type: application/json

{
  "name": "Laptop Pro",
  "brand": "Dell",
  "state": "AVAILABLE"
}

Response: 200 OK
{
  "id": 1,
  "name": "Laptop Pro",
  "brand": "Dell",
  "state": "AVAILABLE",
  "creationTime": "2025-11-15T20:00:00"
}
```

### Update Device (Partial Update)
```
PATCH /api/v1/devices/{id}
Content-Type: application/json

{
  "state": "INACTIVE"
}

Response: 200 OK
{
  "id": 1,
  "name": "Laptop",
  "brand": "Dell",
  "state": "INACTIVE",
  "creationTime": "2025-11-15T20:00:00"
}
```

### Delete Device
```
DELETE /api/v1/devices/{id}

Response: 204 No Content
```

## Domain Validations

### Immutable Fields
- **creationTime**: Cannot be updated after creation (set via @PrePersist)

### State-Based Rules
- **Name/Brand Updates**: Cannot update name or brand if device is IN_USE
- **Deletion**: Cannot delete a device that is IN_USE

### Input Validation
- **name**: Required, non-blank
- **brand**: Required, non-blank
- **state**: Must be valid enum value (AVAILABLE, IN_USE, INACTIVE)

### Error Responses

#### Validation Error (400)
```json
{
  "timestamp": "2025-11-15T20:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more validation errors occurred",
  "validationErrors": {
    "name": "Device name is required"
  },
  "path": "/api/v1/devices"
}
```

#### Business Rule Violation (400)
```json
{
  "timestamp": "2025-11-15T20:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot update name or brand of a device that is IN_USE",
  "path": "/api/v1/devices/1"
}
```

#### Device Not Found (404)
```json
{
  "timestamp": "2025-11-15T20:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Device not found with id: 999",
  "path": "/api/v1/devices/999"
}
```

## Setup Instructions

### Prerequisites

- Java 21 or higher
- Maven 3.9 or higher (or use `./mvnw`)
- Docker & Docker Compose (for containerized setup)
- PostgreSQL 15 (for local setup without Docker)

### Local Setup (without Docker)

1. **Clone the repository**
```bash
git clone https://github.com/your-username/devices-api.git
cd devices-api
```

2. **Start PostgreSQL** (Docker or local installation)
```bash
# Using Docker
docker run --name devices-postgres \
  -e POSTGRES_DB=devices_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine
```

3. **Build the application**
```bash
mvn clean install
```

4. **Run the application**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

5. **Access the API**
- API: http://localhost:8080/api/v1/devices
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI spec: http://localhost:8080/v3/api-docs

### Docker Setup

1. **Build and run with Docker Compose**
```bash
docker-compose up -d
```

This will:
- Start PostgreSQL container with volume persistence
- Build and start the Spring Boot application
- Create necessary networks and volumes

2. **Access the application**
- API: http://localhost:8080/api/v1/devices
- Swagger UI: http://localhost:8080/swagger-ui.html

3. **View logs**
```bash
docker-compose logs -f app
```

4. **Stop the containers**
```bash
docker-compose down
```

5. **Clean up volumes** (removes database data)
```bash
docker-compose down -v
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Unit Tests Only
```bash
mvn test -DskipITs -Dtest="DeviceServiceTest,DeviceControllerTest"
```

### Run Integration Tests
```bash
mvn failsafe:integration-test -DskipUnitTests
```

### Generate Test Coverage Report
```bash
mvn clean test jacoco:report
```

### Test Statistics
- **Unit Tests**: 23 tests (DeviceServiceTest, DeviceControllerTest)
- **Integration Tests**: 9 tests (DeviceControllerIntegrationTest with TestContainers)
- **Total Coverage**: 34 comprehensive test cases
- **Test Status**: All tests passing ✅

## Application Profiles

### Default Profile
Used for general development with basic configuration.

### Dev Profile (`application-dev.properties`)
Development profile with:
- Formatted SQL logging
- DEBUG level logging for application code
- Full SQL parameter logging
- Drop and recreate database on startup

Run with: `--spring.profiles.active=dev`

### Test Profile (`application-test.properties`)
Used automatically during test execution with TestContainers PostgreSQL.

## Configuration

### Database Configuration
```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/devices_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### JPA/Hibernate Configuration
```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## API Documentation

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Spec
View the raw OpenAPI 3.0 specification at:
```
http://localhost:8080/v3/api-docs
```

## Architecture Notes

### Layered Architecture
1. **Controller Layer** (`DeviceController`): Handles HTTP requests, validation, and responses
2. **Service Layer** (`DeviceService`): Contains business logic and domain validations
3. **Repository Layer** (`DeviceRepository`): Data access with custom JPQL queries
4. **Entity Layer** (`Device`): JPA-mapped domain entity

### Design Patterns
- **DTO Pattern**: Separation of API contracts from domain entities
- **Builder Pattern**: Flexible object construction (Lombok @Builder)
- **Repository Pattern**: Abstraction of data access logic
- **Exception Handling**: Centralized global exception handler (@ControllerAdvice)

### Validations
- **Bean Validation**: Request-level validation with @Valid annotations
- **Domain Validation**: Service-level business rule validation
- **Entity Validation**: JPA annotations for column constraints

## Future Improvements

### Security
- [ ] Implement API Key authentication
- [ ] Add JWT token-based authentication
- [ ] Implement role-based access control (RBAC)
- [ ] Add request rate limiting

### Monitoring & Observability
- [ ] Add Spring Boot Actuator endpoints
- [ ] Implement distributed tracing with Spring Cloud Sleuth
- [ ] Add metrics collection with Micrometer
- [ ] Configure centralized logging

### Database
- [ ] Add database migration tool (Flyway or Liquibase)
- [ ] Implement soft delete capability
- [ ] Add audit trail (createdBy, updatedBy, updatedAt)
- [ ] Add database indexes for performance

### API Enhancements
- [ ] Implement sorting on all endpoints
- [ ] Add advanced filtering (device properties)
- [ ] Add bulk operations (bulk update/delete)
- [ ] Implement HATEOAS for discoverability

### Testing
- [ ] Add performance/load testing
- [ ] Implement API contract testing
- [ ] Add chaos engineering tests
- [ ] Increase coverage to 90%+

### DevOps
- [ ] Add GitHub Actions CI/CD pipeline
- [ ] Implement Kubernetes manifests (Helm charts)
- [ ] Add SonarQube integration for code quality
- [ ] Configure automated security scanning

## Known Issues & Limitations

### Current Limitations
1. **No Authentication**: API is currently open without authentication
2. **No Pagination HATEOAS**: Page responses use Spring Data's PageImpl (warning logged)
3. **DDL Auto**: Using `create-drop` - recreates schema on each startup
4. **No Soft Delete**: Deletions are permanent
5. **Limited Audit Trail**: Only creation time tracked

### Potential Issues
1. **TestContainers Startup Time**: Integration tests may take longer due to container initialization
2. **Database Locking**: No optimistic/pessimistic locking mechanism
3. **No Caching**: All queries hit the database

## Troubleshooting

### Port Already in Use
If port 8080 is already in use:
```bash
# Change port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Database Connection Issues
```bash
# Verify PostgreSQL is running
psql -U postgres -d devices_db

# Check connection string in application-dev.properties
```

### Docker Issues
```bash
# Clear Docker resources
docker system prune -a

# Rebuild without cache
docker-compose build --no-cache
```

## Contributing

1. Create feature branches for new features
2. Follow conventional commits format
3. Ensure all tests pass before submitting PR
4. Maintain test coverage above 70%
5. Update documentation as needed

## License

MIT License - see LICENSE file for details

## Support

For issues, questions, or suggestions, please open an issue on GitHub or contact the development team.

---

**Last Updated**: November 15, 2025  
**Version**: 1.0.0  
**Status**: Production Ready ✅
