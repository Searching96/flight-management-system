-- Dữ liệu cấu hình hệ thống FMS
INSERT INTO parameter (
    max_medium_airport, 
    min_flight_duration, 
    min_layover_duration, 
    max_layover_duration, 
    min_booking_in_advance_duration, 
    max_booking_hold_duration,
    deleted_at
) VALUES (
    2,      -- Tối đa 2 sân bay trung gian trong 1 chuyến bay
    45,     -- Thời gian bay tối thiểu 45 phút
    30,     -- Thời gian chờ nối chuyến tối thiểu 30 phút
    90,     -- Thời gian chờ nối chuyến tối đa 12 giờ (720 phút)
    1,      -- Đặt vé trước tối thiểu 1 ngày
    24,     -- Giữ vé tối đa 24 giờ chưa thanh toán
    NULL    -- Chưa bị xóa
);

-- Dữ liệu tài khoản nhân viên (7 loại nhân viên)
INSERT INTO account (account_name, password, account_type, email, citizen_id, phone_number, password_reset_token, password_reset_expiry, deleted_at) VALUES
-- Employee Type 1: Tiếp nhận lịch bay
('Nguyễn Văn An', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 2, 'an.nguyen@fms.vn', '024791001001', '+84901234001', NULL, NULL, NULL),

-- Employee Type 2: Bán/Đặt vé
('Lê Thị Bình', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 2, 'binh.le@fms.vn', '024791001002', '+84901234002', NULL, NULL, NULL),

-- Employee Type 3: CSKH
('Trần Văn Cường', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 2, 'cuong.tran@fms.vn', '024791001003', '+84901234003', NULL, NULL, NULL),

-- Employee Type 4: Kế toán
('Phạm Thị Dung', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 2, 'dung.pham@fms.vn', '024791001004', '+84901234004', NULL, NULL, NULL),

-- Employee Type 5: Dịch vụ bay
('Hoàng Văn Em', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 2, 'em.hoang@fms.vn', '024791001005', '+84901234005', NULL, NULL, NULL),

-- Employee Type 6: Nhân sự
('Vũ Thị Giang', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 2, 'giang.vu@fms.vn', '024791001006', '+84901234006', NULL, NULL, NULL),

-- Employee Type 7: Super Admin
('Đỗ Văn Hải', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 2, 'hai.do@fms.vn', '024791001007', '+84901234007', NULL, NULL, NULL);

-- Dữ liệu nhân viên (7 nhân viên theo 7 loại employee_type)
INSERT INTO employee (employee_id, employee_type, deleted_at) VALUES
-- Employee Type 1: Tiếp nhận lịch bay (account_id = 1)
(1, 1, NULL),

-- Employee Type 2: Bán/Đặt vé (account_id = 2)  
(2, 2, NULL),

-- Employee Type 3: CSKH - Chăm sóc khách hàng (account_id = 3)
(3, 3, NULL),

-- Employee Type 4: Kế toán (account_id = 4)
(4, 4, NULL),

-- Employee Type 5: Dịch vụ bay (account_id = 5)
(5, 5, NULL),

-- Employee Type 6: Nhân sự (account_id = 6)
(6, 6, NULL),

-- Employee Type 7: Super Admin (account_id = 7)
(7, 7, NULL);

-- Airport table matching original schema exactly
INSERT INTO airport (airport_name, city_name, country_name, deleted_at) VALUES
-- Vietnam - North
('Sân bay Quốc tế Nội Bài', 'Hà Nội', 'Việt Nam', NULL),
('Sân bay Cát Bi', 'Hải Phòng', 'Việt Nam', NULL),
('Sân bay Điện Biên Phủ', 'Điện Biên', 'Việt Nam', NULL),
('Sân bay Đồng Hới', 'Quảng Bình', 'Việt Nam', NULL),
('Sân bay Thọ Xuân', 'Thanh Hóa', 'Việt Nam', NULL),

-- Vietnam - Central
('Sân bay Quốc tế Đà Nẵng', 'Đà Nẵng', 'Việt Nam', NULL),
('Sân bay Phú Bài', 'Thừa Thiên Huế', 'Việt Nam', NULL),
('Sân bay Cam Ranh', 'Khánh Hòa', 'Việt Nam', NULL),
('Sân bay Tuy Hòa', 'Phú Yên', 'Việt Nam', NULL),
('Sân bay Pleiku', 'Gia Lai', 'Việt Nam', NULL),
('Sân bay Buôn Ma Thuột', 'Đắk Lắk', 'Việt Nam', NULL),
('Sân bay Cần Thơ', 'Cần Thơ', 'Việt Nam', NULL),

-- Vietnam - South
('Sân bay Quốc tế Tân Sơn Nhất', 'TP. Hồ Chí Minh', 'Việt Nam', NULL),
('Sân bay Rạch Giá', 'Kiên Giang', 'Việt Nam', NULL),
('Sân bay Cà Mau', 'Cà Mau', 'Việt Nam', NULL),
('Sân bay Quốc tế Phú Quốc', 'Kiên Giang', 'Việt Nam', NULL),
('Sân bay Côn Đảo', 'Bà Rịa - Vũng Tàu', 'Việt Nam', NULL),
('Sân bay Liên Khương', 'Lâm Đồng', 'Việt Nam', NULL),

-- International - Southeast Asia
('Sân bay Quốc tế Suvarnabhumi', 'Bangkok', 'Thái Lan', NULL),
('Sân bay Don Mueang', 'Bangkok', 'Thái Lan', NULL),
('Sân bay Changi', 'Singapore', 'Singapore', NULL),
('Sân bay Quốc tế Kuala Lumpur', 'Kuala Lumpur', 'Malaysia', NULL),
('Sân bay Soekarno-Hatta', 'Jakarta', 'Indonesia', NULL),
('Sân bay Quốc tế Ninoy Aquino', 'Manila', 'Philippines', NULL),

-- International - East Asia
('Sân bay Quốc tế Incheon', 'Seoul', 'Hàn Quốc', NULL),
('Sân bay Gimpo', 'Seoul', 'Hàn Quốc', NULL),
('Sân bay Quốc tế Narita', 'Tokyo', 'Nhật Bản', NULL),
('Sân bay Haneda', 'Tokyo', 'Nhật Bản', NULL),
('Sân bay Quốc tế Bắc Kinh', 'Bắc Kinh', 'Trung Quốc', NULL),
('Sân bay Phố Đông', 'Thượng Hải', 'Trung Quốc', NULL),
('Sân bay Bạch Vân', 'Quảng Châu', 'Trung Quốc', NULL),

-- International - Europe
('Sân bay Charles de Gaulle', 'Paris', 'Pháp', NULL),
('Sân bay Heathrow', 'London', 'Anh', NULL),
('Sân bay Frankfurt', 'Frankfurt', 'Đức', NULL),
('Sân bay Schiphol', 'Amsterdam', 'Hà Lan', NULL),

-- International - North America
('Sân bay Quốc tế Los Angeles', 'Los Angeles', 'Mỹ', NULL),
('Sân bay John F. Kennedy', 'New York', 'Mỹ', NULL),
('Sân bay Quốc tế San Francisco', 'San Francisco', 'Mỹ', NULL),
('Sân bay Quốc tế Vancouver', 'Vancouver', 'Canada', NULL),

-- International - Australia
('Sân bay Kingsford Smith', 'Sydney', 'Úc', NULL),
('Sân bay Melbourne', 'Melbourne', 'Úc', NULL),

-- International - South Asia
('Sân bay Quốc tế Indira Gandhi', 'New Delhi', 'Ấn Độ', NULL),
('Sân bay Chhatrapati Shivaji', 'Mumbai', 'Ấn Độ', NULL),

-- International - Middle East
('Sân bay Quốc tế Dubai', 'Dubai', 'UAE', NULL),
('Sân bay Quốc tế Hamad', 'Doha', 'Qatar', NULL);

-- Dữ liệu máy bay (plane) với plane_code và plane_type format mới
INSERT INTO plane (plane_code, plane_type, seat_quantity, deleted_at) VALUES

-- ============= AIRBUS A320 FAMILY - DOMESTIC & REGIONAL =============
-- A320-200 Series (180 seats)
('VN-A001', 'Airbus A320-200', 180, NULL),
('VN-A002', 'Airbus A320-200', 180, NULL),
('VN-A003', 'Airbus A320-200', 180, NULL),
('VN-A004', 'Airbus A320-200', 180, NULL),
('VN-A005', 'Airbus A320-200', 180, NULL),

-- A321-200 Series (220 seats)
('VN-A006', 'Airbus A321-200', 220, NULL),
('VN-A007', 'Airbus A321-200', 220, NULL),
('VN-A008', 'Airbus A321-200', 220, NULL),

-- ============= BOEING 737 FAMILY - DOMESTIC & REGIONAL =============
-- Boeing 737-800 Series (189 seats)
('VN-B001', 'Boeing 737-800', 189, NULL),
('VN-B002', 'Boeing 737-800', 189, NULL),
('VN-B003', 'Boeing 737-800', 189, NULL),
('VN-B004', 'Boeing 737-800', 189, NULL),

-- Boeing 737 MAX 8 Series (200 seats)
('VN-B005', 'Boeing 737 MAX 8', 200, NULL),
('VN-B006', 'Boeing 737 MAX 8', 200, NULL),

-- ============= AIRBUS WIDE-BODY - INTERNATIONAL LONG-HAUL =============
-- A330-200 Series (290 seats)
('VN-A101', 'Airbus A330-200', 290, NULL),
('VN-A102', 'Airbus A330-200', 290, NULL),

-- A330-300 Series (340 seats)
('VN-A103', 'Airbus A330-300', 340, NULL),
('VN-A104', 'Airbus A330-300', 340, NULL),

-- A350-900 Series (350 seats)
('VN-A105', 'Airbus A350-900', 350, NULL),
('VN-A106', 'Airbus A350-900', 350, NULL),

-- ============= BOEING WIDE-BODY - INTERNATIONAL LONG-HAUL =============
-- Boeing 777-200 Series (350 seats)
('VN-B101', 'Boeing 777-200', 350, NULL),
('VN-B102', 'Boeing 777-200', 350, NULL),

-- Boeing 787-9 Dreamliner Series (330 seats)
('VN-B103', 'Boeing 787-9 Dreamliner', 330, NULL),
('VN-B104', 'Boeing 787-9 Dreamliner', 330, NULL),

-- ============= REGIONAL AIRCRAFT - DOMESTIC SHORT-HAUL =============
-- ATR 72-500 Series (70 seats)
('VN-R001', 'ATR 72-500', 70, NULL),
('VN-R002', 'ATR 72-500', 70, NULL),

-- ATR 72-600 Series (78 seats)
('VN-R003', 'ATR 72-600', 78, NULL),
('VN-R004', 'ATR 72-600', 78, NULL),

-- ============= EMBRAER REGIONAL JETS =============
-- Embraer E190 Series (100 seats)
('VN-E001', 'Embraer E190', 100, NULL),
('VN-E002', 'Embraer E190', 100, NULL),

-- ============= ADDITIONAL AIRCRAFT (Current Time: 17:28:21) =============
-- Recent deliveries và active aircraft

-- More A320neo family (fuel efficient)
('VN-A009', 'Airbus A320neo', 180, NULL),
('VN-A010', 'Airbus A320neo', 180, NULL),
('VN-A011', 'Airbus A321neo', 220, NULL),

-- Additional Boeing 737 MAX (post-grounding)
('VN-B007', 'Boeing 737 MAX 8', 200, NULL),
('VN-B008', 'Boeing 737 MAX 8', 200, NULL),

-- Premium long-haul aircraft
('VN-A107', 'Airbus A350-1000', 380, NULL),  -- Flagship aircraft
('VN-B105', 'Boeing 777-300ER', 400, NULL),  -- High capacity long-haul

-- Additional regional coverage
('VN-R005', 'ATR 72-600', 78, NULL),
('VN-R006', 'ATR 42-600', 50, NULL),  -- Smaller regional routes

-- Business/VIP aircraft
('VN-V001', 'Airbus A319 Corporate Jet', 50, NULL),  -- Government/VIP flights

-- Cargo conversion aircraft
('VN-C001', 'Boeing 737-800F', 0, NULL),  -- Pure cargo, no passengers
('VN-C002', 'Airbus A330-200F', 0, NULL), -- International cargo

-- ============= MAINTENANCE STATUS =============
-- Aircraft currently out of service (soft deleted)
('VN-A012', 'Airbus A320-200', 180, '2025-06-01 09:30:00'),  -- Heavy maintenance
('VN-B009', 'Boeing 737-800', 189, '2025-06-05 14:20:00');   -- Engine overhaul

DELIMITER $$

CREATE PROCEDURE generate_chronological_flights()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE rand_plane INT;
    DECLARE rand_dep_airport INT;
    DECLARE rand_arr_airport INT;
    DECLARE dep_time DATETIME;
    DECLARE arr_time DATETIME;
    DECLARE new_flight_id INT;

    -- Set the start and end date
    DECLARE start_date DATETIME DEFAULT '2024-06-01 06:00:00';
    DECLARE end_date DATETIME DEFAULT '2025-12-31 23:00:00';
    DECLARE total_flights INT DEFAULT 100;
    DECLARE interval_seconds BIGINT;
    DECLARE seconds_to_add BIGINT;

    -- Calculate the interval in seconds between each flight
    SET interval_seconds = TIMESTAMPDIFF(SECOND, start_date, end_date) DIV (total_flights - 1);

    WHILE i < total_flights DO
        -- Get random plane_id
        SELECT plane_id INTO rand_plane FROM plane ORDER BY RAND() LIMIT 1;

        -- Get two distinct random airport_ids
        SELECT airport_id INTO rand_dep_airport FROM airport ORDER BY RAND() LIMIT 1;
        SELECT airport_id INTO rand_arr_airport FROM airport WHERE airport_id != rand_dep_airport ORDER BY RAND() LIMIT 1;

        -- Calculate departure time for this flight
        SET seconds_to_add = i * interval_seconds;
        SET dep_time = DATE_ADD(start_date, INTERVAL seconds_to_add SECOND);

        -- Arrival time: 1 to 5 hours after departure
        SET arr_time = DATE_ADD(dep_time, INTERVAL FLOOR(1 + RAND() * 5) HOUR);

        -- Insert the flight with placeholder flight_code
        INSERT INTO flight (plane_id, departure_airport_id, arrival_airport_id, flight_code, departure_time, arrival_time, deleted_at)
        VALUES (rand_plane, rand_dep_airport, rand_arr_airport, '', dep_time, arr_time, NULL);

        -- Get the last inserted flight_id
        SET new_flight_id = LAST_INSERT_ID();

        -- Update flight_code to 'VN-{last 3 digits of flight_id}'
        UPDATE flight
        SET flight_code = CONCAT('VN-', LPAD(MOD(new_flight_id, 1000), 3, '0'))
        WHERE flight_id = new_flight_id;

        SET i = i + 1;
    END WHILE;
END$$

DELIMITER ;

-- Call the procedure to generate 100 chronological flights
CALL generate_chronological_flights();

-- (Optional) Drop the procedure after use
DROP PROCEDURE generate_chronological_flights;


-- ============= BẢNG TICKET_CLASS (Master Data) =============

INSERT INTO ticket_class (ticket_class_name, color, deleted_at) VALUES
('Economy', '#4CAF50', NULL),          -- Xanh lá cây - phổ thông
('Premium Economy', '#FF9800', NULL),  -- Cam - trung cấp
('Business', '#2196F3', NULL),         -- Xanh dương - thương gia
('First', '#9C27B0', NULL);            -- Tím - hạng nhất

-- ============= BẢNG FLIGHT_TICKET_CLASS (Flight-specific Data) =============
DELIMITER $$

CREATE PROCEDURE generate_flight_ticket_classes()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE flightId INT;
    DECLARE planeSeats INT;
    DECLARE ticketClassCount INT;
    DECLARE ticketClassId INT;
    DECLARE totalAllocated DECIMAL(11,2);
    DECLARE remainingSeats DECIMAL(11,2);
    DECLARE weightSum INT;
    DECLARE specifiedFare DECIMAL(11,2);
    DECLARE ticketQuantity DECIMAL(11,2);
    DECLARE remainingQuantity INT;
    DECLARE weight INT;

    -- Cursor for all flights with their plane's seat capacity
    DECLARE flight_cursor CURSOR FOR
        SELECT f.flight_id, p.seat_quantity
        FROM flight f
        JOIN plane p ON f.plane_id = p.plane_id;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN flight_cursor;

    flight_loop: LOOP
        FETCH flight_cursor INTO flightId, planeSeats;
        IF done THEN
            LEAVE flight_loop;
        END IF;

        -- Determine how many ticket classes to assign (1 to all)
        SELECT COUNT(*) INTO ticketClassCount FROM ticket_class;
        SET ticketClassCount = 1 + FLOOR(RAND() * ticketClassCount);

        -- Temporary table for random ticket_class_ids with weights
        CREATE TEMPORARY TABLE temp_ticket_class (
            ticket_class_id INT,
            weight INT
        );

        -- Assign random weights to selected classes
        INSERT INTO temp_ticket_class
        SELECT ticket_class_id, FLOOR(1 + RAND() * 10)
        FROM ticket_class
        ORDER BY RAND()
        LIMIT ticketClassCount;

        -- Calculate total weight for distribution
        SELECT SUM(weight) INTO weightSum FROM temp_ticket_class;

        -- Allocate seats proportionally based on weights
        SET totalAllocated = 0;
        SET remainingSeats = planeSeats;

        ticket_class_loop: BEGIN
            DECLARE done2 INT DEFAULT 0;
            DECLARE cur CURSOR FOR SELECT ticket_class_id, weight FROM temp_ticket_class;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET done2 = 1;

            OPEN cur;
            ticket_class_inner: LOOP
                FETCH cur INTO ticketClassId, weight;
                IF done2 THEN
                    LEAVE ticket_class_inner;
                END IF;

                -- Calculate seats for this class (proportional to weight), no rounding
                IF weightSum > 0 THEN
                    SET ticketQuantity = planeSeats * (weight / weightSum);
                ELSE
                    SET ticketQuantity = planeSeats;
                END IF;

                -- Handle remaining seats
                IF remainingSeats - ticketQuantity < 0 THEN
                    SET ticketQuantity = remainingSeats;
                END IF;

                SET remainingSeats = remainingSeats - ticketQuantity;
                SET totalAllocated = totalAllocated + ticketQuantity;

                -- Generate remaining tickets (50-100% of allocated), rounded as integer
                SET remainingQuantity = FLOOR(ticketQuantity * (0.5 + RAND() * 0.5));
                SET specifiedFare = 500000 + FLOOR(RAND() * 4500001);

                INSERT INTO flight_ticket_class (
                    flight_id,
                    ticket_class_id,
                    ticket_quantity,
                    remaining_ticket_quantity,
                    specified_fare,
                    deleted_at
                ) VALUES (
                    flightId,
                    ticketClassId,
                    ticketQuantity,
                    remainingQuantity,
                    specifiedFare,
                    NULL
                );

                SET ticketClassCount = ticketClassCount - 1;
                SET weightSum = weightSum - weight;
            END LOOP ticket_class_inner;
            CLOSE cur;
        END ticket_class_loop;

        DROP TEMPORARY TABLE IF EXISTS temp_ticket_class;
    END LOOP flight_loop;

    CLOSE flight_cursor;
END$$

DELIMITER ;



-- Call the procedure
CALL generate_flight_ticket_classes();

-- (Optional) Drop the procedure
DROP PROCEDURE generate_flight_ticket_classes;

-- ============= BẢNG CHATBOX =============
-- Generate 24 customer accounts (Khách A -> Khách Z, excluding J, W)
INSERT INTO `account` (account_name, `password`, account_type, email, citizen_id, phone_number) VALUES
('Khách A', 'password123', 1, 'khach.a@email.com', '001000000001', '0900000001'),
('Khách B', 'password123', 1, 'khach.b@email.com', '001000000002', '0900000002'),
('Khách C', 'password123', 1, 'khach.c@email.com', '001000000003', '0900000003'),
('Khách D', 'password123', 1, 'khach.d@email.com', '001000000004', '0900000004'),
('Khách E', 'password123', 1, 'khach.e@email.com', '001000000005', '0900000005'),
('Khách F', 'password123', 1, 'khach.f@email.com', '001000000006', '0900000006'),
('Khách G', 'password123', 1, 'khach.g@email.com', '001000000007', '0900000007'),
('Khách H', 'password123', 1, 'khach.h@email.com', '001000000008', '0900000008'),
('Khách I', 'password123', 1, 'khach.i@email.com', '001000000009', '0900000009'),
('Khách K', 'password123', 1, 'khach.k@email.com', '001000000010', '0900000010'),
('Khách L', 'password123', 1, 'khach.l@email.com', '001000000011', '0900000011'),
('Khách M', 'password123', 1, 'khach.m@email.com', '001000000012', '0900000012'),
('Khách N', 'password123', 1, 'khach.n@email.com', '001000000013', '0900000013'),
('Khách O', 'password123', 1, 'khach.o@email.com', '001000000014', '0900000014'),
('Khách P', 'password123', 1, 'khach.p@email.com', '001000000015', '0900000015'),
('Khách Q', 'password123', 1, 'khach.q@email.com', '001000000016', '0900000016'),
('Khách R', 'password123', 1, 'khach.r@email.com', '001000000017', '0900000017'),
('Khách S', 'password123', 1, 'khach.s@email.com', '001000000018', '0900000018'),
('Khách T', 'password123', 1, 'khach.t@email.com', '001000000019', '0900000019'),
('Khách U', 'password123', 1, 'khach.u@email.com', '001000000020', '0900000020'),
('Khách V', 'password123', 1, 'khach.v@email.com', '001000000021', '0900000021'),
('Khách X', 'password123', 1, 'khach.x@email.com', '001000000022', '0900000022'),
('Khách Y', 'password123', 1, 'khach.y@email.com', '001000000023', '0900000023'),
('Khách Z', 'password123', 1, 'khach.z@email.com', '001000000024', '0900000024'),
('Xuân Thịnh Trần', '$2a$10$j.PrGNrzwEoWo7c/NXjDDet.IuQBtLmherOzk.1pjOPkPHONQbtuG', 1, '23521515@gm.uit.edu.vn', '079123123123', '0879100100');

-- Generate corresponding customer records
INSERT INTO customer (customer_id, score) 
SELECT account_id, 0 FROM `account` WHERE account_type = 1 AND account_name LIKE 'Khách %';

INSERT INTO customer (customer_id, score)
SELECT account_id, 72500 FROM `account` WHERE account_type = 1 AND account_name = 'Xuân Thịnh Trần';

-- Generate 24 employee accounts for customer support (Chăm A -> Chăm Z, excluding J, W)
INSERT INTO `account` (account_name, `password`, account_type, email, citizen_id, phone_number) VALUES
('Chăm A', 'password123', 2, 'cham.a@company.com', '002000000001', '0800000001'),
('Chăm B', 'password123', 2, 'cham.b@company.com', '002000000002', '0800000002'),
('Chăm C', 'password123', 2, 'cham.c@company.com', '002000000003', '0800000003'),
('Chăm D', 'password123', 2, 'cham.d@company.com', '002000000004', '0800000004'),
('Chăm E', 'password123', 2, 'cham.e@company.com', '002000000005', '0800000005'),
('Chăm F', 'password123', 2, 'cham.f@company.com', '002000000006', '0800000006'),
('Chăm G', 'password123', 2, 'cham.g@company.com', '002000000007', '0800000007'),
('Chăm H', 'password123', 2, 'cham.h@company.com', '002000000008', '0800000008'),
('Chăm I', 'password123', 2, 'cham.i@company.com', '002000000009', '0800000009'),
('Chăm K', 'password123', 2, 'cham.k@company.com', '002000000010', '0800000010'),
('Chăm L', 'password123', 2, 'cham.l@company.com', '002000000011', '0800000011'),
('Chăm M', 'password123', 2, 'cham.m@company.com', '002000000012', '0800000012'),
('Chăm N', 'password123', 2, 'cham.n@company.com', '002000000013', '0800000013'),
('Chăm O', 'password123', 2, 'cham.o@company.com', '002000000014', '0800000014'),
('Chăm P', 'password123', 2, 'cham.p@company.com', '002000000015', '0800000015'),
('Chăm Q', 'password123', 2, 'cham.q@company.com', '002000000016', '0800000016'),
('Chăm R', 'password123', 2, 'cham.r@company.com', '002000000017', '0800000017'),
('Chăm S', 'password123', 2, 'cham.s@company.com', '002000000018', '0800000018'),
('Chăm T', 'password123', 2, 'cham.t@company.com', '002000000019', '0800000019'),
('Chăm U', 'password123', 2, 'cham.u@company.com', '002000000020', '0800000020'),
('Chăm V', 'password123', 2, 'cham.v@company.com', '002000000021', '0800000021'),
('Chăm X', 'password123', 2, 'cham.x@company.com', '002000000022', '0800000022'),
('Chăm Y', 'password123', 2, 'cham.y@company.com', '002000000023', '0800000023'),
('Chăm Z', 'password123', 2, 'cham.z@company.com', '002000000024', '0800000024');

-- Generate corresponding employee records (employee_type 3 = customer support)
INSERT INTO employee (employee_id, employee_type) 
SELECT account_id, 3 FROM `account` WHERE account_type = 2 AND account_name LIKE 'Chăm %';
-- =============================================================

-- INSERT PASSENGER
INSERT INTO passenger (passenger_name, email, citizen_id, phone_number) VALUES
('Nguyễn Văn An', 'nguyenvanan@email.com', '001199001234', '0901234567'),
('Trần Thị Bình', 'tranthibinh@email.com', '001299002345', '0902345678'),
('Lê Hoàng Cường', 'lehoangcuong@email.com', '001399003456', '0903456789'),
('Phạm Thị Dung', 'phamthidung@email.com', '001499004567', '0904567890'),
('Hoàng Văn Em', 'hoangvanem@email.com', '001599005678', '0905678901'),
('Vũ Thị Phượng', 'vuthiphuong@email.com', '001699006789', '0906789012'),
('Đặng Văn Giang', 'dangvangiang@email.com', '001799007890', '0907890123'),
('Bùi Thị Hoa', 'buithihoa@email.com', '001899008901', '0908901234'),
('Ngô Văn Inh', 'ngovaninh@email.com', '001999009012', '0909012345'),
('Cao Thị Kim', 'caothikim@email.com', '002099010123', '0910123456'),
('Lý Văn Long', 'lyvanlong@email.com', '002199011234', '0911234567'),
('Phan Thị Mai', 'phanthimai@email.com', '002299012345', '0912345678'),
('Đinh Văn Nam', 'dinhvannam@email.com', '002399013456', '0913456789'),
('Tô Thị Oanh', 'tothioanh@email.com', '002499014567', '0914567890'),
('Võ Văn Phúc', 'vovanphuc@email.com', '002599015678', '0915678901'),
('Đỗ Thị Quỳnh', 'dothiquynh@email.com', '002699016789', '0916789012'),
('Trương Văn Rồng', 'truongvanrong@email.com', '002799017890', '0917890123'),
('Lương Thị Sen', 'luongthisen@email.com', '002899018901', '0918901234'),
('Dương Văn Tâm', 'duongvantam@email.com', '002999019012', '0919012345'),
('Huỳnh Thị Uyên', 'huynhthiuyen@email.com', '003099020123', '0920123456'),
('Trịnh Văn Việt', 'trinhvanviet@email.com', '003199021234', '0921234567'),
('Lại Thị Xuân', 'laithixuan@email.com', '003299022345', '0922345678'),
('Phùng Văn Yên', 'phungvanyen@email.com', '003399023456', '0923456789'),
('Tạ Thị Zoan', 'tathizoan@email.com', '003499024567', '0924567890'),
('Mạc Văn An', 'macvanan@email.com', '003599025678', '0925678901');

INSERT INTO ticket (flight_id, ticket_class_id, book_customer_id, passenger_id, seat_number, ticket_status, payment_time, fare, confirmation_code, order_id) 
SELECT 
    -- Random flight_id from existing flights
    (SELECT flight_id FROM flight ORDER BY RAND() LIMIT 1) as flight_id,
    
    -- Random ticket_class_id from existing ticket classes
    (SELECT ticket_class_id FROM ticket_class ORDER BY RAND() LIMIT 1) as ticket_class_id,
    
    -- Random book_customer_id (nullable, 70% chance of having a value)
    CASE WHEN RAND() > 0.3 THEN (SELECT customer_id FROM customer ORDER BY RAND() LIMIT 1) ELSE NULL END as book_customer_id,
    
    -- Random passenger_id from existing passengers
    (SELECT passenger_id FROM passenger ORDER BY RAND() LIMIT 1) as passenger_id,
    
    -- Random seat number (format: 12A, 34B, etc.)
    CONCAT(
        LPAD(FLOOR(RAND() * 50) + 1, 2, '0'),
        CHAR(65 + FLOOR(RAND() * 6))
    ) as seat_number,
    
    -- Status = 1 (paid)
    1 as ticket_status,
    
    -- Random payment_time between 2023-04-12 and 2025-10-25
    DATE_ADD(
        '2023-04-12 00:00:00',
        INTERVAL FLOOR(RAND() * DATEDIFF('2025-10-25', '2023-04-12')) DAY
    ) + INTERVAL FLOOR(RAND() * 24) HOUR + INTERVAL FLOOR(RAND() * 60) MINUTE + INTERVAL FLOOR(RAND() * 60) SECOND as payment_time,
    
    -- Random fare between 500,000 and 15,000,000 VND
    ROUND((RAND() * 14500000) + 500000, 2) as fare,
    
    -- Random confirmation code (6 characters alphanumeric)
    CONCAT(
        CHAR(65 + FLOOR(RAND() * 26)),
        CHAR(65 + FLOOR(RAND() * 26)),
        LPAD(FLOOR(RAND() * 10), 1, '0'),
        CHAR(65 + FLOOR(RAND() * 26)),
        LPAD(FLOOR(RAND() * 10), 1, '0'),
        CHAR(65 + FLOOR(RAND() * 26))
    ) as confirmation_code,
    
    -- Random order_id (format: ORD-YYYY-XXXXXXXX)
    CONCAT(
        'ORD-',
        YEAR(CURDATE()),
        '-',
        LPAD(FLOOR(RAND() * 99999999) + 1, 8, '0')
    ) as order_id

FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t1
CROSS JOIN 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t2
CROSS JOIN 
    (SELECT 1 UNION SELECT 2) t3
LIMIT 150;

insert into flight (flight_id, plane_id, departure_airport_id, arrival_airport_id, flight_code, departure_time,
                    arrival_time, deleted_at)
values (101, 44, 1, 2, 'VN-101', '2026-02-01 00:00:00', '2026-02-01 03:00:00', null);

INSERT INTO flight_ticket_class (flight_id, ticket_class_id, ticket_quantity, remaining_ticket_quantity, specified_fare, deleted_at) VALUES (101, 1, 50, 46, 1000000.00, null);
INSERT INTO flight_ticket_class (flight_id, ticket_class_id, ticket_quantity, remaining_ticket_quantity, specified_fare, deleted_at) VALUES (101, 2, 100, 100, 2000000.00, null);
INSERT INTO flight_ticket_class (flight_id, ticket_class_id, ticket_quantity, remaining_ticket_quantity, specified_fare, deleted_at) VALUES (101, 3, 20, 20, 3000000.00, null);
