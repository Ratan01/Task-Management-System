CREATE TABLE app_users (
                           id BIGINT PRIMARY KEY,
                           username VARCHAR(100) NOT NULL UNIQUE,
                           email VARCHAR(255),
                           role VARCHAR(20) NOT NULL
);