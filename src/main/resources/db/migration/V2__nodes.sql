CREATE TABLE nodes (
    sequence BIGINT AUTO_INCREMENT PRIMARY KEY,
    id BINARY(16) UNIQUE,
    user_id BINARY(16) NOT NULL REFERENCES users(id),
    workspace_id BINARY(16) REFERENCES nodes(id),
    parent_id BINARY(16) REFERENCES nodes(id),
    type INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512),
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,
    content_type VARCHAR(128) NOT NULL,
    size BIGINT NOT NULL
);