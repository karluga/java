-- FILE MADE 13/05/2025

-- Users
INSERT INTO users (username, password, role) VALUES
('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 1), -- "admin"
('user1', '0a041b9462caa4a31bac3567e0b6e6fd9100787db2ab433d96f6d178cabfce90', 0), -- "user_password"
('manager', '6ee4a469cd4e91053847f5d3fcb61dbcc91e8f0ef10be7748da4c4a1ba382d17', 1), -- "manager"
('reception', '066a4a70376da00eb9e50a8e30725427faf9b9573d0c6430d28316497c889213', 1), -- "reception"
('guest1', '4676424d5f805a7579abd1236287be2abf24f39b8a622ef587edd7d91b8e2952', 0), -- "password"
('guest2', '5be6ba1446ed4bd41b656b740eccab74524602bc5f274e03677b6bab7d424dd5', 0),
('traveler', 'cd50fc998e7e535b8908c8efc8233cfc25ffa79ed9067abf1ebd32c52ffc87df', 0), -- "test"
('business1', '08cd33462126d91ae9312d45349f62cab11f119d10ef403362c28fb37df8195c', 0),
('family1', 'ece67b9976927462bb04b849b659c59fba1625176f9708ae2c631fa1d50aa163', 0),
('couple1', '5d2fc3e768d671006bb0b0466c0141c7e1dd9079fa65ed5f783e2d0c5f0fba7c', 0),
('solo1', '9d792f0b24db5f270d23405f43f32083f290415380399a76d303139fe398fcff', 0),
('vacationer', '8ee924492244603f9e364a949ba16d9b4f5c3a03137d2834ee522d6be95cf1a7', 0);

-- Rooms with prices
INSERT INTO rooms (name, max_people, price_per_night) VALUES 
('Room 101', 2, 60.00),
('Room 102', 3, 75.00),
('Room 103', 1, 50.00),
('Room 104', 2, 65.00),
('Room 105', 4, 90.00),
('Room 106', 1, 55.00),
('Room 107', 3, 80.00),
('Room 108', 2, 70.00),
('Room 109', 5, 120.00),
('Room 110', 2, 60.00),
('Room 111', 3, 75.00),
('Room 112', 1, 50.00),
('Room 113', 4, 95.00),
('Room 114', 2, 65.00),
('Room 115', 3, 85.00),
('Room 116', 1, 45.00),
('Room 117', 2, 70.00),
('Room 118', 5, 110.00),
('Room 119', 3, 80.00),
('Room 120', 2, 60.00);


-- Additional Bookings (mix of registered users and walk-ins)
-- Past bookings
INSERT INTO bookings (room_id, user_id, customer_name, start_date, end_date, number_of_people, is_paid, total_price) VALUES
(4, 3, NULL, '2025-01-05', '2025-01-10', 2, 1, 325.00),
(5, NULL, 'Smith Family', '2025-01-12', '2025-01-15', 4, 1, 270.00),
(6, NULL, 'Jane Smith', '2025-01-20', '2025-01-22', 1, 1, 110.00),
(7, 4, NULL, '2025-02-03', '2025-02-07', 3, 1, 320.00),
(8, NULL, 'Robert Johnson', '2025-02-10', '2025-02-12', 2, 1, 140.00),
(9, 5, NULL, '2025-02-14', '2025-02-16', 5, 1, 240.00),
(10, NULL, 'Emily Davis', '2025-02-20', '2025-02-25', 2, 1, 300.00),
(11, 6, NULL, '2025-03-01', '2025-03-05', 3, 1, 300.00),
(12, NULL, 'Michael Wilson', '2025-03-10', '2025-03-12', 1, 1, 100.00),
(13, 7, NULL, '2025-03-15', '2025-03-20', 4, 1, 475.00);

-- Current bookings (ongoing or upcoming)
INSERT INTO bookings (room_id, user_id, customer_name, start_date, end_date, number_of_people, is_paid, total_price) VALUES
(14, 8, NULL, '2025-05-10', '2025-05-15', 2, 1, 325.00),
(15, NULL, 'Sarah Miller', '2025-05-12', '2025-05-17', 3, 0, 425.00),
(16, 9, NULL, '2025-05-14', '2025-05-16', 1, 1, 90.00),
(17, NULL, 'David Brown', '2025-05-18', '2025-05-22', 2, 0, 280.00),
(18, 10, NULL, '2025-05-20', '2025-05-25', 5, 1, 550.00),
(1, NULL, 'Lisa Taylor', '2025-05-22', '2025-05-24', 2, 1, 120.00),
(2, 3, NULL, '2025-05-25', '2025-05-30', 3, 0, 375.00),
(3, NULL, 'James Wilson', '2025-05-28', '2025-06-02', 1, 1, 250.00),
(4, 4, NULL, '2025-06-01', '2025-06-05', 2, 0, 260.00),
(5, NULL, 'Olivia Martinez', '2025-06-03', '2025-06-08', 4, 1, 450.00);

-- Future bookings
INSERT INTO bookings (room_id, user_id, customer_name, start_date, end_date, number_of_people, is_paid, total_price) VALUES
(6, 5, NULL, '2025-06-10', '2025-06-15', 1, 0, 275.00),
(7, NULL, 'William Anderson', '2025-06-12', '2025-06-17', 3, 0, 400.00),
(8, 6, NULL, '2025-06-15', '2025-06-18', 2, 0, 210.00),
(9, NULL, 'Sophia Thomas', '2025-06-20', '2025-06-25', 5, 0, 600.00),
(10, 7, NULL, '2025-07-01', '2025-07-05', 2, 0, 240.00),
(11, NULL, 'Daniel Jackson', '2025-07-05', '2025-07-10', 3, 0, 375.00),
(12, 8, NULL, '2025-07-10', '2025-07-12', 1, 0, 100.00),
(13, NULL, 'Ava White', '2025-07-15', '2025-07-20', 4, 0, 475.00),
(14, 9, NULL, '2025-07-20', '2025-07-25', 2, 0, 325.00),
(15, NULL, 'Ethan Harris', '2025-08-01', '2025-08-05', 3, 0, 340.00);

-- Payments for bookings (both full and partial payments)
INSERT INTO payments (reservation_id, amount_paid) VALUES
-- For past bookings
(1, 325.00),
(2, 270.00),
(3, 110.00),
(4, 320.00),
(5, 140.00),
(6, 240.00),
(7, 300.00),
(8, 300.00),
(9, 100.00),
(10, 475.00),

-- For current bookings
(11, 325.00),
(12, 212.50), -- partial payment
(13, 90.00),
(14, 140.00), -- partial payment
(15, 550.00),
(16, 120.00),
(17, 187.50), -- partial payment
(18, 250.00),
(19, 130.00), -- partial payment
(20, 450.00),

-- For future bookings (mostly deposits)
(21, 55.00), -- 20% deposit
(22, 80.00), -- 20% deposit
(23, 42.00), -- 20% deposit
(24, 120.00), -- 20% deposit
(25, 48.00), -- 20% deposit
(26, 75.00), -- 20% deposit
(27, 20.00), -- 20% deposit
(28, 95.00), -- 20% deposit
(29, 65.00), -- 20% deposit
(30, 68.00); -- 20% deposit
