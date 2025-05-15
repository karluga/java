CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT,
    user_id INT DEFAULT NULL,
    customer_name VARCHAR(255) DEFAULT NULL,
    start_date DATE,
    end_date DATE,
    number_of_people INT,
    is_paid TINYINT(1) DEFAULT 0,
    total_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
