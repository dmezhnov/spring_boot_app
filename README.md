# Spring Boot REST API Application

Приложение Spring Boot с REST контроллерами для работы с JSON данными.

## Структура проекта

```

src/
└── main/
    ├── java/
    │   └── com/
    │       └── example/
    │           ├── Application.java
    │           ├── controller/
    │           │   ├── ApiController.java
    │           │   ├── ProductController.java
    │           │   └── UserController.java
    │           ├── dto/
    │           │   ├── ProductRequest.java
    │           │   ├── ProductResponse.java
    │           │   ├── UserRequest.java
    │           │   └── UserResponse.java
    │           └── service/
    │               ├── ProductService.java
    │               └── UserService.java
    └── resources/
        └── application.properties

```

## API Endpoints

### User Controller (`/api/users`)

#### 1. Process User

-   **URL**: `POST /api/users/process`
-   **Description**: Обработка данных пользователя
-   **Request**:

```json
{  "name": "John Doe",  "email": "john@example.com",  "age": 30}
```

-   **Response**:

```json
{  "id": 1,  "name": "JOHN DOE",  "email": "john@example.com",  "age": 30,  "status": "ACTIVE",  "createdAt": "2024-01-15T10:30:00"}
```

#### 2. Validate User

-   **URL**: `POST /api/users/validate`
-   **Description**: Валидация данных пользователя
-   **Request**: см. Process User
-   **Response**: UserResponse с `status: "VALIDATED"`

#### 3. Register User

-   **URL**: `POST /api/users/register`
-   **Description**: Регистрация нового пользователя
-   **Request**: см. Process User
-   **Response**: UserResponse (HTTP 201 Created)

#### 4. Health Check

-   **URL**: `GET /api/users/health`
-   **Response**: `"User service is healthy"`

### Product Controller (`/api/products`)

#### 1. Create Product

-   **URL**: `POST /api/products/create`
-   **Description**: Создание нового продукта
-   **Request**:

```json
{  "title": "Laptop",  "description": "High-performance laptop",  "price": 999.99,  "quantity": 5}
```

-   **Response**:

```json
{  "id": 1000,  "title": "Laptop",  "description": "High-performance laptop",  "price": 999.99,  "quantity": 5,  "totalValue": 4999.95,  "category": "GENERAL",  "available": true}
```

#### 2. Apply Discount

-   **URL**: `POST /api/products/discount?discount=20`
-   **Description**: Применение скидки на продукт
-   **Parameters**:
    -   `discount` (0-100, default: 10) - процент скидки
-   **Request**: см. Create Product
-   **Response**: ProductResponse с `category: "DISCOUNTED"` и измененной ценой

#### 3. Calculate Statistics

-   **URL**: `POST /api/products/calculate`
-   **Description**: Расчет статистики по продукту
-   **Request**: см. Create Product
-   **Response**: ProductResponse

#### 4. Health Check

-   **URL**: `GET /api/products/health`
-   **Response**: `"Product service is healthy"`

## Запуск приложения

### Построение проекта

```bash
mise i
```

### Запуск приложения

```bash
mise start
```

Приложение запустится на `http://localhost:8080`

### Запуск тестов

```bash
mise test
```

## Конфигурация

Параметры приложения в `src/main/resources/application.properties`:

-   `server.port=8080` - порт сервера
-   `logging.level.root=INFO` - уровень логирования
-   `spring.jackson.serialization.indent-output=true` - красивый вывод JSON
