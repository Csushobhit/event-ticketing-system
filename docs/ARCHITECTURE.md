# Event Ticketing System - Architecture & API Flow

## High Level Architecture

```mermaid
flowchart TD

Client["Client / Postman"]

SC["SecurityConfig.java"]
JWTF["JwtAuthenticationFilter.java"]
JWTS["JwtService.java"]
CUDS["CustomUserDetailsService.java"]

CTRL["Controllers"]
SERVICE["Services"]
REPO["Repositories"]

DB[(PostgreSQL)]

Client --> SC
SC --> JWTF
JWTF --> JWTS
JWTF --> CUDS

CUDS --> REPO
REPO --> DB

JWTF --> CTRL
CTRL --> SERVICE
SERVICE --> REPO
REPO --> DB
```

---

# Authentication Flow

## User Registration

```mermaid
flowchart TD

A["POST /api/auth/register"]

B["AuthController.java"]

C["AuthService.java"]

D["PasswordEncoder<br/>BCrypt"]

E["UserRepository.java"]

F[(PostgreSQL)]

G["JwtService.java"]

H["JWT Response"]

A --> B
B --> C

C --> D
C --> E

E --> F

C --> G

G --> H
```

### What Happens

1. Request reaches AuthController.
2. AuthService encodes password.
3. User saved to database.
4. JWT generated.
5. JWT returned to client.

---

## User Login

```mermaid
flowchart TD

A["POST /api/auth/login"]

B["AuthController.java"]

C["AuthService.java"]

D["AuthenticationManager"]

E["DaoAuthenticationProvider"]

F["CustomUserDetailsService"]

G["UserRepository"]

H[(PostgreSQL)]

I["BCryptPasswordEncoder"]

J["JwtService"]

K["JWT Response"]

A --> B
B --> C

C --> D

D --> E

E --> F

F --> G

G --> H

E --> I

C --> J

J --> K
```

### What Happens

1. User submits credentials.
2. AuthenticationManager validates them.
3. User loaded from DB.
4. Password checked using BCrypt.
5. JWT generated.
6. JWT returned.

---

# JWT Protected Request Flow

Example:

```http
PUT /api/events/1
Authorization: Bearer <token>
```

```mermaid
flowchart TD

A["Incoming Request"]

B["SecurityConfig.java"]

C["JwtAuthenticationFilter.java"]

D["JwtService.java"]

E["CustomUserDetailsService.java"]

F["UserRepository.java"]

G[(PostgreSQL)]

H["SecurityContextHolder"]

I["@PreAuthorize"]

J["Controller"]

K["Service"]

L["Repository"]

M[(PostgreSQL)]

A --> B

B --> C

C --> D

C --> E

E --> F

F --> G

C --> H

H --> I

I --> J

J --> K

K --> L

L --> M
```

### What Happens

1. Security filter intercepts request.
2. JWT extracted.
3. JWT validated.
4. User loaded.
5. User stored in SecurityContext.
6. @PreAuthorize checks roles.
7. Controller executes.
8. Service executes.
9. Repository accesses database.

---

# Create Event Flow

Endpoint:

```http
POST /api/events
```

```mermaid
flowchart TD

A["POST /api/events"]

B["EventController.java"]

C["@PreAuthorize ORGANIZER"]

D["EventService.java"]

E["SecurityContextHolder"]

F["UserRepository.java"]

G[(PostgreSQL)]

H["Event.builder()"]

I["EventRepository.java"]

J[(PostgreSQL)]

A --> B

B --> C

C --> D

D --> E

D --> F

F --> G

D --> H

H --> I

I --> J
```

### What Happens

1. Organizer calls API.
2. Role validation occurs.
3. Logged-in organizer extracted.
4. Organizer fetched from DB.
5. Event entity created.
6. Event saved.

---

# Get Event By Id Flow

Endpoint:

```http
GET /api/events/{id}
```

```mermaid
flowchart TD

A["GET /api/events/id"]

B["EventController.java"]

C["EventService.java"]

D["EventRepository.java"]

E[(PostgreSQL)]

F["Event"]

G["EventDetailDto"]

A --> B

B --> C

C --> D

D --> E

E --> F

F --> G
```

### What Happens

1. Event fetched.
2. Entity converted into DTO.
3. DTO returned.

---

# Update Event Flow

Endpoint:

```http
PUT /api/events/{id}
```

```mermaid
flowchart TD

A["PUT /api/events/id"]

B["EventController.java"]

C["@PreAuthorize ORGANIZER"]

D["EventService.java"]

E["Load Event"]

F["Current User"]

G["Ownership Check"]

H["Update Fields"]

I["EventRepository.save"]

J[(PostgreSQL)]

A --> B

B --> C

C --> D

D --> E

D --> F

E --> G

F --> G

G --> H

H --> I

I --> J
```

### What Happens

1. Event loaded.
2. Current user identified.
3. Ownership verified.
4. Event updated.
5. Changes saved.

---

# Delete Event Flow

Endpoint:

```http
DELETE /api/events/{id}
```

```mermaid
flowchart TD

A["DELETE /api/events/id"]

B["EventController.java"]

C["@PreAuthorize ORGANIZER or ADMIN"]

D["EventService.java"]

E["Load Event"]

F["Check Admin"]

G["Check Owner"]

H["Authorized?"]

I["EventRepository.delete"]

J[(PostgreSQL)]

A --> B

B --> C

C --> D

D --> E

E --> F

E --> G

F --> H

G --> H

H --> I

I --> J
```

### What Happens

Deletion allowed if:

- User is ADMIN
- OR User owns the event

---

# Organizer Dashboard Flow

Endpoint:

```http
GET /api/organizer/events
```

```mermaid
flowchart TD

A["GET /api/organizer/events"]

B["OrganizerController.java"]

C["@PreAuthorize ORGANIZER"]

D["EventService.java"]

E["SecurityContextHolder"]

F["UserRepository"]

G[(PostgreSQL)]

H["EventRepository.findByOrganizer"]

I[(PostgreSQL)]

A --> B

B --> C

C --> D

D --> E

D --> F

F --> G

D --> H

H --> I
```

### What Happens

1. Organizer identified.
2. Organizer loaded.
3. Only organizer's events returned.

---

# Public Event Discovery Flow

Endpoint:

```http
GET /api/events
```

Example:

```http
/api/events?page=0&size=10

/api/events?title=music

/api/events?location=kolkata

/api/events?date=2026-12-01
```

```mermaid
flowchart TD

A["GET /api/events"]

B["EventController.java"]

C["EventService.java"]

D["Build Specification"]

E["JpaSpecificationExecutor"]

F["Dynamic SQL"]

G[(PostgreSQL)]

H["Page EventDetailDto"]

A --> B

B --> C

C --> D

D --> E

E --> F

F --> G

G --> H
```

### What Happens

1. Optional filters received.
2. Specification built dynamically.
3. Dynamic query generated.
4. Results paginated.
5. DTOs returned.

---

# Current Layered Architecture

```mermaid
flowchart LR

Client

Controller["Controller Layer"]

Service["Service Layer"]

Repository["Repository Layer"]

Database[(PostgreSQL)]

Client --> Controller

Controller --> Service

Service --> Repository

Repository --> Database
```

---

# Security Architecture Summary

```mermaid
flowchart TD

Client

JWT["JWT Token"]

Filter["JwtAuthenticationFilter"]

JwtService["JwtService"]

UserDetails["CustomUserDetailsService"]

Repo["UserRepository"]

DB[(PostgreSQL)]

Context["SecurityContextHolder"]

Authorize["@PreAuthorize"]

Controller

Service

Repository

Database[(PostgreSQL)]

Client --> JWT

JWT --> Filter

Filter --> JwtService

Filter --> UserDetails

UserDetails --> Repo

Repo --> DB

Filter --> Context

Context --> Authorize

Authorize --> Controller

Controller --> Service

Service --> Repository

Repository --> Database
```

---

# Current Features

- JWT Authentication
- Role-Based Authorization
- Ownership-Based Authorization
- Event Creation
- Event Retrieval
- Event Update
- Event Deletion
- Organizer Dashboard
- Public Event Listing
- Pagination
- Search
- Filtering
- DTO Mapping
- Spring Security
- JPA Specifications