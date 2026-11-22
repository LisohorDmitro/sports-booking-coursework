DROP DATABASE IF EXISTS sports_booking;
CREATE DATABASE sports_booking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sports_booking;

CREATE TABLE facility (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(160) NOT NULL,
    address VARCHAR(200) NOT NULL
);

CREATE TABLE app_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(160) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL
);

CREATE TABLE time_slot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    facility_id BIGINT NOT NULL,
    slot_date VARCHAR(10) NOT NULL,
    start_time VARCHAR(5) NOT NULL,
    end_time VARCHAR(5) NOT NULL,
    price DOUBLE NOT NULL,
    FOREIGN KEY (facility_id) REFERENCES facility(id)
);

CREATE TABLE booking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    time_slot_id BIGINT NOT NULL,
    state VARCHAR(30) NOT NULL,
    f1 DOUBLE NOT NULL,
    f2 DOUBLE NOT NULL,
    f3 DOUBLE NOT NULL,
    integral_index DOUBLE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(id),
    FOREIGN KEY (time_slot_id) REFERENCES time_slot(id)
);

INSERT INTO facility (code, name, address) VALUES
('ARENA-01', 'Спортивна Арена "Центральна"', 'м. Київ, вул. Хрещатик, 15'),
('POOL-02', 'Басейн "Олімпійський"', 'м. Львів, вул. Стрийська, 199'),
('GYM-03', 'Тренажерний зал "Форс"', 'м. Полтава, вул. Соборності, 45');

INSERT INTO app_user (full_name, email, phone) VALUES
('Платон Коваленко', 'platon@gmail.com', '+380501112233'),
('Євдокія Шевченко', 'yevdokia@gmail.com', '+380679876543'),
('Дмитро Лисогор', 'dmytro@example.com', '+380931112233');

INSERT INTO time_slot (facility_id, slot_date, start_time, end_time, price) VALUES
(1, '2026-11-01', '10:00', '12:00', 500.0),
(1, '2026-11-01', '14:00', '16:00', 550.0),
(2, '2026-11-01', '14:00', '15:00', 300.0),
(3, '2026-11-02', '09:00', '11:00', 250.0);

INSERT INTO booking (user_id, time_slot_id, state, f1, f2, f3, integral_index) VALUES
(1, 1, 'CREATED', 50.0, 60.0, 40.0, 51.0),
(2, 3, 'CONFIRMED', 30.0, 40.0, 20.0, 31.0);
