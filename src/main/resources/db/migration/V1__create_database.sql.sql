CREATE TABLE users
(
    id         BINARY(16) PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    file_path  BINARY(16),
    layout     TINYINT(1)            default 0 not null,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- table refresh tokens
CREATE TABLE refresh_token
(
    id            BINARY(16) PRIMARY KEY,
    user_id       BINARY(16)          NOT NULL,
    refresh_token BINARY(16)  NOT NULL,
    expired_at    DATETIME     NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- table permissions
CREATE TABLE permissions
(
    id   BINARY(16) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- table users_permissions
CREATE TABLE users_permissions
(
    user_id       BINARY(16) NOT NULL,
    permission_id BINARY(16) NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

-- table shares
CREATE TABLE shares
(
    id         BINARY(16) PRIMARY KEY,
    user_id    BINARY(16)         NOT NULL,
    storage_id BINARY(16) NOT NULL,
    type       VARCHAR(10) NOT NULL,
    password   VARCHAR(255),
    created_at DATETIME    NOT NULL,
    expired_at DATETIME    NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- tables storages
CREATE TABLE folders
(
    id         BINARY(16) PRIMARY KEY,
    path       VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    parent_id  BINARY(16),
    updated_at DATETIME                           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME                           NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (parent_id) REFERENCES folders (id) ON DELETE CASCADE
);

CREATE TABLE files
(
    id         BINARY(16) PRIMARY KEY,
    parent_id  BINARY(16),
    name       VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    size       LONG                               NOT NULL,
    icon       VARCHAR(255)                       NOT NULL,
    type       VARCHAR(255)                       NOT NULL,
    updated_at DATETIME                           NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (parent_id) REFERENCES folders (id) ON DELETE CASCADE
)