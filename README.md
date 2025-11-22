# Spring Boot REST API Application

Spring Boot application with REST controllers for working with JSON data.

## Project Structure

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── example/
    │           ├── Application.java        // interface
    │           ├── ApplicationImpl.java    // Spring Boot entry point
    │           ├── controller/
    │           │   ├── ApiController.java
    │           │   ├── ProductController.java
    │           │   └── UserController.java
    │           └── dto/
    │               ├── ProductRequest.java
    │               ├── ProductRequestImpl.java
    │               ├── ProductResponse.java
    │               ├── ProductResponseImpl.java
    │               ├── UserRequest.java
    │               ├── UserRequestImpl.java
    │               ├── UserResponse.java
    │               └── UserResponseImpl.java
    └── resources/
        └── application.properties
```

## API Endpoints

### User Controller (`/api/users`)

#### 1. Process User

- **URL**: `POST /api/users/process`
- **Description**: Process user data and return a normalized representation.
- **Request**:

```json
{ "name": "John Doe", "email": "john@example.com", "age": 30 }
```

- **Response**:

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

#### 2. Validate User

- **URL**: `POST /api/users/validate`
- **Description**: Validate user data (name and email) and return validation status.
- **Request**: same as **Process User**
- **Response**: user response JSON with `status: "VALIDATED"`.

#### 3. Register User

- **URL**: `POST /api/users/register`
- **Description**: Register a new user.
- **Request**: same as **Process User**
- **Response**: user response JSON (HTTP `201 Created`).

#### 4. Health Check

- **URL**: `GET /api/users/health`
- **Response**:

```text
"User service is healthy"
```

### Product Controller (`/api/products`)

#### 1. Create Product

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

- **Response**:

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

#### 2. Apply Discount

- **URL**: `POST /api/products/discount?discount=20`
- **Description**: Apply a percentage discount to a product.
- **Parameters**:
  - `discount` (0–100, default: `10`) – discount percentage.
- **Request**: same as **Create Product**
- **Response**: product response JSON with `category: "DISCOUNTED"` and updated price.

#### 3. Calculate Statistics

- **URL**: `POST /api/products/calculate`
- **Description**: Recalculate product statistics based on the request body.
- **Request**: same as **Create Product**
- **Response**: product response JSON with calculated fields.

#### 4. Health Check

- **URL**: `GET /api/products/health`
- **Response**:

```text
"Product service is healthy"
```

## Running the Application

### Install and build

```bash
mise i
```

### Start the application

```bash
mise start
```

The application will start on `http://localhost:8080`.

### Run tests

```bash
mise test
```

## Configuration

Main application properties are in `src/main/resources/application.properties`:

- `server.port=8080` – HTTP server port.
- `logging.level.root=INFO` – root logging level.
- `spring.jackson.serialization.indent-output=true` – pretty-printed JSON output.
