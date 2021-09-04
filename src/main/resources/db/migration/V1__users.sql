CREATE TABLE users
(
    sequence INT AUTO_INCREMENT PRIMARY KEY,
    id BINARY(16) UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    hash VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,
    enabled BOOLEAN NOT NULL,
    roles VARCHAR(1024) NOT NULL
);


--INSERT INTO users (id, email, hash, created, modified, enabled, roles)
--VALUES (UUID_TO_BIN('aad41419-a0e8-4f6d-872d-483c74ba1859'), 'admin@local', '$2a$10$MBzotBuEldeMHvI1937g/.sIT1e4B5BZ5r4.VJvdP32g.dGIKJOmy',
--        '2020-04-18 11:28:36', '2020-04-18 11:28:36', true, 'ADMINISTRATOR,USER');

