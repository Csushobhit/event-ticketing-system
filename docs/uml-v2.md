classDiagram

class User {
    Long id
    String name
    String email
    String password
    Set<Role> roles
    Set<Event> events
    List<Order> orders

    Collection getAuthorities()
    String getUsername()
}

class Event {
    Long id
    String title
    String description
    LocalDateTime date
    String location
    BigDecimal ticketPrice
    Integer totalTicketsAvailable
    Integer ticketsSold
    User organizer
}

class Order {
    Long id
    LocalDateTime orderDate
    BigDecimal totalAmount
    String paymentIntentId
    String status

    User user
    Set<Ticket> tickets
}

class Ticket {
    Long id
    UUID uniqueCode

    Event event
    Order order

    prePersist()
}

class Role {
    <<enumeration>>
    ROLE_USER
    ROLE_ORGANIZER
    ROLE_ADMIN
}

User "1" --> "*" Event : organizes
User "1" --> "*" Order : places
Order "1" --> "*" Ticket : contains
Event "1" --> "*" Ticket : issues
User "1" --> "*" Role : has