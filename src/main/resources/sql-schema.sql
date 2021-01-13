CREATE DATABASE IF NOT EXISTS ims;
CREATE TABLE IF NOT EXISTS ims.customers(customer_id INT UNIQUE NOT NULL AUTO_INCREMENT, first_name VARCHAR(100) NOT NULL, last_name VARCHAR(100) NOT NULL, PRIMARY KEY (customer_id));
CREATE TABLE IF NOT EXISTS ims.orders(order_id INT UNIQUE NOT NULL AUTO_INCREMENT, customer_id INT NOT NULL, date_placed DATETIME DEFAULT NOW() NOT NULL, PRIMARY KEY (order_id), FOREIGN KEY (customer_id) REFERENCES customers(customer_id));
CREATE TABLE IF NOT EXISTS ims.items(item_id INT UNIQUE NOT NULL AUTO_INCREMENT, item_name VARCHAR(100) NOT NULL, price DECIMAL(10,2) NOT NULL, stock INT NOT NULL, PRIMARY KEY (item_id));
CREATE TABLE IF NOT EXISTS ims.orderlines(orderline_id INT UNIQUE NOT NULL AUTO_INCREMENT, order_id INT NOT NULL, item_id INT NOT NULL, quantity INT NOT NULL, PRIMARY KEY (orderline_id), FOREIGN KEY (order_id) REFERENCES orders(order_id), FOREIGN KEY (item_id) REFERENCES items(item_id));