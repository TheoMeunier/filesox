CREATE TABLE audit_logs
(
    id          BINARY(16) PRIMARY KEY,
    action      VARCHAR(50)  NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id   VARCHAR(100),
    entity_name VARCHAR(500),
    details     TEXT,
    old_values  TEXT,
    new_values  TEXT,
    user_id     VARCHAR(100),
    ip_address  VARCHAR(45),
    timestamp   DATETIME
)

