-- Users
INSERT INTO users (username, password, role) VALUES ('admin', '21232f297a57a5a743894a0e4a801fc3', 1); -- "admin"
INSERT INTO users (username, password, role) VALUES ('user1', 'ee11cbb19052e40b07aac0ca060c23ee', 0); -- "user_password"

-- Rooms with prices
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 101', 2, 60.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 102', 3, 75.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 103', 1, 50.00);

-- Additional Rooms with prices
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 104', 2, 65.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 105', 4, 90.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 106', 1, 55.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 107', 3, 80.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 108', 2, 70.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 109', 5, 120.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 110', 2, 60.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 111', 3, 75.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 112', 1, 50.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 113', 4, 95.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 114', 2, 65.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 115', 3, 85.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 116', 1, 45.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 117', 2, 70.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 118', 5, 110.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 119', 3, 80.00);
INSERT INTO rooms (name, max_people, price_per_night) VALUES ('Room 120', 2, 60.00);

-- Bookings: mix of user and name-based
INSERT INTO bookings (room_id, user_id, start_date, end_date, max_people)
VALUES (1, 1, '2025-04-19', '2025-04-22', 2);

INSERT INTO bookings (room_id, user_id, start_date, end_date, max_people)
VALUES (2, 2, '2025-04-20', '2025-04-23', 3);

INSERT INTO bookings (room_id, customer_name, start_date, end_date, max_people)
VALUES (3, 'John Doe', '2025-04-21', '2025-04-24', 1);
