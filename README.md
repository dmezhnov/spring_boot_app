## Spring Boot REST API Application

Spring Boot application with REST controllers for working with JSON data.

### Project Structure

```text
.  // Project root
├── README.md      // Project documentation and API/contracts
├── ROADMAP.md      // High-level roadmap of planned improvements
├── ROADMAP.ru.md      // Russian translation of the project roadmap
├── build.gradle   // Gradle build configuration
├── settings.gradle   // Gradle settings (root project name, modules)
├── gradle.properties   // Shared Gradle properties
├── mise.toml      // mise tasks and toolchain configuration
├── configs/       // Infrastructure and environment configuration
│   ├── docker-compose.yml      // Dockerized PostgreSQL service for the app
│   └── java-formatter.xml      // Java code style/formatter profile for IDEs
├── gradle/        // Gradle wrapper configuration
│   └── wrapper/
│       ├── gradle-wrapper.jar        // Gradle wrapper binary (generated)
│       └── gradle-wrapper.properties        // Gradle wrapper settings
├── scripts/       // Bun automation scripts for running, building and testing the app
│   ├── start.bun.ts      // CLI entrypoint: start Docker + Spring Boot in background
│   ├── stop.bun.ts      // CLI entrypoint: stop app and Docker resources started by scripts
│   ├── test.bun.ts      // CLI entrypoint: run test suite (delegates to lib/test-script)
│   ├── build.bun.ts      // CLI entrypoint: build Gradle sources (delegates to lib/build-script.bun.ts)
│   ├── save.bun.ts      // CLI entrypoint for helper script defined in lib/save-script.bun.ts
│   └── lib/      // Shared helpers for Bun scripts
│       ├── process-runner.bun.ts        // Utility to run external processes with logging and error handling
│       ├── run-env.bun.ts        // Central locations for run directories, state and log files
│       ├── docker-cli.bun.ts        // Helper to resolve Docker CLI binary on different platforms
│       ├── start-script.bun.ts        // Implementation of start logic (Docker + app boot)
│       ├── stop-script.bun.ts        // Implementation of stop/cleanup logic
│       ├── test-script.bun.ts        // Implementation of test orchestration (Gradle + Bruno + cleanup)
│       └── save-script.bun.ts        // Implementation of Git draft-branch save/push workflow
├── bruno/         // Bruno API test workspace
│   ├── bruno.json      // Bruno collection configuration
│   ├── mise.toml      // Local mise tasks for running Bruno tests
│   ├── General/      // Bruno requests for general /api endpoints
│   │   ├── Echo.bru        // Bruno request for "Echo" scenario
│   │   ├── Info.bru        // Bruno request for "Info" scenario
│   │   ├── Transform.bru        // Bruno request for "Transform" scenario
│   │   └── Welcome.bru        // Bruno request for "Welcome" scenario
│   ├── Products/      // Bruno requests for product endpoints
│   │   ├── Apply Discount.bru        // Bruno request for discount scenario
│   │   ├── Calculate Statistics.bru        // Bruno request for product statistics scenario
│   │   ├── Create Product.bru        // Bruno request for product creation scenario
│   │   └── Health Check.bru        // Bruno request for product health check
│   ├── Users/      // Bruno requests for user endpoints
│   │   ├── Health Check.bru        // Bruno request for user health check
│   │   ├── Process User.bru        // Bruno request for user processing scenario
│   │   ├── Register User.bru        // Bruno request for user registration scenario
│   │   └── Validate User.bru        // Bruno request for user validation scenario
│   └── test-bruno.bun.ts      // Bun script to run Bruno tests from this workspace
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── Application.java      // Application interface (marker type)
│   │   │           ├── ApplicationImpl.java      // Spring Boot entry point (@SpringBootApplication)
│   │   │           ├── controller/      // REST controllers for API endpoints
│   │   │           │   ├── ApiController.java        // Basic JSON utilities under /api
│   │   │           │   ├── ProductController.java        // Product-related endpoints under /api/products
│   │   │           │   └── UserController.java        // User-related endpoints under /api/users
│   │   │           ├── dto/      // Data transfer object interfaces + implementations
│   │   │           │   ├── ProductRequest.java        // Interface for product request DTO
│   │   │           │   ├── ProductRequestImpl.java        // Public-field product request implementation
│   │   │           │   ├── ProductResponse.java        // Interface for product response DTO
│   │   │           │   ├── ProductResponseImpl.java        // Public-field product response implementation
│   │   │           │   ├── UserRequest.java        // Interface for user request DTO
│   │   │           │   ├── UserRequestImpl.java        // Public-field user request implementation
│   │   │           │   ├── UserResponse.java        // Interface for user response DTO
│   │   │           │   └── UserResponseImpl.java        // Public-field user response implementation
│   │   │           ├── register/      // Application services (register/use-case layer)
│   │   │           │   ├── ProductRegister.java        // Interface for product registration logic
│   │   │           │   ├── ProductRegisterImpl.java        // Implementation: product calculations + persistence
│   │   │           │   ├── UserRegister.java        // Interface for user processing/validation logic
│   │   │           │   └── UserRegisterImpl.java        // Implementation: user processing/validation + persistence
│   │   │           └── repository/      // Repositories for JDBC-based persistence
│   │   │               ├── ProductRepository.java        // Contract for persisting/finding products
│   │   │               ├── ProductRepositoryImpl.java        // JdbcTemplate-based product repository implementation
│   │   │               ├── UserRepository.java        // Contract for persisting/finding users
│   │   │               └── UserRepositoryImpl.java        // JdbcTemplate-based user repository implementation
│   │   └── resources/
│   │       ├── application.properties      // Spring Boot and datasource configuration
│   │       └── db/
│   │           └── changelog/
│   │               ├── db.changelog-master.yaml      // Liquibase root changelog
│   │               └── db.changelog-1.0-init.yaml      // Initial schema changelog
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   ├── ApplicationTest.java      // Tests application entrypoint annotations
│                   ├── dto/      // DTO-level tests
│                   │   ├── ProductRequestTest.java        // Tests ProductRequestImpl builder and fields
│                   │   ├── ProductResponseTest.java        // Tests ProductResponseImpl builder and fields
│                   │   ├── UserRequestTest.java        // Tests UserRequestImpl builder and fields
│                   │   └── UserResponseTest.java        // Tests UserResponseImpl builder and fields
│                   ├── register/      // Service-layer tests for register implementations
│                   │   ├── ProductRegisterTest.java        // Tests product register logic + persistence with PostgreSQL
│                   │   └── UserRegisterTest.java        // Tests user register logic + persistence with PostgreSQL
│                   └── repository/      // Repository-level integration tests
│                       ├── ProductRepositoryTest.java        // Tests ProductRepositoryImpl with real PostgreSQL/Liquibase
│                       └── UserRepositoryTest.java        // Tests UserRepositoryImpl with real PostgreSQL/Liquibase
├── node_modules/      // Node.js/Bun dependencies for tooling and scripts (generated)
├── build/      // Generated Gradle build output (do not edit manually)
│   ├── classes/      // Compiled main and test classes (generated)
│   ├── generated/      // Generated sources and metadata (generated)
│   ├── libs/      // Built application JARs (generated)
│   ├── reports/      // Test and build reports (generated)
│   ├── resources/      // Processed resources (generated)
│   ├── test-results/      // Machine-readable test results (generated)
│   ├── tmp/      // Temporary Gradle files and caches (generated)
│   └── resolvedMainClassName      // Gradle-produced file with resolved main class (generated)
```

### API Endpoints

#### General API (`/api`)

- `GET /api/welcome` – returns a welcome message with application version and status.
- `POST /api/echo` – echoes back the raw request body with metadata (timestamp, type).
- `GET /api/info` – returns basic Java and OS information.
- `POST /api/transform` – returns the input map, list of keys and keys count.

#### User Controller (`/api/users`)

1. **Process User**

- **URL**: `POST /api/users/process`
- **Description**: Process user data and return a normalized representation.
- **Request**:

```json
{ "name": "John Doe", "email": "john@example.com", "age": 30 }
```

- **Response example**:

```json
{
  "id": 1,
  "name": "JOHN DOE",
  "email": "john@example.com",
  "age": 30,
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00"
}
```

2. **Validate User**

- **URL**: `POST /api/users/validate`
- **Description**: Validate user data (name and email) and return validation status.
- **Request**: same as **Process User**.
- **Response**: user response JSON with `status: "VALIDATED"`.

3. **Register User**

- **URL**: `POST /api/users/register`
- **Description**: Register a new user using the same processing logic as **Process User**.
- **Request**: same as **Process User**.
- **Response**: user response JSON (HTTP `201 Created`).

4. **Find User by Email**

- **URL**: `GET /api/users/by-email?email=...`
- **Description**: Look up a user by email address.
- **Responses**:
  - `200 OK` – user found.
  - `400 Bad Request` – missing or empty `email` parameter.
  - `404 Not Found` – user not found.

5. **Health Check**

- **URL**: `GET /api/users/health`
- **Response body**: `"User service is healthy"`.

#### Product Controller (`/api/products`)

1. **Create Product**

- **URL**: `POST /api/products/create`
- **Description**: Create a new product and calculate its total value and availability.
- **Request**:

```json
{
  "title": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "quantity": 5
}
```

- **Response example**:

```json
{
  "id": 1000,
  "title": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "quantity": 5,
  "totalValue": 4999.95,
  "category": "GENERAL",
  "available": true
}
```

2. **Apply Discount**

- **URL**: `POST /api/products/discount?discount=20`
- **Description**: Apply a percentage discount to a product.
- **Parameters**:
  - `discount` (0–100, default: `10`) – discount percentage.
- **Request**: same as **Create Product**.
- **Response**: product response JSON with `category: "DISCOUNTED"` and updated price.

3. **Calculate Statistics**

- **URL**: `POST /api/products/calculate`
- **Description**: Recalculate and persist product statistics (total value and availability) based on the request body, using the same logic as **Create Product** (the resulting product is saved to the database when a repository is configured).
- **Request**: same as **Create Product**.
- **Response**: product response JSON with calculated fields.

4. **Find Product by Title**

- **URL**: `GET /api/products/by-title?title=...`
- **Description**: Look up a product by its title.
- **Responses**:
  - `200 OK` – product found.
  - `400 Bad Request` – missing or empty `title` parameter.
  - `404 Not Found` – product not found.

5. **Health Check**

- **URL**: `GET /api/products/health`
- **Response body**: `"Product service is healthy"`.

### Running the Application

#### Install tools and build the project

```bash
mise install   # or: mise i
```

This will install all required tools and run the Gradle build via the `postinstall` hook.

#### Start the application (with PostgreSQL via Docker)

```bash
mise run start
```

The application will start on `http://localhost:8080` and will use PostgreSQL from `configs/docker-compose.yml`.

#### Run all tests (Gradle + Bruno)

```bash
# Gradle tests only (default)
mise run test

# Bruno API tests only
mise run test --bruno

# Full test suite: Gradle + Bruno
mise run test --full
```

All of these commands will:

- ensure the Dockerized PostgreSQL and Spring Boot application are running (starting them if needed),
- run Gradle tests when enabled for the chosen mode,
- run Bruno HTTP tests when enabled for the chosen mode,
- and then stop only the application environment and Docker resources that were started by the test script (pre-existing services are reused and left running).

### Configuration

Main application properties are in `src/main/resources/application.properties`:

- `server.port=8080` – HTTP server port.
- `logging.level.root=INFO` – root logging level.
- `spring.jackson.serialization.indent-output=true` – pretty-printed JSON output.
- `spring.datasource.*` – PostgreSQL connection settings (must match `configs/docker-compose.yml`).
- `spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml` – Liquibase changelog location.
