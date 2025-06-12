DROP DATABASE IF EXISTS fms_db;

CREATE DATABASE IF NOT EXISTS fms_db;
USE fms_db;

CREATE TABLE IF NOT EXISTS parameter
(
	id INT AUTO_INCREMENT PRIMARY KEY,
    max_medium_airport INT NOT NULL,
    min_flight_duration INT NOT NULL, -- mins
    min_layover_duration INT NOT NULL, -- mins
    max_layover_duration INT NOT NULL, -- mins
    min_booking_in_advance_duration INT NOT NULL,
    max_booking_hold_duration INT NOT NULL,
    deleted_at DATETIME DEFAULT NULL
);    

CREATE TABLE IF NOT EXISTS `account`
(
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    account_name VARCHAR(200) NOT NULL,
    `password` VARCHAR(200) NOT NULL,
    account_type INT NOT NULL, -- 1: customer, 2: employee
    email VARCHAR(200) NOT NULL UNIQUE,
    citizen_id VARCHAR(200) NOT NULL UNIQUE,
    phone_number VARCHAR(200) NOT NULL,
    password_reset_token VARCHAR(512) NULL,
    password_reset_expiry DATETIME NULL,
    deleted_at DATETIME DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS employee
(
    employee_id INT PRIMARY KEY,
    employee_type INT NOT NULL, -- 1: tiep nhan lich bay, 2: ban/dat ve, 3: cskh, 4: ke toan, 5: sa
    deleted_at DATETIME DEFAULT NULL,
    FOREIGN KEY (employee_id) REFERENCES `account`(account_id)
);

CREATE TABLE IF NOT EXISTS customer
(
    customer_id INT PRIMARY KEY,
    score INT DEFAULT 0,
    deleted_at DATETIME DEFAULT NULL,
    FOREIGN KEY (customer_id) REFERENCES `account`(account_id)
);

CREATE TABLE IF NOT EXISTS passenger
(
    passenger_id INT AUTO_INCREMENT PRIMARY KEY,
    passenger_name VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL,
    citizen_id VARCHAR(200) NOT NULL UNIQUE,
    phone_number VARCHAR(200) NOT NULL,
    deleted_at DATETIME DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS plane
(
    plane_id INT AUTO_INCREMENT PRIMARY KEY,
    plane_code VARCHAR(200) NOT NULL,
    plane_type VARCHAR(200) NOT NULL,
    seat_quantity INT NOT NULL,
    deleted_at DATETIME DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS airport
(
    airport_id INT AUTO_INCREMENT PRIMARY KEY,
    airport_name VARCHAR(200) NOT NULL,
    city_name VARCHAR(200) NOT NULL,
    country_name VARCHAR(200) NOT NULL,
    deleted_at DATETIME DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS ticket_class
(
    ticket_class_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_class_name VARCHAR(200) NOT NULL,
    color VARCHAR(200) NOT NULL,
    deleted_at DATETIME DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS flight
(
    flight_id INT AUTO_INCREMENT PRIMARY KEY,
    plane_id INT NOT NULL,
    departure_airport_id INT NOT NULL,
    arrival_airport_id INT NOT NULL,
    flight_code VARCHAR(200) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    deleted_at DATETIME DEFAULT NULL,
    FOREIGN KEY (plane_id) REFERENCES plane(plane_id),
    FOREIGN KEY (departure_airport_id) REFERENCES airport(airport_id),
    FOREIGN KEY (arrival_airport_id) REFERENCES airport(airport_id)
);

CREATE TABLE IF NOT EXISTS flight_detail
(
    flight_id INT NOT NULL,
    medium_airport_id INT NOT NULL,
    arrival_time DATETIME NOT NULL,
    layover_duration INT NOT NULL, -- mins
    deleted_at DATETIME DEFAULT NULL,
    PRIMARY KEY (flight_id, medium_airport_id),
    FOREIGN KEY (flight_id) REFERENCES flight(flight_id),
    FOREIGN KEY (medium_airport_id) REFERENCES airport(airport_id)
);

CREATE TABLE IF NOT EXISTS flight_ticket_class
(
    flight_id INT NOT NULL,
    ticket_class_id INT NOT NULL,
    ticket_quantity INT NOT NULL,
    remaining_ticket_quantity INT NOT NULL,
    specified_fare DECIMAL(11,2) NOT NULL,
    deleted_at DATETIME DEFAULT NULL,
    PRIMARY KEY (flight_id, ticket_class_id),
    FOREIGN KEY (ticket_class_id) REFERENCES ticket_class(ticket_class_id),
    FOREIGN KEY (flight_id) REFERENCES flight(flight_id)
);

CREATE TABLE IF NOT EXISTS ticket
(
    ticket_id INT auto_increment PRIMARY KEY,
    flight_id INT NOT NULL,
    ticket_class_id INT NOT NULL,
    book_customer_id INT, -- nullable
    passenger_id INT NOT NULL,
    seat_number VARCHAR(7) NOT NULL,
    ticket_status TINYINT DEFAULT 0, -- 0: unpaid, 1: paid,
    payment_time DATETIME, -- nullable
    fare DECIMAL(10,2) NOT NULL,
    confirmation_code varchar(20) not null,
    order_id varchar(100) DEFAULT NULL,
    deleted_at DATETIME DEFAULT NULL,
    FOREIGN KEY (flight_id) REFERENCES flight(flight_id),
    FOREIGN KEY (ticket_class_id) REFERENCES ticket_class(ticket_class_id),
    FOREIGN KEY (book_customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (passenger_id) REFERENCES passenger(passenger_id)
);

CREATE TABLE IF NOT EXISTS chatbox
(
    chatbox_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    deleted_at DATETIME DEFAULT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

CREATE TABLE IF NOT EXISTS message
(
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    chatbox_id INT NOT NULL,
    employee_id INT NULL,
    content TEXT NOT NULL,
    send_time DATETIME NOT NULL,
    deleted_at DATETIME DEFAULT NULL,
    FOREIGN KEY (chatbox_id) REFERENCES chatbox(chatbox_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

CREATE TABLE IF NOT EXISTS account_chatbox (
    account_id INT NOT NULL,
    chatbox_id INT NOT NULL,
    last_visit_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    PRIMARY KEY (account_id, chatbox_id),
    FOREIGN KEY (account_id) REFERENCES account(account_id),
    FOREIGN KEY (chatbox_id) REFERENCES chatbox(chatbox_id),
    INDEX idx_account_chatbox_last_visit (account_id, last_visit_time),
    INDEX idx_chatbox_last_visit (chatbox_id, last_visit_time)
);

-- insert into customer values (3, 0, null);
-- select * from account;
-- select * from airport;
-- select * from chatbox;
-- select * from customer;
-- select * from employee;
-- select * from flight;
-- select * from flight_detail;
-- select * from flight_ticket_class;
-- select * from message;
-- select * from parameter;
-- select * from passenger;
-- select * from plane;
-- select * from ticket;
-- select * from ticket_class;