## Spring Boot REST API Application

Spring Boot application with REST controllers for working with JSON data.

### Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           ├── Application.java        // interface
│   │           ├── ApplicationImpl.java    // Spring Boot entry point
│   │           ├── controller/
│   │           │   ├── ApiController.java
│   │           │   ├── ProductController.java
│   │           │   └── UserController.java
│   │           ├── dto/
│   │           │   ├── ProductRequest.java
│   │           │   ├── ProductRequestImpl.java
│   │           │   ├── ProductResponse.java
│   │           │   ├── ProductResponseImpl.java
│   │           │   ├── UserRequest.java
│   │           │   ├── UserRequestImpl.java
│   │           │   ├── UserResponse.java
│   │           │   └── UserResponseImpl.java
│   │           ├── register/
│   │           │   ├── ProductRegister.java
│   │           │   ├── ProductRegisterImpl.java
│   │           │   ├── UserRegister.java
│   │           │   └── UserRegisterImpl.java
│   │           └── repository/
│   │               ├── ProductRepository.java
│   │               ├── ProductRepositoryImpl.java
│   │               ├── UserRepository.java
│   │               └── UserRepositoryImpl.java
│   └── resources/
│       ├── application.properties
│       └── db/
│           └── changelog/
│               ├── db.changelog-master.yaml
│               └── db.changelog-1.0-init.yaml
└── test/
    └── java/
        └── com/
            └── example/
                ├── ApplicationTest.java
                ├── dto/
                │   ├── ProductRequestTest.java
                │   ├── ProductResponseTest.java
                │   ├── UserRequestTest.java
                │   └── UserResponseTest.java
                ├── register/
                │   ├── ProductRegisterTest.java
                │   └── UserRegisterTest.java
                └── repository/
                    ├── ProductRepositoryTest.java
                    └── UserRepositoryTest.java
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
- **Description**: Recalculate product statistics (total value and availability) based on the request body.
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
mise run test
```

This command will:

- ensure the Dockerized PostgreSQL and Spring Boot application are running (starting them if needed),
- run Gradle tests,
- run Bruno HTTP tests,
- and then stop only the application environment and Docker resources that were started by the test script (pre-existing services are reused and left running).

### Configuration

Main application properties are in `src/main/resources/application.properties`:

- `server.port=8080` – HTTP server port.
- `logging.level.root=INFO` – root logging level.
- `spring.jackson.serialization.indent-output=true` – pretty-printed JSON output.
- `spring.datasource.*` – PostgreSQL connection settings (must match `configs/docker-compose.yml`).
- `spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml` – Liquibase changelog location.
