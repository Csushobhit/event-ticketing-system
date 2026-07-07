# User & Admin APIs

---

# GET /api/users/me/tickets

## Complete Execution Path

```text
Client / Browser
 |
 v
GET /api/users/me/tickets
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
UserController.getMyTickets()
 |
 v
UserService.getMyTickets()
 |
 v
SecurityContextHolder.getContext()
 |
 v
Authentication.getName()
 |
 v
UserRepository.findByEmail(email)
 |
 v
PostgreSQL
 |
 v
User Entity
 |
 v
TicketRepository.findByOrder_User(user)
 |
 v
PostgreSQL
 |
 v
List<Ticket>
 |
 v
mapToTicketDto(ticket)
 |
 v
EventSummaryDto.builder()
 |
 v
TicketDto.builder()
 |
 v
List<TicketDto>
 |
 v
HTTP 200 Response
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["GET /api/users/me/tickets"]

C["JwtAuthenticationFilter"]

D["UserController.getMyTickets()"]

E["UserService.getMyTickets()"]

F["UserRepository.findByEmail()"]

G["PostgreSQL"]

H["TicketRepository.findByOrder_User()"]

I["PostgreSQL"]

J["List<Ticket>"]

K["mapToTicketDto()"]

L["List<TicketDto>"]

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

# GET /api/admin/users

## Complete Execution Path

```text
Client / Browser
 |
 v
GET /api/admin/users
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
@PreAuthorize("hasRole('ADMIN')")
 |
 v
AdminController.getAllUsers()
 |
 v
AdminService.getAllUsers()
 |
 v
UserRepository.findAll()
 |
 v
PostgreSQL
 |
 v
List<User>
 |
 v
mapToUserDto(user)
 |
 v
List<UserDto>
 |
 v
HTTP 200 Response
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["GET /api/admin/users"]

C["JwtAuthenticationFilter"]

D["@PreAuthorize ADMIN"]

E["AdminController.getAllUsers()"]

F["AdminService.getAllUsers()"]

G["UserRepository.findAll()"]

H["PostgreSQL"]

I["List<User>"]

J["mapToUserDto()"]

K["List<UserDto>"]

L["HTTP Response"]

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
```

---

# GET /api/admin/events

## Complete Execution Path

```text
Client / Browser
 |
 v
GET /api/admin/events
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
@PreAuthorize("hasRole('ADMIN')")
 |
 v
AdminController.getAllEvents()
 |
 v
AdminService.getAllEvents()
 |
 v
EventRepository.findAll()
 |
 v
PostgreSQL
 |
 v
List<Event>
 |
 v
mapToAdminEventViewDto(event)
 |
 v
List<AdminEventViewDto>
 |
 v
HTTP 200 Response
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["GET /api/admin/events"]

C["JwtAuthenticationFilter"]

D["@PreAuthorize ADMIN"]

E["AdminController.getAllEvents()"]

F["AdminService.getAllEvents()"]

G["EventRepository.findAll()"]

H["PostgreSQL"]

I["List<Event>"]

J["mapToAdminEventViewDto()"]

K["List<AdminEventViewDto>"]

L["HTTP Response"]

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
```

---

# PUT /api/admin/users/{userId}/role

## Complete Execution Path

```text
Client / Browser
 |
 v
PUT /api/admin/users/{userId}/role
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
@PreAuthorize("hasRole('ADMIN')")
 |
 v
AdminController.updateUserRole(
    userId,
    UpdateUserRoleDto
 )
 |
 v
AdminService.updateUserRole(
    userId,
    newRole
 )
 |
 v
UserRepository.findById(userId)
 |
 v
PostgreSQL
 |
 v
Target User Loaded
 |
 v
SecurityContextHolder.getAuthentication()
 |
 v
Current Admin Email
 |
 v
Compare Target User Email
 |
 +-----------------------------+
 | Self Role Change Attempt ?  |
 +-----------------------------+
 |
 |---- YES
 |        |
 |        v
 |  IllegalStateException
 |
 |---- NO
 |
 v
user.setRoles(Set.of(newRole))
 |
 v
UserRepository.save(user)
 |
 v
PostgreSQL
 |
 v
mapToUserDto()
 |
 v
UserDto
 |
 v
HTTP 200 Response
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["PUT User Role"]

C["JwtAuthenticationFilter"]

D["@PreAuthorize ADMIN"]

E["AdminController.updateUserRole()"]

F["AdminService.updateUserRole()"]

G["UserRepository.findById()"]

H["PostgreSQL"]

I["Check Self Update"]

J{"Self Update?"}

K["IllegalStateException"]

L["Set New Role"]

M["UserRepository.save()"]

N["PostgreSQL"]

O["UserDto"]

P["HTTP Response"]

A --> B
B --> C
C --> D
D --> E
E --> F

F --> G
G --> H

H --> I

I --> J

J -->|Yes| K

J -->|No| L

L --> M
M --> N

N --> O
O --> P
```

---

# GET /api/admin/analytics

## Complete Execution Path

```text
Client / Browser
 |
 v
GET /api/admin/analytics
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
@PreAuthorize("hasRole('ADMIN')")
 |
 v
AdminController.getPlatformAnalytics()
 |
 v
AdminService.getPlatformAnalytics()
 |
 v
EventRepository.calculateTotalRevenue()
 |
 v
PostgreSQL
 |
 v
Revenue Result
 |
 v
EventRepository.calculateTotalTicketsSold()
 |
 v
PostgreSQL
 |
 v
Tickets Sold Result
 |
 v
UserRepository.count()
 |
 v
PostgreSQL
 |
 v
Total Users
 |
 v
EventRepository.count()
 |
 v
PostgreSQL
 |
 v
Total Events
 |
 v
PlatformAnalyticsDto.builder()
 |
 v
PlatformAnalyticsDto
 |
 v
HTTP 200 Response
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["GET /api/admin/analytics"]

C["JwtAuthenticationFilter"]

D["@PreAuthorize ADMIN"]

E["AdminController.getPlatformAnalytics()"]

F["AdminService.getPlatformAnalytics()"]

G["EventRepository.calculateTotalRevenue()"]

H["EventRepository.calculateTotalTicketsSold()"]

I["UserRepository.count()"]

J["EventRepository.count()"]

K["PostgreSQL"]

L["PlatformAnalyticsDto"]

M["HTTP Response"]

A --> B
B --> C
C --> D
D --> E
E --> F

F --> G
F --> H
F --> I
F --> J

G --> K
H --> K
I --> K
J --> K

K --> L

L --> M
```