# Identity Service API

## Authentication APIs

### **Register**
`POST /api/v1/auth/register`

Registers a new user.

#### Request Body
```json
{
  "fullName": "Sammed Jain",
  "email": "sammed@samsung.com",
  "password": "password"
}
```

#### Response
- `201 Created` → User registered successfully
- `400 Bad Request` → Validation errors or email already in use

```json
{
    "userId": "0be3f348-db3f-4713-9e00-2c0975c654c3",
    "fullName": "Sammed Jain",
    "email": "sammed@msung.com",
    "phone": "9090909090",
    "status": "PENDING_VERIFICATION",
    "corporateVerified": false
}
```
---

### **Login**
`POST /api/v1/auth/login`

Authenticates the user and returns a JWT token.

#### Request Body
```json
{
  "email": "sammed@email.com",
  "password": "password"
}
```

#### Response
- `200 OK` →  User authenticated successfully 
- `400 Bad Request` → Invalid credentials
```json
{
  "token": "jwt-token-here"
}
```


---

### **Send Verification Email**
`POST /api/v1/auth/send-verification-email`

Sends an email verification link to the provided email.

#### Request Body
```json
{
  "email": "sammed@email.com"
}
```


#### Response
- `200 OK` → Email sent
- `409 Too Many Requests ` → Rate limit exceeded

```json
{
  "status": "success",
  "message": "Verification email sent successfully to sammed@email.com"
}
```


---

### **Verify Email**
`GET /api/v1/auth/verify-email?token={token}`

Verifies a user’s email using a token.

#### Request
```http
GET /api/v1/auth/verify-email?token=jxMb-I30UjcIZeJ261fT1AxLPVa9QZjeE2uuht5Vv4Y
```

#### Response
- `200 OK` → Email verified
- `400 Bad Request` → Invalid or expired token
- `409 Too Many Requests ` → Rate limit exceeded

#### Response
```json
{
  "status": "success",
  "message": "Email verified successfully"
}
```

---

## User APIs

### **Get My Profile**
`GET /api/v1/users/me`

Fetches profile of the authenticated user.

#### Headers
```
Authorization: Bearer <jwt-token>
```

#### Response
- `200 OK` → User profile fetched successfully
- `401 Unauthorized` → Invalid or missing token
```json
{
  "userId": "d989f6ff-fb14-485b-96ea-a28a34e469b2",
  "fullName": "Sammed Jain",
  "email": "sammed@email.com",
  "phone": "9090909090",
  "status": "ACTIVE",
  "corporateVerified": true
}
```


---

## Health Check

### **Health**
`GET /actuator/health`

Checks if the service is running.

#### Response
```json
{
  "status": "UP"
}
```

---
## Error Handling
All error responses follow this structure:

```json
{
  "timestamp": "2025-09-04T20:18:28.7083393",
  "status": 400,
  "error": "Bad Request",
  "code": "ERR_400",
  "message": "Invalid request",
  "details": {
    "cause": "Email is already in use"
  },
  "path": "/api/v1/auth/register"
}
```
```json
{
    "timestamp": "2025-09-04T20:09:20.0867354",
    "status": 401,
    "error": "Unauthorized",
    "code": "ERR_401_JWT",
    "message": "Invalid or expired JWT token",
    "details": {
        "JWTException": "JWT strings must contain exactly 2 period characters. Found: 0",
        "cause": "Full authentication is required to access this resource"
    },
    "path": "/api/v1/users/me"
}
```
- `timestamp`: Time of the error
- `status`: HTTP status code
- `error`: Error type
- `code`: Application-specific error code
- `message`:  Human-readable error message
- `details`: Cause and additional error details (optional)
- `path`: Request path that caused the error
