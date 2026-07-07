# Event APIs

---

# POST /api/events

## Complete Execution Path

```text
Client / Browser
 |
 v
POST /api/events
 |
 v
SecurityConfig.securityFilterChain()
 |
 v
JwtAuthenticationFilter.doFilterInternal()
 |
 v
JwtService.extractUsername(jwt)
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
JwtService.isTokenValid()
 |
 v
SecurityContextHolder.setAuthentication()
 |
 v
@PreAuthorize("hasRole('ORGANIZER')")
 |
 v
EventController.createEvent(CreateEventRequest request)
 |
 v
EventService.createEvent(CreateEventRequest request)
 |
 v
SecurityContextHolder.getContext()
 |
 v
Authentication.getPrincipal()
 |
 v
UserDetails.getUsername()
 |
 v
UserRepository.findByEmail(email)
 |
 v
PostgreSQL
 |
 v
Event.builder()
 |
 v
EventRepository.save(event)
 |
 v
PostgreSQL
 |
 v
Created Event Returned
 |
 v
ServletUriComponentsBuilder.buildAndExpand()
 |
 v
HTTP 201 Created
 |
 v
Response Returned
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["POST /api/events"]

C["SecurityConfig.securityFilterChain()"]

D["JwtAuthenticationFilter.doFilterInternal()"]

E["JwtService.extractUsername()"]

F["CustomUserDetailsService.loadUserByUsername()"]

G["UserRepository.findByEmail()"]

H["PostgreSQL"]

I["JwtService.isTokenValid()"]

J["SecurityContextHolder"]

K["@PreAuthorize ORGANIZER"]

L["EventController.createEvent()"]

M["EventService.createEvent()"]

N["UserRepository.findByEmail()"]

O["PostgreSQL"]

P["Event.builder()"]

Q["EventRepository.save()"]

R["PostgreSQL"]

S["HTTP 201 Response"]

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
P --> Q
Q --> R
R --> S
```

---

# GET /api/events

## Complete Execution Path

```text
Client / Browser
 |
 v
GET /api/events
 |
 v
SecurityConfig
 |
 v
Endpoint Public
 |
 v
EventController.getAllEvents(
    Pageable,
    title,
    location,
    date
)
 |
 v
EventService.getAllEvents(
    pageable,
    title,
    location,
    date
)
 |
 v
Specification<Event> spec
 |
 +-----------------------------+
 | Title Filter                |
 +-----------------------------+
 |
 +-----------------------------+
 | Location Filter             |
 +-----------------------------+
 |
 +-----------------------------+
 | Date Filter                 |
 +-----------------------------+
 |
 v
EventRepository.findAll(
    spec,
    pageable
)
 |
 v
JpaSpecificationExecutor
 |
 v
Dynamic SQL Generated
 |
 v
PostgreSQL
 |
 v
Page<Event>
 |
 v
EventService.mapToEventDetailDto()
 |
 v
Page<EventDetailDto>
 |
 v
HTTP 200 Response
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["GET /api/events"]

C["EventController.getAllEvents()"]

D["EventService.getAllEvents()"]

E["Build Specification"]

F["EventRepository.findAll(spec,pageable)"]

G["JpaSpecificationExecutor"]

H["Dynamic Query"]

I["PostgreSQL"]

J["Page<Event>"]

K["mapToEventDetailDto()"]

L["Page<EventDetailDto>"]

M["HTTP Response"]

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
```

---

# GET /api/events/{id}

## Complete Execution Path

```text
Client / Browser
 |
 v
GET /api/events/{id}
 |
 v
SecurityConfig
 |
 v
Endpoint Public
 |
 v
EventController.getEventById(Long id)
 |
 v
EventService.getEventById(Long eventId)
 |
 v
EventService.getEventEntityById(eventId)
 |
 v
EventRepository.findById(eventId)
 |
 v
PostgreSQL
 |
 v
Event Entity
 |
 v
EventService.mapToEventDetailDto()
 |
 v
EventDetailDto
 |
 v
HTTP 200 Response
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["GET /api/events/{id}"]

C["EventController.getEventById()"]

D["EventService.getEventById()"]

E["EventService.getEventEntityById()"]

F["EventRepository.findById()"]

G["PostgreSQL"]

H["Event"]

I["mapToEventDetailDto()"]

J["EventDetailDto"]

K["HTTP Response"]

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
```

---

# PUT /api/events/{id}

## Complete Execution Path

```text
Client / Browser
 |
 v
PUT /api/events/{id}
 |
 v
SecurityConfig.securityFilterChain()
 |
 v
JwtAuthenticationFilter.doFilterInternal()
 |
 v
JwtService.extractUsername()
 |
 v
CustomUserDetailsService.loadUserByUsername()
 |
 v
UserRepository.findByEmail()
 |
 v
PostgreSQL
 |
 v
SecurityContextHolder.setAuthentication()
 |
 v
@PreAuthorize("hasRole('ORGANIZER')")
 |
 v
EventController.updateEvent()
 |
 v
EventService.updateEvent()
 |
 v
EventService.getEventEntityById()
 |
 v
EventRepository.findById()
 |
 v
PostgreSQL
 |
 v
Load Existing Event
 |
 v
SecurityContextHolder.getAuthentication()
 |
 v
Current User Email
 |
 v
Existing Event Organizer Email
 |
 v
Ownership Validation
 |
 +--------------------+
 | Owner ?            |
 +--------------------+
 |
 |---- NO
 |        |
 |        v
 |  AccessDeniedException
 |
 |---- YES
 |
 v
Update Fields
 |
 v
EventRepository.save()
 |
 v
PostgreSQL
 |
 v
Updated Event Returned
 |
 v
HTTP 200 Response
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["PUT /api/events/{id}"]

C["JwtAuthenticationFilter"]

D["@PreAuthorize ORGANIZER"]

E["EventController.updateEvent()"]

F["EventService.updateEvent()"]

G["EventRepository.findById()"]

H["PostgreSQL"]

I["Ownership Check"]

J{"Owner?"}

K["AccessDeniedException"]

L["Update Fields"]

M["EventRepository.save()"]

N["PostgreSQL"]

O["HTTP Response"]

A --> B
B --> C
C --> D
D --> E
E --> F
F --> G
G --> H
H --> I
I --> J

J -->|No| K

J -->|Yes| L
L --> M
M --> N
N --> O
```

---

# DELETE /api/events/{id}

## Complete Execution Path

```text
Client / Browser
 |
 v
DELETE /api/events/{id}
 |
 v
SecurityConfig.securityFilterChain()
 |
 v
JwtAuthenticationFilter.doFilterInternal()
 |
 v
JwtService.extractUsername()
 |
 v
CustomUserDetailsService.loadUserByUsername()
 |
 v
UserRepository.findByEmail()
 |
 v
PostgreSQL
 |
 v
SecurityContextHolder.setAuthentication()
 |
 v
@PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
 |
 v
EventController.deleteEvent(Long id)
 |
 v
EventService.deleteEvent(Long eventId)
 |
 v
EventService.getEventEntityById()
 |
 v
EventRepository.findById()
 |
 v
PostgreSQL
 |
 v
Authentication authentication =
 SecurityContextHolder.getContext()
                      .getAuthentication()
 |
 v
Check ROLE_ADMIN
 |
 v
Check Event Owner
 |
 v
Authorization Decision
 |
 +-------------------------+
 | Admin OR Owner ?        |
 +-------------------------+
 |
 |---- NO
 |        |
 |        v
 |  AccessDeniedException
 |
 |---- YES
 |
 v
EventRepository.delete(event)
 |
 v
PostgreSQL
 |
 v
HTTP 204 No Content
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["DELETE /api/events/{id}"]

C["JwtAuthenticationFilter"]

D["@PreAuthorize ORGANIZER or ADMIN"]

E["EventController.deleteEvent()"]

F["EventService.deleteEvent()"]

G["EventRepository.findById()"]

H["PostgreSQL"]

I["Check Admin"]

J["Check Owner"]

K{"Authorized?"}

L["AccessDeniedException"]

M["EventRepository.delete()"]

N["PostgreSQL"]

O["HTTP 204"]

A --> B
B --> C
C --> D
D --> E
E --> F
F --> G
G --> H

H --> I
H --> J

I --> K
J --> K

K -->|No| L

K -->|Yes| M
M --> N
N --> O
```

---

# GET /api/events/test

## Complete Execution Path

```text
Client / Browser
 |
 v
GET /api/events/test
 |
 v
SecurityConfig
 |
 v
Public Endpoint
 |
 v
EventController.test()
 |
 v
return "working"
 |
 v
HTTP 200 Response
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["GET /api/events/test"]

C["EventController.test()"]

D["return working"]

E["HTTP Response"]

A --> B
B --> C
C --> D
D --> E
```