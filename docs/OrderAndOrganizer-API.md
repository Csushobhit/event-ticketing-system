# Order & Organizer APIs

---

# POST /api/orders/checkout

## Complete Execution Path

```text
Client / Browser
 |
 v
POST /api/orders/checkout
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
@PreAuthorize("hasRole('USER')")
 |
 v
OrderController.checkout(
    CheckoutRequest request
 )
 |
 v
OrderService.createPaymentIntent(
    CheckoutRequest request
 )
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
EventRepository.findAndLockById(
    request.getEventId()
 )
 |
 v
PostgreSQL
 |
 v
PESSIMISTIC_WRITE LOCK ACQUIRED
 |
 v
Calculate Tickets Available
 |
 v
Validate Requested Quantity
 |
 +-----------------------------+
 | Enough Tickets Available ?  |
 +-----------------------------+
 |
 |---- NO
 |        |
 |        v
 |  InsufficientTicketsException
 |
 |---- YES
 |
 v
Calculate Total Amount
 |
 v
Convert Amount To Cents
 |
 v
PaymentIntentCreateParams.builder()
 |
 v
Add Metadata
    userId
    eventId
    ticketQuantity
 |
 v
PaymentIntent.create(params)
 |
 v
Stripe API
 |
 v
PaymentIntent Returned
 |
 v
paymentIntent.getClientSecret()
 |
 v
CheckoutResponse(clientSecret)
 |
 v
HTTP 200 Response
 |
 v
Client Receives Stripe Client Secret
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["POST /api/orders/checkout"]

C["JwtAuthenticationFilter"]

D["@PreAuthorize USER"]

E["OrderController.checkout()"]

F["OrderService.createPaymentIntent()"]

G["UserRepository.findByEmail()"]

H["PostgreSQL"]

I["EventRepository.findAndLockById()"]

J["PostgreSQL"]

K["Pessimistic Lock"]

L["Validate Tickets"]

M{"Enough Tickets?"}

N["InsufficientTicketsException"]

O["Calculate Amount"]

P["PaymentIntentCreateParams"]

Q["PaymentIntent.create()"]

R["Stripe"]

S["Client Secret"]

T["CheckoutResponse"]

U["HTTP Response"]

A --> B
B --> C
C --> D
D --> E
E --> F

F --> G
G --> H

F --> I
I --> J
J --> K

K --> L

L --> M

M -->|No| N

M -->|Yes| O

O --> P
P --> Q
Q --> R

R --> S
S --> T
T --> U
```

---

# GET /api/organizer/events

## Complete Execution Path

```text
Client / Browser
 |
 v
GET /api/organizer/events
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
OrganizerController.getOrganizerEvents()
 |
 v
EventService.getEventsByOrganizer()
 |
 v
SecurityContextHolder.getAuthentication()
 |
 v
Authentication.getName()
 |
 v
UserRepository.findByEmail()
 |
 v
PostgreSQL
 |
 v
Organizer User Entity
 |
 v
EventRepository.findByOrganizer(
    organizer
 )
 |
 v
PostgreSQL
 |
 v
List<Event>
 |
 v
HTTP 200 Response
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["GET /api/organizer/events"]

C["JwtAuthenticationFilter"]

D["@PreAuthorize ORGANIZER"]

E["OrganizerController.getOrganizerEvents()"]

F["EventService.getEventsByOrganizer()"]

G["UserRepository.findByEmail()"]

H["PostgreSQL"]

I["EventRepository.findByOrganizer()"]

J["PostgreSQL"]

K["List<Event>"]

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

# GET /api/organizer/dashboard/{eventId}

## Complete Execution Path

```text
Client / Browser
 |
 v
GET /api/organizer/dashboard/{eventId}
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
OrganizerController.getDashboardData(eventId)
 |
 v
OrganizerService.getDashboardData(eventId)
 |
 v
SecurityContextHolder.getAuthentication()
 |
 v
Authentication.getName()
 |
 v
UserRepository.findByEmail()
 |
 v
PostgreSQL
 |
 v
Organizer Entity
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
Validate Event Ownership
 |
 +---------------------+
 | Organizer Owns Event|
 +---------------------+
 |
 |---- NO
 |        |
 |        v
 |  AccessDeniedException
 |
 |---- YES
 |
 v
Calculate Revenue
 |
 v
Build OrganizerDashboardDto
 |
 v
HTTP 200 Response
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["GET Dashboard"]

C["JwtAuthenticationFilter"]

D["@PreAuthorize ORGANIZER"]

E["OrganizerController.getDashboardData()"]

F["OrganizerService.getDashboardData()"]

G["UserRepository.findByEmail()"]

H["PostgreSQL"]

I["EventRepository.findById()"]

J["PostgreSQL"]

K["Ownership Validation"]

L{"Owner?"}

M["AccessDeniedException"]

N["Calculate Revenue"]

O["OrganizerDashboardDto"]

P["HTTP Response"]

A --> B
B --> C
C --> D
D --> E
E --> F

F --> G
G --> H

F --> I
I --> J

J --> K

K --> L

L -->|No| M

L -->|Yes| N

N --> O
O --> P
```

---

# GET /api/organizer/events/{eventId}/attendees

## Complete Execution Path

```text
Client / Browser
 |
 v
GET /api/organizer/events/{eventId}/attendees
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
OrganizerController.getEventAttendees(eventId)
 |
 v
OrganizerService.getEventAttendees(eventId)
 |
 v
SecurityContextHolder.getAuthentication()
 |
 v
Authentication.getName()
 |
 v
UserRepository.findByEmail()
 |
 v
PostgreSQL
 |
 v
Organizer Entity
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
Validate Ownership
 |
 +----------------------+
 | Organizer Owns Event |
 +----------------------+
 |
 |---- NO
 |        |
 |        v
 |  AccessDeniedException
 |
 |---- YES
 |
 v
TicketRepository.findByEvent_Id(eventId)
 |
 v
PostgreSQL
 |
 v
List<Ticket>
 |
 v
mapToAttendeeDto(ticket)
 |
 v
AttendeeDto List
 |
 v
HTTP 200 Response
```

---

## Mermaid Flowchart

```mermaid
flowchart TD

A["Client"]

B["GET Attendees"]

C["JwtAuthenticationFilter"]

D["@PreAuthorize ORGANIZER"]

E["OrganizerController.getEventAttendees()"]

F["OrganizerService.getEventAttendees()"]

G["UserRepository.findByEmail()"]

H["PostgreSQL"]

I["EventRepository.findById()"]

J["PostgreSQL"]

K["Ownership Validation"]

L{"Owner?"}

M["AccessDeniedException"]

N["TicketRepository.findByEvent_Id()"]

O["PostgreSQL"]

P["List<Ticket>"]

Q["mapToAttendeeDto()"]

R["List<AttendeeDto>"]

S["HTTP Response"]

A --> B
B --> C
C --> D
D --> E
E --> F

F --> G
G --> H

F --> I
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
```