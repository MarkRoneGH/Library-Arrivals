-- ╨б╨╕╤Б╤В╨╡╨╝╨░ ╤Г╤З╨╡╤В╨░ ╨╜╨╛╨▓╤Л╤Е ╨┐╨╛╤Б╤В╤Г╨┐╨╗╨╡╨╜╨╕╨╣ ╨▓ ╨▒╨╕╨▒╨╗╨╕╨╛╤В╨╡╨║╤Г
-- ╨а╨╛╨╗╨╕
CREATE TABLE Roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- ╨Я╨╡╤А╤Б╨╛╨╜╤Л (╤Б╨╛╤В╤А╤Г╨┤╨╜╨╕╨║╨╕ ╨╕ ╤З╨╕╤В╨░╤В╨╡╨╗╨╕)
CREATE TABLE Persons (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    patronymic VARCHAR(50),
    last_name VARCHAR(50) NOT NULL
);

-- ╨Я╨╛╨╗╤М╨╖╨╛╨▓╨░╤В╨╡╨╗╨╕ ╤Б╨╕╤Б╤В╨╡╨╝╤Л (╨╗╨╛╨│╨╕╨╜, ╤А╨╛╨╗╤М, ╨┐╨╡╤А╤Б╨╛╨╜╨░)
CREATE TABLE Users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(60) NOT NULL CHECK (LENGTH(password_hash) = 60),
    role_id INT NOT NULL,
    person_id INT NOT NULL UNIQUE,
    FOREIGN KEY (role_id) REFERENCES Roles(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (person_id) REFERENCES Persons(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ╨Ъ╨░╤В╨░╨╗╨╛╨│ ╨║╨╜╨╕╨│ (╨╛╨┐╨╕╤Б╨░╨╜╨╕╨╡ ╨╕╨╖╨┤╨░╨╜╨╕╤П)
CREATE TABLE Books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    year_published INT
);

-- ╨а╨╡╨│╨╕╤Б╤В╤А╨░╤Ж╨╕╤П ╨┐╨╛╤Б╤В╤Г╨┐╨╗╨╡╨╜╨╕╤П (╨┐╨░╤А╤В╨╕╤П ╨║╨╜╨╕╨│)
CREATE TABLE Arrivals (
    id SERIAL PRIMARY KEY,
    arrival_date DATE NOT NULL DEFAULT CURRENT_DATE,
    notes VARCHAR(500)
);

-- ╨н╨║╨╖╨╡╨╝╨┐╨╗╤П╤А╤Л ╨║╨╜╨╕╨│ (╨╕╨╜╨▓. ╨╜╨╛╨╝╨╡╤А, ╨┐╨╛╨╗╨║╨░, ╤Б╤В╨░╤В╤Г╤Б)
-- status: RECEIVED, IN_FUND, ISSUED, WRITTEN_OFF, RELOCATED
CREATE TABLE BookCopies (
    id SERIAL PRIMARY KEY,
    book_id INT NOT NULL,
    arrival_id INT NOT NULL,
    inventory_number VARCHAR(50) NOT NULL UNIQUE,
    shelf_location VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'RECEIVED',
    FOREIGN KEY (book_id) REFERENCES Books(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (arrival_id) REFERENCES Arrivals(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ╨Т╤Л╨┤╨░╤З╨╕ ╨║╨╜╨╕╨│ ╤З╨╕╤В╨░╤В╨╡╨╗╤П╨╝ (╤Б ╨▓╨╛╨╖╨▓╤А╨░╤В╨╛╨╝ ╨╕ ╨║╨╛╨╜╤В╤А╨╛╨╗╨╡╨╝ ╤Б╤А╨╛╨║╨░)
CREATE TABLE Issues (
    id SERIAL PRIMARY KEY,
    book_copy_id INT NOT NULL,
    reader_id INT NOT NULL,
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date DATE NOT NULL,
    returned_at TIMESTAMP,
    FOREIGN KEY (book_copy_id) REFERENCES BookCopies(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (reader_id) REFERENCES Users(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ╨а╨╛╨╗╨╕
INSERT INTO Roles (name) VALUES
('Admin'),
('Librarian'),
('Reader'),
('Waiting');

-- ╨Я╨╡╤А╤Б╨╛╨╜╤Л
INSERT INTO Persons (first_name, patronymic, last_name) VALUES
('╨Ь╨░╤А╨║', '╨а╤Г╤Б╨╗╨░╨╜╨╛╨▓╨╕╤З', '╨Ь╨╕╤А╨╛╨╜╨╡╨╜╨║╨╛'),
('╨Р╨╗╨╡╨║╤Б╨╡╨╣', '╨Я╨╡╤В╤А╨╛╨▓╨╕╤З', '╨б╨╝╨╕╤А╨╜╨╛╨▓'),
('╨Ф╨╝╨╕╤В╤А╨╕╨╣', '╨б╨╡╤А╨│╨╡╨╡╨▓╨╕╤З', '╨Ъ╨╛╨╖╨╗╨╛╨▓');

-- ╨Я╨╛╨╗╤М╨╖╨╛╨▓╨░╤В╨╡╨╗╨╕
-- ╨е╨╡╤И╨╕ ╤Б╨│╨╡╨╜╨╡╤А╨╕╤А╨╛╨▓╨░╨╜╤Л BCrypt.hashpw(password, BCrypt.gensalt())
-- Admin: markrone / 2007, ╨╛╤Б╤В╨░╨╗╤М╨╜╤Л╨╡: password123
INSERT INTO Users (username, password_hash, role_id, person_id) VALUES
('markrone', '$2a$10$9RdoCUhXWZ0./HuJkn8XiercBT6pny.H6I0YDqkiey7b.kEXQn2QG', 1, (SELECT id FROM Persons WHERE last_name = '╨Ь╨╕╤А╨╛╨╜╨╡╨╜╨║╨╛' LIMIT 1)),
('user1', '$2a$10$zFCLhEJ9Kg9g7Bt6g/APUetlzgYuwODdUG86bNYQbUVuH8Zp84PS2', 2, (SELECT id FROM Persons WHERE last_name = '╨б╨╝╨╕╤А╨╜╨╛╨▓' LIMIT 1)),
('user2', '$2a$10$zFCLhEJ9Kg9g7Bt6g/APUetlzgYuwODdUG86bNYQbUVuH8Zp84PS2', 3, (SELECT id FROM Persons WHERE last_name = '╨Ъ╨╛╨╖╨╗╨╛╨▓' LIMIT 1));

-- ╨Ъ╨╜╨╕╨│╨╕ ╨║╨░╤В╨░╨╗╨╛╨│╨░
INSERT INTO Books (title, author, isbn, year_published) VALUES
('╨Т╨╛╨╣╨╜╨░ ╨╕ ╨╝╨╕╤А', '╨Ы. ╨Э. ╨в╨╛╨╗╤Б╤В╨╛╨╣', '978-5-17-090666-4', 2020),
('╨Ь╨░╤Б╤В╨╡╤А ╨╕ ╨Ь╨░╤А╨│╨░╤А╨╕╤В╨░', '╨Ь. ╨Р. ╨С╤Г╨╗╨│╨░╨║╨╛╨▓', '978-5-17-090667-1', 2019),
('╨Я╤А╨╡╤Б╤В╤Г╨┐╨╗╨╡╨╜╨╕╨╡ ╨╕ ╨╜╨░╨║╨░╨╖╨░╨╜╨╕╨╡', '╨д. ╨Ь. ╨Ф╨╛╤Б╤В╨╛╨╡╨▓╤Б╨║╨╕╨╣', '978-5-17-090668-8', 2021);

-- ╨Я╨╛╤Б╤В╤Г╨┐╨╗╨╡╨╜╨╕╤П
INSERT INTO Arrivals (arrival_date, notes) VALUES
(CURRENT_DATE, '╨Я╨░╤А╤В╨╕╤П ╤Е╤Г╨┤╨╛╨╢╨╡╤Б╤В╨▓╨╡╨╜╨╜╨╛╨╣ ╨╗╨╕╤В╨╡╤А╨░╤В╤Г╤А╤Л'),
(CURRENT_DATE, '╨Ф╨╛╨┐╨╛╨╗╨╜╨╕╤В╨╡╨╗╤М╨╜╤Л╨╡ ╤Н╨║╨╖╨╡╨╝╨┐╨╗╤П╤А╤Л');

-- ╨н╨║╨╖╨╡╨╝╨┐╨╗╤П╤А╤Л (╨┐╤А╨╕╨▓╤П╨╖╨░╨╜╤Л ╨║ ╨║╨╜╨╕╨│╨░╨╝ ╨╕ ╨┐╨╛╤Б╤В╤Г╨┐╨╗╨╡╨╜╨╕╤П╨╝)
INSERT INTO BookCopies (book_id, arrival_id, inventory_number, shelf_location, status) VALUES
((SELECT id FROM Books WHERE title = '╨Т╨╛╨╣╨╜╨░ ╨╕ ╨╝╨╕╤А' LIMIT 1), (SELECT id FROM Arrivals LIMIT 1), 'INV-001', '╨б╤В╨╡╨╗╨╗╨░╨╢ ╨Р1', 'IN_FUND'),
((SELECT id FROM Books WHERE title = '╨Т╨╛╨╣╨╜╨░ ╨╕ ╨╝╨╕╤А' LIMIT 1), (SELECT id FROM Arrivals LIMIT 1), 'INV-002', '╨б╤В╨╡╨╗╨╗╨░╨╢ ╨Р1', 'IN_FUND'),
((SELECT id FROM Books WHERE title = '╨Ь╨░╤Б╤В╨╡╤А ╨╕ ╨Ь╨░╤А╨│╨░╤А╨╕╤В╨░' LIMIT 1), (SELECT id FROM Arrivals LIMIT 1), 'INV-003', '╨б╤В╨╡╨╗╨╗╨░╨╢ ╨С2', 'IN_FUND'),
((SELECT id FROM Books WHERE title = '╨Я╤А╨╡╤Б╤В╤Г╨┐╨╗╨╡╨╜╨╕╨╡ ╨╕ ╨╜╨░╨║╨░╨╖╨░╨╜╨╕╨╡' LIMIT 1), (SELECT id FROM Arrivals OFFSET 1 LIMIT 1), 'INV-004', '╨б╤В╨╡╨╗╨╗╨░╨╢ ╨Р1', 'RECEIVED');
