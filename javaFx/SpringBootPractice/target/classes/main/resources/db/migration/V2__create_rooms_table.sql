CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    max_people INT DEFAULT 1,
    price_per_night DECIMAL(10, 2) NOT NULL DEFAULT 50.00
);