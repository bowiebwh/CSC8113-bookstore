-- src/main/resources/data.sq
INSERT INTO book (title, author, price, image_url, stock)
VALUES
('Effective Java', 'Joshua Bloch', 45.99, '/images/effective java.jpg', 1000),
('Clean Code', 'Robert C. Martin', 39.99, '/images/clean code.jpg', 1500),
('The Pragmatic Programmer', 'Andrew Hunt', 42.50, '/images/pragmatic programmer.jpg', 1200),
('Design Patterns', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides', 54.99, '/images/design patterns.jpg', 600),
('Eloquent JavaScript', 'Marijn Haverbeke', 31.99, '/images/eloquent javascript.jpg', 1100),
('Domain-Driven Design', 'Eric Evans', 59.99, '/images/domain driven design.jpg', 500)
ON CONFLICT (title) DO NOTHING;


-- INSERT INTO book (title, author, price, image_url, stock)
-- VALUES
--     ('Effective Java', 'Joshua Bloch', 45.99, 'https://s3.amazonaws.com/my-bucket/effective-java.jpg', 10),
--     ('Clean Code', 'Robert C. Martin', 39.99, 'https://s3.amazonaws.com/my-bucket/clean-code.jpg', 15),
--     ('The Pragmatic Programmer', 'Andrew Hunt', 42.50, 'https://s3.amazonaws.com/my-bucket/pragmatic-programmer.jpg', 12)
--     ON CONFLICT (title) DO NOTHING;