CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    file_path  UUID,
    layout     BOOLEAN               DEFAULT false NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- table refresh tokens
CREATE TABLE refresh_token
(
    id            UUID PRIMARY KEY,
    user_id       UUID      NOT NULL,
    refresh_token UUID      NOT NULL,
    expired_at    TIMESTAMP NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- table permissions
CREATE TABLE permissions
(
    id   UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- table users_permissions
CREATE TABLE users_permissions
(
    user_id       UUID NOT NULL,
    permission_id UUID NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

-- table shares
CREATE TABLE shares
(
    id         UUID PRIMARY KEY,
    user_id    UUID        NOT NULL,
    storage_id UUID        NOT NULL,
    type       VARCHAR(10) NOT NULL,
    password   VARCHAR(255),
    created_at TIMESTAMP   NOT NULL,
    expired_at TIMESTAMP   NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- tables storages
CREATE TABLE folders
(
    id         UUID PRIMARY KEY,
    path       VARCHAR(255) NOT NULL,
    parent_id  UUID,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (parent_id) REFERENCES folders (id) ON DELETE CASCADE
);

CREATE TABLE files
(
    id         UUID PRIMARY KEY,
    parent_id  UUID,
    name       VARCHAR(255) NOT NULL,
    size       BIGINT       NOT NULL,
    icon       VARCHAR(255) NOT NULL,
    type       VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (parent_id) REFERENCES folders (id) ON DELETE CASCADE
)