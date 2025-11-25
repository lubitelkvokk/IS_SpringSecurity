# Spring Security — REST API с JWT аутентификацией

Простое REST API приложение на Spring Boot с реализованной системой аутентификации, авторизации и базовыми мерами защиты.

## Описание проекта

Приложение предоставляет API для регистрации, входа и управления пользователями с использованием JWT токенов. Все данные хранятся в памяти (H2 база данных), что удобно для разработки и тестирования.

##  API Endpoints

### Аутентификация (`/auth`)

#### 1. **Регистрация пользователя**
```http
POST /auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```

**Ответ (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 2. **Вход в систему**
```http
POST /auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePassword123!"
}
```

**Ответ (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Пользовательский API (`/api`)

#### 3. **Получить список пользователей** (требует аутентификацию)
```http
GET /api/data
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Ответ (200 OK):**
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "ROLE_USER"
  }
]
```

### Любимые места (`/api/places`)

#### 3. **Получить все любимые места текущего пользователя**
```http
GET /api/places
Authorization: Bearer eyJhбGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Ответ (200 OK):**
```json
[
  {
    "id": 1,
    "placeName": "Парк культуры",
    "description": "Красивый парк в центре города"
  },
  {
    "id": 2,
    "placeName": "Кафе на набережной",
    "description": "Уютное кафе с видом на реку"
  }
]
```

#### 4. **Добавить новое любимое место**
```http
POST /api/places
Authorization: Bearer eyJhбGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "placeName": "Парк культуры",
  "description": "Красивый парк в центре города"
}
```

**Ответ (201 Created):**
```json
{
  "id": 1,
  "placeName": "Парк культуры",
  "description": "Красивый парк в центре города"
}
```

#### 5. **Обновить любимое место**
```http
PUT /api/places/{placeId}
Authorization: Bearer eyJhбGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "placeName": "Парк культуры (обновленное)",
  "description": "Красивый парк в центре города с новым названием"
}
```

#### 6. **Удалить любимое место**
```http
DELETE /api/places/{placeId}
Authorization: Bearer eyJhбGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Пользователи (`/api/users`)

#### 7. **Получить список всех пользователей**
```http
GET /api/users
Authorization: Bearer eyJhбGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### 8. **Получить профиль текущего пользователя**
```http
GET /api/users/profile
Authorization: Bearer eyJhбGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Ответ (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com"
}
```

---

## Меры защиты

### 1. **Аутентификация через JWT**

JWT (JSON Web Token) используется для безопасной аутентификации без сохранения сессии на сервере.

- **Где**: `JwtService.java`
- **Как работает**:
  - При регистрации/входе генерируется подписанный токен
  - Токен содержит: username, id, email, роль и время истечения
  - Каждый запрос к защищённым эндпоинтам должен содержать токен в заголовке `Authorization: Bearer <token>`
  - Токен проверяется на валидность и срок действия перед каждым запросом
- **Ключ подписи**: хранится в `application.yaml` как `token.signing.key` (BASE64-кодированный)

### 2. **Хеширование паролей (BCrypt)**

Пароли никогда не хранятся в открытом виде.

- **Где**: `SecurityConfiguration.java` → `BCryptPasswordEncoder`
- **Как работает**:
  - При регистрации пароль автоматически хешируется BCrypt
  - При входе введённый пароль сравнивается с хешем, а не с открытым паролем
  - BCrypt добавляет "соль" и замедленное хеширование, что делает перебор невозможным

```java
PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
String hashedPassword = passwordEncoder.encode("SecurePassword123!");
// Хеш: $2a$10$... (никогда не будет одинаковым для одного пароля)
```

### 3. **Защита от SQL-injection (SQLi)**

Приложение использует Spring Data JPA с параметризованными запросами.

- **Где**: `UserRepository.java` (наследует `JpaRepository`)
- **Как работает**:
  - Все запросы к БД выполняются через JPA, которая использует подготовленные операторы (prepared statements)
  - Входные данные передаются как параметры, а не конкатенируются в SQL-строку
  - Spring автоматически экранирует спецсимволы

```java
// Безопасно (JPA заранее подготавливает запрос)
User user = userRepository.findByUsername(username);

// Небезопасно (если бы использовали конкатенацию)
String query = "SELECT * FROM users WHERE username = '" + username + "'";
```

### 4. **Контроль доступа (Authorization)**

Доступ к эндпоинтам ограничивается в зависимости от аутентификации пользователя.

- **Где**: `SecurityConfiguration.java` и аннотация `@PreAuthorize`
- **Как работает**:
  - Путь `/api/**` требует аутентификацию (любой авторизованный пользователь)
  - Путь `/auth/**` открыт для всех (регистрация и вход)
  - Проверка происходит до выполнения метода контроллера
  - Эндпоинты `/api/places` проверяют принадлежность данных текущему пользователю на уровне сервиса

```java
@GetMapping("/places")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> getUserFavoritePlaces() {
    // Получаем данные только текущего пользователя
    return ResponseEntity.ok(favoritePlaceService.getUserFavoritePlaces(...));
}
```

