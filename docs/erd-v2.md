erDiagram

    USERS {
        BIGINT id PK
        VARCHAR name
        VARCHAR email UK
        VARCHAR password
    }

    USER_ROLES {
        BIGINT user_id FK
        VARCHAR role
    }

    EVENTS {
        BIGINT id PK
        VARCHAR title
        TEXT description
        TIMESTAMP date
        VARCHAR location
        DECIMAL ticket_price
        INTEGER total_tickets_available
        INTEGER tickets_sold
        BIGINT organizer_id FK
    }

    ORDERS {
        BIGINT id PK
        TIMESTAMP order_date
        DECIMAL total_amount
        VARCHAR payment_intent_id UK
        VARCHAR status
        BIGINT user_id FK
    }

    TICKETS {
        BIGINT id PK
        UUID unique_code UK
        BIGINT event_id FK
        BIGINT order_id FK
    }

    USERS ||--o{ EVENTS : organizes
    USERS ||--o{ ORDERS : places
    ORDERS ||--o{ TICKETS : contains
    EVENTS ||--o{ TICKETS : issues
    USERS ||--o{ USER_ROLES : has