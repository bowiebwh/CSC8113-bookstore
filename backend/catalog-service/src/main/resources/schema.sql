-- src/main/resources/schema.sql
CREATE TABLE IF NOT EXISTS book (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) UNIQUE,
    author VARCHAR(255),
    price DECIMAL,
    image_url VARCHAR(500),
    stock INT
);
