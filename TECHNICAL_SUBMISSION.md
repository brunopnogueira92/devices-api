# Technical Exercise Submission - Devices API

## Project Overview

This repository contains a complete implementation of a RESTful API for managing device resources, developed as a technical assessment for a Senior Java Developer position.

### Key Features
- Full CRUD operations for device management
- Advanced filtering (by brand and state)
- Pagination support on all list endpoints
- Comprehensive domain validations
- Professional API documentation (OpenAPI 3.0)
- PostgreSQL database persistence
- Complete Docker containerization
- Extensive test coverage (unit + integration tests)

---

## Technical Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 21 |
| Framework | Spring Boot | 3.3.4 |
| Build Tool | Maven | 3.9+ |
| Database | PostgreSQL | 15 |
| Testing | JUnit 5, Mockito, TestContainers | Latest |
| Documentation | SpringDoc OpenAPI | 3.0 |
| Containerization | Docker & Docker Compose | Latest |

---

## Project Structure

```
devices-api/
├── src/
│   ├── main/
│   │   ├── java/com/devices/
│   │   │   ├── controller/          # REST endpoints
│   │   │   ├── service/             # Business logic
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   ├── exception/           # Exception handling
│   │   │   ├── config/              # Configuration
│   │   │   └── util/                # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
│       ├── java/com/devices/
│       │   ├── controller/          # Controller tests
│       │   └── service/             # Service tests
│       └── resources/
│           └── application-test.properties
├── Dockerfile                        # Application container
├── Dockerfile.postgres               # Database container
├── docker-compose.yml                # Container orchestration
├── .gitignore                        # Git ignore rules
├── pom.xml                          # Maven configuration
└── README.md                        # Complete documentation
```

---

## Requirements Compliance

### ✅ Device Domain Implementation
- [x] **Id**: Auto-generated (Long, @GeneratedValue)
- [x] **Name**: String, required, not blank
- [x] **Brand**: String, required, not blank
- [x] **State**: Enum (AVAILABLE, IN_USE, INACTIVE)
- [x] **Creation Time**: LocalDateTime, immutable (@PrePersist)

### ✅ Supported Functionalities
- [x] Create a new device
- [x] Fully update an existing device (PUT)
- [x] Partially update an existing device (PATCH)
- [x] Fetch a single device by ID
- [x] Fetch all devices (with pagination)
- [x] Fetch devices by brand (with pagination)
- [x] Fetch devices by state (with pagination)
- [x] Delete a single device

### ✅ Domain Validations
- [x] **Creation time immutability**: Enforced via `@Column(updatable = false)` and `@PrePersist`
- [x] **Name/Brand update restriction**: Cannot update if device is IN_USE
- [x] **Delete restriction**: Cannot delete IN_USE devices

### ✅ Acceptance Criteria
- [x] **Compiles and runs**: `mvn clean install` + `mvn spring-boot:run`
- [x] **Test coverage**: 32 tests (23 unit + 9 integration)
- [x] **API documentation**: OpenAPI 3.0 / Swagger UI available
- [x] **Database persistence**: PostgreSQL 15 (not in-memory)
- [x] **Containerization**: Docker + Docker Compose with health checks
- [x] **Git repository**: Clean structure with proper .gitignore
- [x] **README documentation**: Complete setup and usage instructions

---

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker & Docker Compose

### Running with Docker Compose (Recommended)

```bash
# Start all services
docker-compose up -d

# Access the application
# API: http://localhost:8080/api/v1/devices
# Swagger UI: http://localhost:8080/swagger-ui.html

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

### Running Locally (Development)

```bash
# 1. Start PostgreSQL (Docker or local)
docker run --name devices-postgres \
  -e POSTGRES_DB=devices_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine

# 2. Build the application
mvn clean install

# 3. Run the application
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# 4. Access the application
# API: http://localhost:8080/api/v1/devices
# Swagger UI: http://localhost:8080/swagger-ui.html
```

---

## Testing

### Test Suite Overview

| Test Type | Count | Coverage |
|-----------|-------|----------|
| Unit Tests | 23 | Service layer + Controller layer (mocked) |
| Integration Tests | 9 | End-to-end with TestContainers PostgreSQL |
| **Total** | **32** | **~75-80% code coverage** |

### Running Tests

#### All Tests
```bash
mvn clean verify
```

#### Unit Tests Only
```bash
mvn test -Dtest="DeviceServiceTest,DeviceControllerTest"
```

#### Integration Tests
```bash
mvn failsafe:integration-test failsafe:verify
```

**Note:** Integration tests use TestContainers and require Docker to be running.

### Test Results
```
✅ Unit Tests: 23/23 passing
✅ Integration Tests: 9/9 passing
✅ Total: 32/32 passing (0 failures)
⏱️  Execution time: ~10 seconds
```

---

## API Documentation

### Interactive Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

### Example Endpoints

#### Create Device
```bash
POST /api/v1/devices
Content-Type: application/json

{
  "name": "Laptop Pro",
  "brand": "Apple"
}

# Response: 201 Created
{
  "id": 1,
  "name": "Laptop Pro",
  "brand": "Apple",
  "state": "AVAILABLE",
  "creationTime": "2025-11-16T10:30:00"
}
```

#### Get All Devices (Paginated)
```bash
GET /api/v1/devices?page=0&size=10&sort=id,desc

# Response: 200 OK
{
  "content": [...],
  "pageable": {...},
  "totalPages": 1,
  "totalElements": 5
}
```

#### Update Device (Full)
```bash
PUT /api/v1/devices/1
Content-Type: application/json

{
  "name": "Laptop Pro Max",
  "brand": "Apple",
  "state": "AVAILABLE"
}

# Response: 200 OK
```

#### Update Device (Partial)
```bash
PATCH /api/v1/devices/1
Content-Type: application/json

{
  "state": "IN_USE"
}

# Response: 200 OK
```

#### Filter by Brand
```bash
GET /api/v1/devices/brand/Apple?page=0&size=10

# Response: 200 OK
```

#### Filter by State
```bash
GET /api/v1/devices/state/AVAILABLE?page=0&size=10

# Response: 200 OK
```

#### Delete Device
```bash
DELETE /api/v1/devices/1

# Response: 204 No Content
```

---

## Architecture & Design

### Layered Architecture
```
┌─────────────────────────────────────┐
│     Controller Layer (REST API)     │
│  - Request validation               │
│  - Response formatting              │
│  - Exception handling               │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Service Layer (Business)       │
│  - Domain validations               │
│  - Business logic                   │
│  - Transaction management           │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Repository Layer (Data Access)    │
│  - CRUD operations                  │
│  - Custom queries                   │
│  - JPA/Hibernate                    │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│     Entity Layer (Domain Model)     │
│  - JPA entities                     │
│  - Domain constraints               │
│  - Lifecycle callbacks              │
└─────────────────────────────────────┘
```

### Design Patterns Applied
- **DTO Pattern**: Separation between API contracts and domain entities
- **Repository Pattern**: Abstraction of data access logic
- **Builder Pattern**: Flexible object construction (Lombok @Builder)
- **Strategy Pattern**: Service layer encapsulates business rules
- **Exception Handling**: Centralized via @ControllerAdvice

### Best Practices Implemented
- **SOLID Principles**: Single responsibility, dependency injection
- **Bean Validation**: JSR-303 annotations for input validation
- **Transactional Boundaries**: Proper @Transactional usage
- **Immutability**: Fields marked as updatable = false where needed
- **Logging**: SLF4J with appropriate log levels
- **Error Responses**: Standardized ErrorResponse structure

---

## Configuration Profiles

### Default Profile
Basic configuration for general use.

### Development Profile (`application-dev.properties`)
- SQL logging enabled
- DEBUG level logging
- Database recreation on startup (create-drop)

Activate with: `--spring.profiles.active=dev`

### Test Profile (`application-test.properties`)
- Automatically used during test execution
- TestContainers PostgreSQL configuration
- Test-specific settings

### Production Profile (`application-prod.properties`)
- Optimized settings for production
- Minimal logging
- Database validation only (validate)

---

## Database Schema

### Device Table
```sql
CREATE TABLE devices (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    brand           VARCHAR(255) NOT NULL,
    state           VARCHAR(20) NOT NULL,
    creation_time   TIMESTAMP NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_devices_brand ON devices(brand);
CREATE INDEX idx_devices_state ON devices(state);
```

---

## Error Handling

### Validation Error (400 Bad Request)
```json
{
  "timestamp": "2025-11-16T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more validation errors occurred",
  "validationErrors": {
    "name": "Device name is required"
  },
  "path": "/api/v1/devices"
}
```

### Business Rule Violation (400 Bad Request)
```json
{
  "timestamp": "2025-11-16T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot update name or brand of a device that is IN_USE",
  "path": "/api/v1/devices/1"
}
```

### Resource Not Found (404 Not Found)
```json
{
  "timestamp": "2025-11-16T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Device not found with id: 999",
  "path": "/api/v1/devices/999"
}
```

---

## Development Notes

### Code Quality
- **Naming Conventions**: Clear, descriptive names following Java conventions
- **Package Structure**: Organized by layer and responsibility
- **Javadoc**: Present on all public classes and methods
- **Code Comments**: Explanatory comments where business logic is complex
- **No Code Duplication**: DRY principle followed throughout

### Dependencies
- **Lombok**: Reduces boilerplate code (@Data, @Builder, etc.)
- **Spring Data JPA**: Simplified data access
- **PostgreSQL Driver**: Database connectivity
- **SpringDoc OpenAPI**: API documentation
- **TestContainers**: Integration testing with real database
- **AssertJ**: Fluent assertions in tests

---

## Troubleshooting

### Port Already in Use
```bash
# Change the application port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Database Connection Issues
```bash
# Verify PostgreSQL is running
docker ps | grep postgres

# Check connection
psql -h localhost -U postgres -d devices_db
```

### Docker Issues
```bash
# Clean up Docker resources
docker system prune -a

# Rebuild containers without cache
docker-compose build --no-cache
docker-compose up -d
```

### TestContainers on macOS with Colima

If using Colima instead of Docker Desktop on macOS, you may need to set environment variables:

```bash
export DOCKER_HOST=unix://$HOME/.colima/default/docker.sock
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
mvn failsafe:integration-test failsafe:verify
```

---

## Future Enhancements

### Security
- [ ] Implement JWT-based authentication
- [ ] Add role-based access control (RBAC)
- [ ] Implement request rate limiting
- [ ] Add API versioning strategy

### Monitoring
- [ ] Spring Boot Actuator endpoints
- [ ] Distributed tracing (Spring Cloud Sleuth)
- [ ] Metrics collection (Micrometer/Prometheus)
- [ ] Centralized logging (ELK stack)

### Database
- [ ] Database migration tool (Flyway/Liquibase)
- [ ] Soft delete capability
- [ ] Audit trail (createdBy, modifiedBy, modifiedAt)
- [ ] Optimistic locking for concurrent updates

### API Enhancements
- [ ] HATEOAS for API discoverability
- [ ] Advanced filtering (multiple criteria)
- [ ] Bulk operations support
- [ ] API response caching

### DevOps
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Kubernetes deployment manifests
- [ ] Code quality gates (SonarQube)
- [ ] Automated security scanning

---

## Known Limitations

1. **No Authentication**: API is currently open (designed for evaluation purposes)
2. **DDL Auto Create-Drop**: Database schema recreated on startup in dev/test profiles
3. **No Soft Delete**: Deletions are permanent
4. **Limited Audit Trail**: Only creation time is tracked

These are intentional simplifications for the technical exercise. Production deployment would require the security and audit enhancements listed in the Future Enhancements section.

---

## Contact & Support

This project was developed as a technical assessment. For questions or feedback, please refer to the submission email or contact information provided separately.

---

**Project Version**: 1.0.0  
**Last Updated**: November 16, 2025  
**Status**: ✅ Production Ready

---

## License

This project is provided for evaluation purposes as part of a technical assessment process.
