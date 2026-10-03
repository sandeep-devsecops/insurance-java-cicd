USE insurance;
CREATE TABLE IF NOT EXISTS policies(id INT AUTO_INCREMENT PRIMARY KEY,customer VARCHAR(100) NOT NULL,type VARCHAR(30) NOT NULL,premium DECIMAL(10,2) NOT NULL);
INSERT INTO policies(customer,type,premium) VALUES ('Demo Customer','Health',2500.00),('Sample Customer','Vehicle',1200.00);
