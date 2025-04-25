CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT,
    user_id INT DEFAULT NULL,
    customer_name VARCHAR(255) DEFAULT NULL,
    start_date DATE,
    end_date DATE,
    max_people INT,
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
