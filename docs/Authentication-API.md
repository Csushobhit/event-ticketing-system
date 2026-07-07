# Authentication APIs

---

# POST /api/auth/register

## Controller Entry Point

```text
Browser / Postman
        |
        v
POST /api/auth/register
        |
        v
AuthController.register(RegisterRequest request)
```

---

## Complete Execution Path

```text
Client
 |
 v
POST /api/auth/register
 |
 v
DispatcherServlet
 |
 v
AuthController.register(RegisterRequest request)
 |
 v
AuthService.register(RegisterRequest request)
 |
 +--------------------------------------------------+
 | userRepository.findByEmail(request.getEmail())   |
 +--------------------------------------------------+
 |
 |---- User Exists?
 |         |
 |         +---- YES
 |         |       |
 |         |       v
 |         |  throw IllegalStateException
 |         |
 |         +---- NO
 |
 v
passwordEncoder.encode(request.getPassword())
 |
 v
User.builder()
    .name(...)
    .email(...)
    .password(...)
    .roles(Set.of(Role.ROLE_USER))
    .build()
 |
 v
userRepository.save(user)
 |
 v
PostgreSQL
 |
 v
JwtService.generateToken(user)
 |
 v
JwtService.generateToken(Map<String,Object>, UserDetails)
 |
 v
Jwts.builder()
 |
 v
JWT Token Generated
 |
 v
AuthenticationResponse.builder()
        .token(jwtToken)
        .build()
 |
 v
AuthController
 |
 v
HTTP 200 Response
 |
 v
Client Receives JWT
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client / Browser"]

B["POST /api/auth/register"]

C["AuthController.register()"]

D["AuthService.register()"]

E["UserRepository.findByEmail()"]

F{"Email Exists?"}

G["Throw IllegalStateException"]

H["PasswordEncoder.encode()"]

I["User.builder()"]

J["UserRepository.save()"]

K["PostgreSQL"]

L["JwtService.generateToken()"]

M["Jwts.builder()"]

N["JWT Generated"]

O["AuthenticationResponse"]

P["HTTP Response"]

A --> B
B --> C
C --> D
D --> E
E --> F

F -->|Yes| G

F -->|No| H
H --> I
I --> J
J --> K

K --> L
L --> M
M --> N

N --> O
O --> P
```

---

# POST /api/auth/login

## Controller Entry Point

```text
Browser / Postman
        |
        v
POST /api/auth/login
        |
        v
AuthController.login(AuthenticationRequest request)
```

---

## Complete Execution Path

```text
Client
 |
 v
POST /api/auth/login
 |
 v
DispatcherServlet
 |
 v
AuthController.login(AuthenticationRequest request)
 |
 v
AuthService.authenticate(AuthenticationRequest request)
 |
 v
AuthenticationManager.authenticate(
    UsernamePasswordAuthenticationToken
)
 |
 v
DaoAuthenticationProvider.authenticate()
 |
 v
CustomUserDetailsService.loadUserByUsername(email)
 |
 v
UserRepository.findByEmail(email)
 |
 v
PostgreSQL
 |
 v
User Entity Returned
 |
 v
BCryptPasswordEncoder.matches()
 |
 |---- Password Valid?
 |         |
 |         +---- NO
 |         |       |
 |         |       v
 |         |  AuthenticationException
 |         |
 |         +---- YES
 |
 v
Authentication Success
 |
 v
UserRepository.findByEmail(email)
 |
 v
PostgreSQL
 |
 v
User Entity Returned
 |
 v
JwtService.generateToken(user)
 |
 v
JwtService.generateToken(Map<String,Object>, UserDetails)
 |
 v
Jwts.builder()
 |
 v
JWT Token Generated
 |
 v
AuthenticationResponse.builder()
        .token(jwtToken)
        .build()
 |
 v
AuthController
 |
 v
HTTP 200 Response
 |
 v
Client Receives JWT
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client / Browser"]

B["POST /api/auth/login"]

C["AuthController.login()"]

D["AuthService.authenticate()"]

E["AuthenticationManager.authenticate()"]

F["DaoAuthenticationProvider.authenticate()"]

G["CustomUserDetailsService.loadUserByUsername()"]

H["UserRepository.findByEmail()"]

I["PostgreSQL"]

J["User Entity"]

K["BCryptPasswordEncoder.matches()"]

L{"Password Valid?"}

M["AuthenticationException"]

N["Authentication Success"]

O["UserRepository.findByEmail()"]

P["PostgreSQL"]

Q["JwtService.generateToken()"]

R["Jwts.builder()"]

S["JWT Generated"]

T["AuthenticationResponse"]

U["HTTP Response"]

A --> B
B --> C
C --> D
D --> E
E --> F
F --> G
G --> H
H --> I
I --> J

J --> K
K --> L

L -->|No| M

L -->|Yes| N

N --> O
O --> P

P --> Q
Q --> R
R --> S

S --> T
T --> U
```

---

# JWT Authentication Flow After Login

## Complete Execution Path For Any Protected Endpoint

Example:

```http
GET /api/users/me/tickets
Authorization: Bearer eyJ...
```

```text
Client
 |
 v
SecurityConfig.securityFilterChain()
 |
 v
JwtAuthenticationFilter.doFilterInternal()
 |
 v
request.getHeader("Authorization")
 |
 v
Extract JWT
 |
 v
JwtService.extractUsername(jwt)
 |
 v
JwtService.extractAllClaims(jwt)
 |
 v
Jwts.parser()
 |
 v
Email Extracted
 |
 v
CustomUserDetailsService.loadUserByUsername(email)
 |
 v
UserRepository.findByEmail(email)
 |
 v
PostgreSQL
 |
 v
UserDetails
 |
 v
JwtService.isTokenValid(jwt, userDetails)
 |
 v
UsernamePasswordAuthenticationToken
 |
 v
SecurityContextHolder
        .getContext()
        .setAuthentication(...)
 |
 v
Controller Method Executes
 |
 v
Service Method Executes
 |
 v
Repository Method Executes
 |
 v
PostgreSQL
 |
 v
Response Returned
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Protected API Request"]

B["SecurityConfig.securityFilterChain()"]

C["JwtAuthenticationFilter.doFilterInternal()"]

D["Authorization Header"]

E["Extract JWT"]

F["JwtService.extractUsername()"]

G["CustomUserDetailsService.loadUserByUsername()"]

H["UserRepository.findByEmail()"]

I["PostgreSQL"]

J["JwtService.isTokenValid()"]

K["UsernamePasswordAuthenticationToken"]

L["SecurityContextHolder.setAuthentication()"]

M["Controller"]

N["Service"]

O["Repository"]

P["PostgreSQL"]

A --> B
B --> C

C --> D
D --> E

E --> F

F --> G

G --> H

H --> I

I --> J

J --> K

K --> L

L --> M

M --> N

N --> O

O --> P
```