-- table users
CREATE TABLE users
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
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
    id            INT PRIMARY KEY AUTO_INCREMENT,
    user_id       INT          NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    expired_at    DATETIME     NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- table permissions
CREATE TABLE permissions
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

INSERT INTO permissions (name)
VALUES ('Administration');
INSERT INTO permissions (name)
VALUES ('Create file or folder');
INSERT INTO permissions (name)
VALUES ('Delete file or folder');
INSERT INTO permissions (name)
VALUES ('Download');
INSERT INTO permissions (name)
VALUES ('Edit file');
INSERT INTO permissions (name)
VALUES ('Share files');
INSERT INTO permissions (name)
VALUES ('Rename file or folder');

-- table users_permissions
CREATE TABLE users_permissions
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    user_id       INT NOT NULL,
    permission_id INT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (permission_id) REFERENCES permissions (id)
);

-- table shares
CREATE TABLE shares
(
    id         BINARY(16) PRIMARY KEY,
    user_id    INT          NOT NULL,
    storage_id BINARY(16) NOT NULL,
    type      VARCHAR(10) NOT NULL,
    password   VARCHAR(255),
    created_at DATETIME     NOT NULL,
    expired_at DATETIME     NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- table settings
CREATE TABLE settings
(
    id    INT PRIMARY KEY AUTO_INCREMENT,
    `key` VARCHAR(255) NOT NULL,
    value TEXT         NOT NULL
);

INSERT INTO settings (`key`, value)
VALUES ('view_register', 'false');
INSERT INTO settings (`key`, value)
VALUES ('user_default_path', 'true');

-- table logs
CREATE TABLE logs
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    user_id    INT          NOT NULL,
    action     VARCHAR(255) NOT NULL,
    subject    VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id)
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
    size       LONG                       NOT NULL,
    icon       VARCHAR(255)                       NOT NULL,
    type       VARCHAR(255)                       NOT NULL,
    updated_at DATETIME                           NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (parent_id) REFERENCES folders (id) ON DELETE CASCADE
)