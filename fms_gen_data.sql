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
    720,    -- Thời gian chờ nối chuyến tối đa 12 giờ (720 phút)
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
('Đỗ Văn Hải', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 2, 'hai.do@fms.vn', '024791001007', '+84901234007', NULL, NULL, NULL),

-- Khách hàng (20 tài khoản)
-- Nhóm 1: Đăng ký từ 09-12/2023 (5 khách hàng)
('Bùi Minh Đức', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'duc.bui@gmail.com', '024791002001', '+84912345001', NULL, NULL, NULL),
('Võ Thị Kiều', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'kieu.vo@gmail.com', '024791002002', '+84912345002', NULL, NULL, NULL),
('Đỗ Văn Long', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'long.do@yahoo.com', '024791002003', '+84912345003', NULL, NULL, NULL),
('Nguyễn Thị Mai', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'mai.nguyen@hotmail.com', '024791002004', '+84912345004', NULL, NULL, NULL),
('Lê Văn Nam', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'nam.le@gmail.com', '024791002005', '+84912345005', NULL, NULL, NULL),

-- Nhóm 2: Đăng ký từ 01-06/2024 (7 khách hàng)
('Trần Thị Oanh', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'oanh.tran@gmail.com', '024791002006', '+84912345006', NULL, NULL, NULL),
('Phạm Văn Phúc', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'phuc.pham@outlook.com', '024791002007', '+84912345007', NULL, NULL, NULL),
('Hoàng Thị Quỳnh', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'quynh.hoang@gmail.com', '024791002008', '+84912345008', NULL, NULL, NULL),
('Bùi Văn Rạng', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'rang.bui@yahoo.com', '024791002009', '+84912345009', NULL, NULL, NULL),
('Võ Thị Sương', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'suong.vo@gmail.com', '024791002010', '+84912345010', NULL, NULL, NULL),
('Đỗ Văn Tài', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'tai.do@hotmail.com', '024791002011', '+84912345011', NULL, NULL, NULL),
('Nguyễn Thị Uyên', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'uyen.nguyen@gmail.com', '024791002012', '+84912345012', NULL, NULL, NULL),

-- Nhóm 3: Đăng ký từ 07-12/2024 (5 khách hàng)
('Lê Văn Việt', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'viet.le@outlook.com', '024791002013', '+84912345013', NULL, NULL, NULL),
('Trần Thị Xuân', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'xuan.tran@gmail.com', '024791002014', '+84912345014', NULL, NULL, NULL),
('Phạm Văn Yên', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'yen.pham@yahoo.com', '024791002015', '+84912345015', NULL, NULL, NULL),
('Hoàng Thị Zân', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'zan.hoang@gmail.com', '024791002016', '+84912345016', NULL, NULL, NULL),
('Bùi Văn Anh', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'anh.bui@hotmail.com', '024791002017', '+84912345017', NULL, NULL, NULL),

-- Nhóm 4: Đăng ký từ 01-06/2025 (3 khách hàng)
('Võ Thị Bích', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'bich.vo@gmail.com', '024791002018', '+84912345018', NULL, NULL, NULL),
('Đỗ Văn Cẩm', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'cam.do@outlook.com', '024791002019', '+84912345019', NULL, NULL, NULL),
('Nguyễn Thị Đào', '$2a$12$LQv3c1yqBwLVFgDJ1ydvnO1rHhI8h5UQjN7X8kNmW5pGkKwJmV8K2', 1, 'dao.nguyen@gmail.com', '024791002020', '+84912345020', NULL, NULL, NULL);

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

-- Dữ liệu khách hàng với hệ thống điểm mới
INSERT INTO customer (customer_id, score, deleted_at) VALUES
-- Nhóm 1: Khách hàng cũ (09-12/2023) - Chi tiêu cao do sử dụng lâu
(8, 285000, NULL),   -- Bùi Minh Đức - VIP (2.85 tỷ VND chi tiêu) - Giảm 8%
(9, 167000, NULL),   -- Võ Thị Kiều - VIP (1.67 tỷ VND chi tiêu) - Giảm 5%
(10, 78000, NULL),   -- Đỗ Văn Long - Thân thiết (780M VND) - Giảm 5%
(11, 35000, NULL),   -- Nguyễn Thị Mai - Bình thường (350M VND) - Giảm 2%
(12, 18000, NULL),   -- Lê Văn Nam - Bình thường (180M VND) - Giảm 2%

-- Nhóm 2: Khách hàng đăng ký 01-06/2024 - Chi tiêu trung bình
(13, 89000, NULL),   -- Trần Thị Oanh - Thân thiết (890M VND) - Giảm 5%
(14, 42000, NULL),   -- Phạm Văn Phúc - Bình thường (420M VND) - Giảm 2%
(15, 25000, NULL),   -- Hoàng Thị Quỳnh - Bình thường (250M VND) - Giảm 2%
(16, 95000, NULL),   -- Bùi Văn Rạng - Thân thiết (950M VND) - Giảm 5%
(17, 12000, NULL),   -- Võ Thị Sương - Bình thường (120M VND) - Giảm 2%
(18, 58000, NULL),   -- Đỗ Văn Tài - Thân thiết (580M VND) - Giảm 5%
(19, 28000, NULL),   -- Nguyễn Thị Uyên - Bình thường (280M VND) - Giảm 2%

-- Nhóm 3: Khách hàng đăng ký 07-12/2024 - Chi tiêu thấp hơn (mới tham gia)
(20, 8500, NULL),    -- Lê Văn Việt - Mới (85M VND) - Không giảm
(21, 15000, NULL),   -- Trần Thị Xuân - Bình thường (150M VND) - Giảm 2%
(22, 32000, NULL),   -- Phạm Văn Yên - Bình thường (320M VND) - Giảm 2%
(23, 6200, NULL),    -- Hoàng Thị Zân - Mới (62M VND) - Không giảm
(24, 11500, NULL),   -- Bùi Văn Anh - Bình thường (115M VND) - Giảm 2%

-- Nhóm 4: Khách hàng mới nhất (01-06/2025) - Chi tiêu thấp (vừa đăng ký)
(25, 2800, NULL),    -- Võ Thị Bích - Mới (28M VND) - Không giảm
(26, 4500, NULL),    -- Đỗ Văn Cẩm - Mới (45M VND) - Không giảm
(27, 7200, NULL);    -- Nguyễn Thị Đào - Mới (72M VND) - Không giảm

-- Dữ liệu hành khách mở rộng (200 người)
INSERT INTO passenger (passenger_name, email, citizen_id, phone_number, deleted_at) VALUES
-- Nhóm 1: Các khách hàng đã đăng ký (20 người - trùng với bảng customer)
('Bùi Minh Đức', 'duc.bui@gmail.com', '024791002001', '+84912345001', NULL),
('Võ Thị Kiều', 'kieu.vo@gmail.com', '024791002002', '+84912345002', NULL),
('Đỗ Văn Long', 'long.do@yahoo.com', '024791002003', '+84912345003', NULL),
('Nguyễn Thị Mai', 'mai.nguyen@hotmail.com', '024791002004', '+84912345004', NULL),
('Lê Văn Nam', 'nam.le@gmail.com', '024791002005', '+84912345005', NULL),
('Trần Thị Oanh', 'oanh.tran@gmail.com', '024791002006', '+84912345006', NULL),
('Phạm Văn Phúc', 'phuc.pham@outlook.com', '024791002007', '+84912345007', NULL),
('Hoàng Thị Quỳnh', 'quynh.hoang@gmail.com', '024791002008', '+84912345008', NULL),
('Bùi Văn Rạng', 'rang.bui@yahoo.com', '024791002009', '+84912345009', NULL),
('Võ Thị Sương', 'suong.vo@gmail.com', '024791002010', '+84912345010', NULL),
('Đỗ Văn Tài', 'tai.do@hotmail.com', '024791002011', '+84912345011', NULL),
('Nguyễn Thị Uyên', 'uyen.nguyen@gmail.com', '024791002012', '+84912345012', NULL),
('Lê Văn Việt', 'viet.le@outlook.com', '024791002013', '+84912345013', NULL),
('Trần Thị Xuân', 'xuan.tran@gmail.com', '024791002014', '+84912345014', NULL),
('Phạm Văn Yên', 'yen.pham@yahoo.com', '024791002015', '+84912345015', NULL),
('Hoàng Thị Zân', 'zan.hoang@gmail.com', '024791002016', '+84912345016', NULL),
('Bùi Văn Anh', 'anh.bui@hotmail.com', '024791002017', '+84912345017', NULL),
('Võ Thị Bích', 'bich.vo@gmail.com', '024791002018', '+84912345018', NULL),
('Đỗ Văn Cẩm', 'cam.do@outlook.com', '024791002019', '+84912345019', NULL),
('Nguyễn Thị Đào', 'dao.nguyen@gmail.com', '024791002020', '+84912345020', NULL),

-- Nhóm 2: Thành viên gia đình của khách hàng (40 người)
-- Gia đình khách VIP và thân thiết
('Lê Thị Hoa', 'hoa.le.family@gmail.com', '024791003001', '+84933456001', NULL), -- Vợ Bùi Minh Đức
('Bùi Minh Tuấn', 'tuan.bui.son@gmail.com', '024791003002', '+84933456002', NULL), -- Con trai
('Bùi Thị Linh', 'linh.bui.daughter@gmail.com', '024791003003', '+84933456003', NULL), -- Con gái
('Lê Văn Phong', 'phong.le.father@gmail.com', '024791003004', '+84933456004', NULL), -- Bố vợ
('Lê Thị Lan', 'lan.le.mother@gmail.com', '024791003005', '+84933456005', NULL), -- Mẹ vợ

('Nguyễn Văn Hùng', 'hung.nguyen.family@yahoo.com', '024791003006', '+84933456006', NULL), -- Chồng Võ Thị Kiều
('Nguyễn Thị Lan', 'lan.nguyen.daughter@gmail.com', '024791003007', '+84933456007', NULL), -- Con gái
('Nguyễn Văn Minh', 'minh.nguyen.son@yahoo.com', '024791003008', '+84933456008', NULL), -- Con trai
('Võ Văn Thành', 'thanh.vo.father@gmail.com', '024791003009', '+84933456009', NULL), -- Bố

('Trần Thị Minh', 'minh.tran.family@hotmail.com', '024791003010', '+84933456010', NULL), -- Vợ Đỗ Văn Long
('Đỗ Văn Khải', 'khai.do.son@gmail.com', '024791003011', '+84933456011', NULL), -- Con trai
('Đỗ Thị Hương', 'huong.do.daughter@hotmail.com', '024791003012', '+84933456012', NULL), -- Con gái

('Lý Thị Nga', 'nga.ly.family@outlook.com', '024791003013', '+84933456013', NULL), -- Vợ Bùi Văn Rạng
('Bùi Thị Thu', 'thu.bui.daughter@gmail.com', '024791003014', '+84933456014', NULL), -- Con gái
('Bùi Văn Đức', 'duc.bui.son@outlook.com', '024791003015', '+84933456015', NULL), -- Con trai

('Phan Văn Đức', 'duc.phan.family@gmail.com', '024791003016', '+84933456016', NULL), -- Chồng Trần Thị Oanh
('Phan Thị Hương', 'huong.phan.daughter@yahoo.com', '024791003017', '+84933456017', NULL), -- Con gái
('Phan Văn Thắng', 'thang.phan.son@gmail.com', '024791003018', '+84933456018', NULL), -- Con trai

-- Thành viên gia đình mở rộng của các khách hàng khác
('Vũ Văn Thành', 'thanh.vu.relative@gmail.com', '024791003019', '+84933456019', NULL),
('Đinh Thị Phương', 'phuong.dinh.relative@hotmail.com', '024791003020', '+84933456020', NULL),
('Cao Văn Đạt', 'dat.cao.relative@outlook.com', '024791003021', '+84933456021', NULL),
('Lưu Thị Vy', 'vy.luu.relative@gmail.com', '024791003022', '+84933456022', NULL),
('Đặng Văn Toàn', 'toan.dang.relative@yahoo.com', '024791003023', '+84933456023', NULL),
('Hoàng Thị Sen', 'sen.hoang.relative@gmail.com', '024791003024', '+84933456024', NULL),
('Trịnh Văn Bảo', 'bao.trinh.relative@hotmail.com', '024791003025', '+84933456025', NULL),
('Phùng Thị Diệu', 'dieu.phung.relative@outlook.com', '024791003026', '+84933456026', NULL),
('Lã Văn Kiên', 'kien.la.relative@gmail.com', '024791003027', '+84933456027', NULL),
('Mai Thị Loan', 'loan.mai.relative@yahoo.com', '024791003028', '+84933456028', NULL),
('Chu Văn Đông', 'dong.chu.relative@gmail.com', '024791003029', '+84933456029', NULL),
('Tô Thị Nga', 'nga.to.relative@hotmail.com', '024791003030', '+84933456030', NULL),
('Dương Văn Phát', 'phat.duong.relative@outlook.com', '024791003031', '+84933456031', NULL),
('Lâm Thị Yến', 'yen.lam.relative@gmail.com', '024791003032', '+84933456032', NULL),
('Từ Văn Hải', 'hai.tu.relative@yahoo.com', '024791003033', '+84933456033', NULL),
('Tạ Thị Hồng', 'hong.ta.relative@gmail.com', '024791003034', '+84933456034', NULL),
('Ký Văn Lâm', 'lam.ky.relative@hotmail.com', '024791003035', '+84933456035', NULL),
('Bạch Thị Mai', 'mai.bach.relative@outlook.com', '024791003036', '+84933456036', NULL),
('Ông Văn Nam', 'nam.ong.relative@gmail.com', '024791003037', '+84933456037', NULL),
('Âu Thị Oanh', 'oanh.au.relative@yahoo.com', '024791003038', '+84933456038', NULL),
('Úc Văn Phúc', 'phuc.uc.relative@gmail.com', '024791003039', '+84933456039', NULL),
('Ý Thị Quỳnh', 'quynh.y.relative@hotmail.com', '024791003040', '+84933456040', NULL),

-- Nhóm 3: Khách quốc tế (30 người)
-- Khách Mỹ
('Smith John', 'john.smith@tourism.com', 'P123456789', '+1234567890', NULL),
('Johnson Mary', 'mary.johnson@business.com', 'P234567890', '+1234567891', NULL),
('Williams Robert', 'robert.williams@travel.com', 'P345678901', '+1234567892', NULL),
('Brown Jennifer', 'jennifer.brown@corp.com', 'P456789012', '+1234567893', NULL),
('Davis Michael', 'michael.davis@leisure.com', 'P567890123', '+1234567894', NULL),

-- Khách Anh
('Wilson James', 'james.wilson@business.co.uk', 'P987654321', '+44123456789', NULL),
('Taylor Emma', 'emma.taylor@tourism.co.uk', 'P876543210', '+44123456788', NULL),
('Anderson Oliver', 'oliver.anderson@travel.co.uk', 'P765432109', '+44123456787', NULL),
('Thomas Sophie', 'sophie.thomas@corp.co.uk', 'P654321098', '+44123456786', NULL),
('Jackson Isabella', 'isabella.jackson@leisure.co.uk', 'P543210987', '+44123456785', NULL),

-- Khách Hàn Quốc
('Kim Min-jun', 'minjun.kim@korea.kr', 'K456789123', '+82101234567', NULL),
('Lee Seo-yeon', 'seoyeon.lee@korea.kr', 'K567890234', '+82101234568', NULL),
('Park Ji-hoon', 'jihoon.park@business.kr', 'K678901345', '+82101234569', NULL),
('Choi Yu-jin', 'yujin.choi@travel.kr', 'K789012456', '+82101234570', NULL),
('Jung Do-yoon', 'doyoon.jung@corp.kr', 'K890123567', '+82101234571', NULL),

-- Khách Nhật Bản
('Tanaka Hiroshi', 'hiroshi.tanaka@japan.jp', 'J789123456', '+81901234567', NULL),
('Suzuki Yuki', 'yuki.suzuki@business.jp', 'J890234567', '+81901234568', NULL),
('Watanabe Kenji', 'kenji.watanabe@travel.jp', 'J901345678', '+81901234569', NULL),
('Ito Akiko', 'akiko.ito@corp.jp', 'J012456789', '+81901234570', NULL),
('Yamamoto Takeshi', 'takeshi.yamamoto@leisure.jp', 'J123567890', '+81901234571', NULL),

-- Khách Trung Quốc
('Wang Li', 'li.wang@china.cn', 'C321654987', '+86123456789', NULL),
('Zhang Wei', 'wei.zhang@business.cn', 'C432765098', '+86123456788', NULL),
('Liu Xiao', 'xiao.liu@travel.cn', 'C543876109', '+86123456787', NULL),
('Chen Ming', 'ming.chen@corp.cn', 'C654987210', '+86123456786', NULL),
('Yang Fang', 'fang.yang@leisure.cn', 'C765098321', '+86123456785', NULL),

-- Khách các nước khác
('Singh Raj', 'raj.singh@india.in', 'I123456789', '+91987654321', NULL), -- Ấn Độ
('Mueller Hans', 'hans.mueller@germany.de', 'D987654321', '+49123456789', NULL), -- Đức
('Dupont Marie', 'marie.dupont@france.fr', 'F456789123', '+33123456789', NULL), -- Pháp
('Rossi Marco', 'marco.rossi@italy.it', 'I789123456', '+39123456789', NULL), -- Ý
('Silva Carlos', 'carlos.silva@brazil.br', 'B321654987', '+5511987654321', NULL), -- Brazil

-- Nhóm 4: Khách nội địa không có tài khoản (110 người)
-- Khách miền Bắc (40 người)
('Nguyễn Văn Hải', 'hai.nguyen.guest@gmail.com', '024791004001', '+84944567001', NULL),
('Trần Thị Liên', 'lien.tran.guest@yahoo.com', '024791004002', '+84944567002', NULL),
('Lê Văn Minh', 'minh.le.guest@hotmail.com', '024791004003', '+84944567003', NULL),
('Phạm Thị Oanh', 'oanh.pham.guest@outlook.com', '024791004004', '+84944567004', NULL),
('Hoàng Văn Phúc', 'phuc.hoang.guest@gmail.com', '024791004005', '+84944567005', NULL),
('Bùi Thị Quỳnh', 'quynh.bui.guest@yahoo.com', '024791004006', '+84944567006', NULL),
('Võ Văn Rạng', 'rang.vo.guest@hotmail.com', '024791004007', '+84944567007', NULL),
('Đỗ Thị Sương', 'suong.do.guest@gmail.com', '024791004008', '+84944567008', NULL),
('Nguyễn Văn Tài', 'tai.nguyen.guest@outlook.com', '024791004009', '+84944567009', NULL),
('Lê Thị Uyên', 'uyen.le.guest@yahoo.com', '024791004010', '+84944567010', NULL),
('Trần Văn Việt', 'viet.tran.guest@gmail.com', '024791004011', '+84944567011', NULL),
('Phạm Thị Xuân', 'xuan.pham.guest@hotmail.com', '024791004012', '+84944567012', NULL),
('Hoàng Văn Yên', 'yen.hoang.guest@outlook.com', '024791004013', '+84944567013', NULL),
('Bùi Thị Zân', 'zan.bui.guest@gmail.com', '024791004014', '+84944567014', NULL),
('Võ Văn An', 'an.vo.guest@yahoo.com', '024791004015', '+84944567015', NULL),
('Đỗ Thị Bình', 'binh.do.guest@hotmail.com', '024791004016', '+84944567016', NULL),
('Nguyễn Văn Cường', 'cuong.nguyen.guest@gmail.com', '024791004017', '+84944567017', NULL),
('Lê Thị Dung', 'dung.le.guest@outlook.com', '024791004018', '+84944567018', NULL),
('Trần Văn Em', 'em.tran.guest@yahoo.com', '024791004019', '+84944567019', NULL),
('Phạm Thị Giang', 'giang.pham.guest@gmail.com', '024791004020', '+84944567020', NULL),
('Hoàng Văn Hải', 'hai.hoang.hn.guest@hotmail.com', '024791004021', '+84944567021', NULL),
('Bùi Thị Inh', 'inh.bui.guest@outlook.com', '024791004022', '+84944567022', NULL),
('Võ Văn Khanh', 'khanh.vo.guest@gmail.com', '024791004023', '+84944567023', NULL),
('Đỗ Thị Linh', 'linh.do.hn.guest@yahoo.com', '024791004024', '+84944567024', NULL),
('Nguyễn Văn Minh', 'minh.nguyen.hn.guest@hotmail.com', '024791004025', '+84944567025', NULL),
('Lê Thị Ngọc', 'ngoc.le.guest@gmail.com', '024791004026', '+84944567026', NULL),
('Trần Văn Ông', 'ong.tran.guest@outlook.com', '024791004027', '+84944567027', NULL),
('Phạm Thị Phương', 'phuong.pham.hn.guest@yahoo.com', '024791004028', '+84944567028', NULL),
('Hoàng Văn Quang', 'quang.hoang.guest@gmail.com', '024791004029', '+84944567029', NULL),
('Bùi Thị Rừng', 'rung.bui.guest@hotmail.com', '024791004030', '+84944567030', NULL),
('Võ Văn Sơn', 'son.vo.guest@outlook.com', '024791004031', '+84944567031', NULL),
('Đỗ Thị Tuyết', 'tuyet.do.guest@gmail.com', '024791004032', '+84944567032', NULL),
('Nguyễn Văn Uy', 'uy.nguyen.guest@yahoo.com', '024791004033', '+84944567033', NULL),
('Lê Thị Vân', 'van.le.guest@hotmail.com', '024791004034', '+84944567034', NULL),
('Trần Văn Wuân', 'wuan.tran.guest@gmail.com', '024791004035', '+84944567035', NULL),
('Phạm Thị Xuyến', 'xuyen.pham.guest@outlook.com', '024791004036', '+84944567036', NULL),
('Hoàng Văn Yêu', 'yeu.hoang.guest@yahoo.com', '024791004037', '+84944567037', NULL),
('Bùi Thị Zuy', 'zuy.bui.guest@gmail.com', '024791004038', '+84944567038', NULL),
('Võ Văn Ánh', 'anh.vo.hn.guest@hotmail.com', '024791004039', '+84944567039', NULL),
('Đỗ Thị Ế', 'e.do.guest@outlook.com', '024791004040', '+84944567040', NULL),

-- Khách miền Trung (35 người)
('Nguyễn Văn Bảo', 'bao.nguyen.dn.guest@gmail.com', '049791005001', '+84935678001', NULL),
('Trần Thị Cẩm', 'cam.tran.dn.guest@yahoo.com', '049791005002', '+84935678002', NULL),
('Lê Văn Đạt', 'dat.le.dn.guest@hotmail.com', '049791005003', '+84935678003', NULL),
('Phạm Thị Em', 'em.pham.dn.guest@outlook.com', '049791005004', '+84935678004', NULL),
('Hoàng Văn Phong', 'phong.hoang.dn.guest@gmail.com', '049791005005', '+84935678005', NULL),
('Bùi Thị Giang', 'giang.bui.dn.guest@yahoo.com', '049791005006', '+84935678006', NULL),
('Võ Văn Hạnh', 'hanh.vo.dn.guest@hotmail.com', '049791005007', '+84935678007', NULL),
('Đỗ Thị Ích', 'ich.do.dn.guest@gmail.com', '049791005008', '+84935678008', NULL),
('Nguyễn Văn Khang', 'khang.nguyen.dn.guest@outlook.com', '049791005009', '+84935678009', NULL),
('Lê Thị Lệ', 'le.le.dn.guest@yahoo.com', '049791005010', '+84935678010', NULL),
('Trần Văn Mạnh', 'manh.tran.dn.guest@gmail.com', '049791005011', '+84935678011', NULL),
('Phạm Thị Ninh', 'ninh.pham.dn.guest@hotmail.com', '049791005012', '+84935678012', NULL),
('Hoàng Văn Ổn', 'on.hoang.dn.guest@outlook.com', '049791005013', '+84935678013', NULL),
('Bùi Thị Phấn', 'phan.bui.dn.guest@gmail.com', '049791005014', '+84935678014', NULL),
('Võ Văn Quý', 'quy.vo.dn.guest@yahoo.com', '049791005015', '+84935678015', NULL),
('Đỗ Thị Riêng', 'rieng.do.dn.guest@hotmail.com', '049791005016', '+84935678016', NULL),
('Nguyễn Văn Sáng', 'sang.nguyen.dn.guest@gmail.com', '049791005017', '+84935678017', NULL),
('Lê Thị Tịnh', 'tinh.le.dn.guest@outlook.com', '049791005018', '+84935678018', NULL),
('Trần Văn Ứng', 'ung.tran.dn.guest@yahoo.com', '049791005019', '+84935678019', NULL),
('Phạm Thị Vui', 'vui.pham.dn.guest@gmail.com', '049791005020', '+84935678020', NULL),
('Hoàng Văn Xuân', 'xuan.hoang.dn.guest@hotmail.com', '049791005021', '+84935678021', NULL),
('Bùi Thị Yêu', 'yeu.bui.dn.guest@outlook.com', '049791005022', '+84935678022', NULL),
('Võ Văn Zin', 'zin.vo.dn.guest@gmail.com', '049791005023', '+84935678023', NULL),
('Đỗ Thị Ăn', 'an.do.dn.guest@yahoo.com', '049791005024', '+84935678024', NULL),
('Nguyễn Văn Êm', 'em.nguyen.dn.guest@hotmail.com', '049791005025', '+84935678025', NULL),
('Lê Thị Ỉm', 'im.le.dn.guest@gmail.com', '049791005026', '+84935678026', NULL),
('Trần Văn Ôn', 'on.tran.dn.guest@outlook.com', '049791005027', '+84935678027', NULL),
('Phạm Thị Ưng', 'ung.pham.dn.guest@yahoo.com', '049791005028', '+84935678028', NULL),
('Hoàng Văn Ưu', 'uu.hoang.dn.guest@gmail.com', '049791005029', '+84935678029', NULL),
('Bùi Thị Ạ', 'a.bui.dn.guest@hotmail.com', '049791005030', '+84935678030', NULL),
('Võ Văn Ậm', 'am.vo.dn.guest@outlook.com', '049791005031', '+84935678031', NULL),
('Đỗ Thị Ặc', 'ac.do.dn.guest@gmail.com', '049791005032', '+84935678032', NULL),
('Nguyễn Văn Ằng', 'ang.nguyen.dn.guest@yahoo.com', '049791005033', '+84935678033', NULL),
('Lê Thị Ắt', 'at.le.dn.guest@hotmail.com', '049791005034', '+84935678034', NULL),
('Trần Văn Ẳng', 'ang.tran.dn.guest@gmail.com', '049791005035', '+84935678035', NULL),

-- Khách miền Nam (35 người)
('Nguyễn Văn Cần', 'can.nguyen.hcm.guest@gmail.com', '079791006001', '+84926789001', NULL),
('Trần Thị Dư', 'du.tran.hcm.guest@yahoo.com', '079791006002', '+84926789002', NULL),
('Lê Văn Giàu', 'giau.le.hcm.guest@hotmail.com', '079791006003', '+84926789003', NULL),
('Phạm Thị Hiền', 'hien.pham.hcm.guest@outlook.com', '079791006004', '+84926789004', NULL),
('Hoàng Văn Kỳ', 'ky.hoang.hcm.guest@gmail.com', '079791006005', '+84926789005', NULL),
('Bùi Thị Lạc', 'lac.bui.hcm.guest@yahoo.com', '079791006006', '+84926789006', NULL),
('Võ Văn Mười', 'muoi.vo.hcm.guest@hotmail.com', '079791006007', '+84926789007', NULL),
('Đỗ Thị Năm', 'nam.do.hcm.guest@gmail.com', '079791006008', '+84926789008', NULL),
('Nguyễn Văn Ót', 'ot.nguyen.hcm.guest@outlook.com', '079791006009', '+84926789009', NULL),
('Lê Thị Phiến', 'phien.le.hcm.guest@yahoo.com', '079791006010', '+84926789010', NULL),
('Trần Văn Quít', 'quit.tran.hcm.guest@gmail.com', '079791006011', '+84926789011', NULL),
('Phạm Thị Ri', 'ri.pham.hcm.guest@hotmail.com', '079791006012', '+84926789012', NULL),
('Hoàng Văn Sáu', 'sau.hoang.hcm.guest@outlook.com', '079791006013', '+84926789013', NULL),
('Bùi Thị Tám', 'tam.bui.hcm.guest@gmail.com', '079791006014', '+84926789014', NULL),
('Võ Văn Út', 'ut.vo.hcm.guest@yahoo.com', '079791006015', '+84926789015', NULL),
('Đỗ Thị Vàng', 'vang.do.hcm.guest@hotmail.com', '079791006016', '+84926789016', NULL),
('Nguyễn Văn Xiêm', 'xiem.nguyen.hcm.guest@gmail.com', '079791006017', '+84926789017', NULL),
('Lê Thị Ỷ', 'y.le.hcm.guest@outlook.com', '079791006018', '+84926789018', NULL),
('Trần Văn Zích', 'zich.tran.hcm.guest@yahoo.com', '079791006019', '+84926789019', NULL),
('Phạm Thị Ẻo', 'eo.pham.hcm.guest@gmail.com', '079791006020', '+84926789020', NULL),
('Hoàng Văn Ỉa', 'ia.hoang.hcm.guest@hotmail.com', '079791006021', '+84926789021', NULL),
('Bùi Thị Òa', 'oa.bui.hcm.guest@outlook.com', '079791006022', '+84926789022', NULL),
('Võ Văn Ủa', 'ua.vo.hcm.guest@gmail.com', '079791006023', '+84926789023', NULL),
('Đỗ Thị Ạch', 'ach.do.hcm.guest@yahoo.com', '079791006024', '+84926789024', NULL),
('Nguyễn Văn Ặm', 'am.nguyen.hcm.guest@hotmail.com', '079791006025', '+84926789025', NULL),
('Lê Thị Ẳn', 'an.le.hcm.guest@gmail.com', '079791006026', '+84926789026', NULL),
('Trần Văn Ẵng', 'ang.tran.hcm.guest@outlook.com', '079791006027', '+84926789027', NULL),
('Phạm Thị Ặp', 'ap.pham.hcm.guest@yahoo.com', '079791006028', '+84926789028', NULL),
('Hoàng Văn Ắc', 'ac.hoang.hcm.guest@gmail.com', '079791006029', '+84926789029', NULL),
('Bùi Thị Ặt', 'at.bui.hcm.guest@hotmail.com', '079791006030', '+84926789030', NULL),
('Võ Văn Ẩu', 'au.vo.hcm.guest@outlook.com', '079791006031', '+84926789031', NULL),
('Đỗ Thị Ẫy', 'ay.do.hcm.guest@gmail.com', '079791006032', '+84926789032', NULL),
('Nguyễn Văn Ầm', 'am.nguyen.tg.guest@yahoo.com', '079791006033', '+84926789033', NULL),
('Lê Thị Ẩn', 'an.le.tg.guest@hotmail.com', '079791006034', '+84926789034', NULL),
('Trần Văn Ẫng', 'ang.tran.tg.guest@gmail.com', '079791006035', '+84926789035', NULL);

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
('Khách Z', 'password123', 1, 'khach.z@email.com', '001000000024', '0900000024');

-- Generate corresponding customer records
INSERT INTO customer (customer_id, score) 
SELECT account_id, 0 FROM `account` WHERE account_type = 1 AND account_name LIKE 'Khách %';

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