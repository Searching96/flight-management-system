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
        SET flight_code = CONCAT('VN', LPAD(MOD(new_flight_id, 1000), 3, '0'))
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

INSERT INTO chatbox (customer_id, deleted_at) VALUES
-- January 2025 - Week 1
(15, NULL),  -- Chatbox 1: Vấn đề booking
(22, NULL),  -- Chatbox 2: Hủy chuyến bay
(8, NULL),   -- Chatbox 3: Thay đổi thông tin

-- January 2025 - Week 2  
(45, NULL),  -- Chatbox 4: Vấn đề thanh toán
(33, NULL),  -- Chatbox 5: Baggage inquiry
(12, NULL),  -- Chatbox 6: Seat selection

-- January 2025 - Week 3
(67, NULL),  -- Chatbox 7: Refund request
(29, NULL),  -- Chatbox 8: Flight delay

-- January 2025 - Week 4
(51, NULL),  -- Chatbox 9: Upgrade request
(18, NULL),  -- Chatbox 10: Special meal
(74, NULL),  -- Chatbox 11: Travel documents

-- February 2025 - Week 1
(36, NULL),  -- Chatbox 12: Tết booking
(88, NULL),  -- Chatbox 13: Group booking

-- February 2025 - Week 2
(41, NULL),  -- Chatbox 14: Cancel Tết flight
(56, NULL),  -- Chatbox 15: Rebook after cancel

-- February 2025 - Week 3
(23, NULL),  -- Chatbox 16: Lost booking
(69, NULL),  -- Chatbox 17: Name correction

-- February 2025 - Week 4
(47, NULL),  -- Chatbox 18: Miles inquiry
(14, NULL),  -- Chatbox 19: App login issue

-- March 2025 - Week 1
(82, NULL),  -- Chatbox 20: International booking
(35, NULL),  -- Chatbox 21: Visa requirements

-- March 2025 - Week 2
(59, NULL),  -- Chatbox 22: Flight change
(26, NULL),  -- Chatbox 23: Compensation claim

-- March 2025 - Week 3
(73, NULL),  -- Chatbox 24: Pet travel
(11, NULL),  -- Chatbox 25: Wheelchair service

-- March 2025 - Week 4
(48, NULL),  -- Chatbox 26: Bulk booking
(61, NULL),  -- Chatbox 27: Payment failed

-- April 2025 - Week 1
(37, NULL),  -- Chatbox 28: Schedule change
(84, NULL),  -- Chatbox 29: Lounge access

-- April 2025 - Week 2
(19, NULL),  -- Chatbox 30: Booking modification
(52, NULL),  -- Chatbox 31: Extra baggage

-- April 2025 - Week 3
(75, NULL),  -- Chatbox 32: Medical clearance
(28, NULL),  -- Chatbox 33: Child travel

-- April 2025 - Week 4
(63, NULL),  -- Chatbox 34: Corporate booking
(16, NULL),  -- Chatbox 35: Technical issue

-- May 2025 - Week 1
(71, NULL),  -- Chatbox 36: Summer vacation booking
(44, NULL),  -- Chatbox 37: Seat map issue

-- May 2025 - Week 2
(58, NULL),  -- Chatbox 38: Frequent flyer
(31, NULL),  -- Chatbox 39: Group discount

-- May 2025 - Week 3
(85, NULL),  -- Chatbox 40: International connection
(17, NULL),  -- Chatbox 41: Boarding pass

-- May 2025 - Week 4
(49, NULL),  -- Chatbox 42: Weather delay
(66, NULL),  -- Chatbox 43: Rebooking

-- June 2025 - Week 1
(38, NULL),  -- Chatbox 44: Summer peak booking
(77, NULL),  -- Chatbox 45: Priority boarding

-- June 2025 - Week 2 (Current week)
(24, NULL),  -- Chatbox 46: Current issue
(53, NULL);  -- Chatbox 47: Active chat

-- ============= BẢNG MESSAGE =============

INSERT INTO message (chatbox_id, employee_id, content, send_time, deleted_at) VALUES

-- ============= CHATBOX 1 (Customer 15) - Vấn đề booking =============
(1, NULL, 'Xin chào, tôi gặp vấn đề khi booking vé máy bay trên website', '2025-01-03 09:15:00', NULL),
(1, 5, 'Chào anh/chị! Tôi là Minh từ bộ phận hỗ trợ. Anh/chị có thể cho tôi biết cụ thể vấn đề gì không?', '2025-01-03 09:16:30', NULL),
(1, NULL, 'Tôi đã chọn chuyến bay HAN-SGN ngày 15/1 nhưng không thể thanh toán được', '2025-01-03 09:18:00', NULL),
(1, 5, 'Anh/chị có thể thử làm mới trang và thử lại không? Hoặc thử với trình duyệt khác?', '2025-01-03 09:19:15', NULL),
(1, NULL, 'Đã thử rồi nhưng vẫn báo lỗi "Payment gateway error"', '2025-01-03 09:21:30', NULL),
(1, 5, 'Vậy để tôi hỗ trợ anh/chị booking trực tiếp. Anh/chị cho tôi thông tin: Họ tên, số điện thoại, ngày bay mong muốn', '2025-01-03 09:23:00', NULL),
(1, NULL, 'Nguyễn Văn A, 0901234567, chuyến HAN-SGN 15/1 lúc 14:00', '2025-01-03 09:25:00', NULL),
(1, 5, 'Tôi đã check, chuyến bay VN103 còn chỗ. Giá vé Economy 2.500.000đ. Anh/chị xác nhận booking không?', '2025-01-03 09:27:30', NULL),
(1, NULL, 'Vâng, tôi xác nhận. Thanh toán như thế nào?', '2025-01-03 09:29:00', NULL),
(1, 5, 'Tôi sẽ tạo booking và gửi link thanh toán qua SMS. Booking code: VN123456. Cảm ơn anh/chị!', '2025-01-03 09:31:00', NULL),
(1, NULL, 'Cảm ơn bạn hỗ trợ nhiệt tình!', '2025-01-03 09:32:30', NULL),

-- ============= CHATBOX 2 (Customer 22) - Hủy chuyến bay =============
(2, NULL, 'Tôi cần hủy vé máy bay đã booking', '2025-01-05 14:20:00', NULL),
(2, 7, 'Chào anh/chị! Tôi là Linh. Anh/chị cho tôi booking code để tôi check thông tin', '2025-01-05 14:21:00', NULL),
(2, NULL, 'Booking code: VN789012, chuyến SGN-HAN ngày 20/1', '2025-01-05 14:22:30', NULL),
(2, 7, 'Tôi đã tìm thấy booking. Vé loại Economy Flex, có thể hủy với phí 500.000đ. Anh/chị có muốn tiếp tục?', '2025-01-05 14:24:00', NULL),
(2, NULL, 'Phí hủy 500k à? Tôi có thể đổi ngày được không?', '2025-01-05 14:25:30', NULL),
(2, 7, 'Đổi ngày phí chỉ 300.000đ thôi ạ. Anh/chị muốn đổi sang ngày nào?', '2025-01-05 14:26:30', NULL),
(2, NULL, 'Vậy tôi đổi sang ngày 25/1 cùng giờ được không?', '2025-01-05 14:28:00', NULL),
(2, 7, 'Ngày 25/1 chuyến VN104 18:00 còn chỗ. Tôi thực hiện đổi ngay. Phí đổi 300k được trừ vào thẻ ban đầu', '2025-01-05 14:29:30', NULL),
(2, NULL, 'OK, cảm ơn bạn. Khi nào có vé mới?', '2025-01-05 14:30:30', NULL),
(2, 7, 'Vé mới đã được gửi qua email. Booking code mới: VN789013. Chúc anh/chị có chuyến bay tốt!', '2025-01-05 14:32:00', NULL),

-- ============= CHATBOX 3 (Customer 8) - Thay đổi thông tin =============
(3, NULL, 'Tôi booking nhầm tên, cần sửa lại', '2025-01-07 16:45:00', NULL),
(3, 9, 'Chào anh/chị! Tôi là Hùng. Anh/chị cung cấp booking code và tên đúng cần sửa', '2025-01-07 16:46:00', NULL),
(3, NULL, 'Code: VN345678. Tên sai: "Nguyen Van B", tên đúng: "Nguyen Van Binh"', '2025-01-07 16:47:30', NULL),
(3, 9, 'Tôi thấy booking rồi. Sửa tên từ "Nguyen Van B" thành "Nguyen Van Binh". Phí sửa tên 200.000đ', '2025-01-07 16:48:30', NULL),
(3, NULL, 'Sao phải mất phí vậy? Chỉ thiếu chữ "inh" thôi mà', '2025-01-07 16:49:30', NULL),
(3, 9, 'Theo quy định airline, mọi thay đổi tên đều có phí ạ. Tuy nhiên tôi sẽ escalate để xem có thể miễn phí không', '2025-01-07 16:51:00', NULL),
(3, NULL, 'Cảm ơn bạn. Tôi đợi phản hồi', '2025-01-07 16:52:00', NULL),
(3, 9, 'Supervisor đã approve miễn phí vì chỉ là lỗi typo nhỏ. Tôi sửa ngay. Vé mới sẽ được gửi lại email', '2025-01-07 16:55:00', NULL),
(3, NULL, 'Tuyệt vời! Cảm ơn team support rất nhiều', '2025-01-07 16:56:00', NULL),
(3, 9, 'Không có gì ạ! Chúc anh/chị có chuyến bay tốt. Cần hỗ trợ gì khác cứ liên hệ nhé', '2025-01-07 16:57:00', NULL),

-- ============= CHATBOX 12 (Customer 36) - Tết booking =============
(12, NULL, 'Tôi muốn book vé Tết từ HAN về Huế', '2025-02-01 10:30:00', NULL),
(12, 5, 'Chào anh/chị! Tết năm nay từ 26/1-2/2. Anh/chị muốn bay ngày nào ạ?', '2025-02-01 10:31:00', NULL),
(12, NULL, 'Tôi muốn bay 28/1 về và 2/2 lên. Còn vé không?', '2025-02-01 10:32:30', NULL),
(12, 5, 'HAN-HUI ngày 28/1 còn vài chỗ Economy, giá 4.2 triệu. HUI-HAN 2/2 còn Business 8.5 triệu, Economy hết rồi', '2025-02-01 10:34:00', NULL),
(12, NULL, 'Sao giá cao thế? Bình thường chỉ 1.8 triệu thôi', '2025-02-01 10:35:00', NULL),
(12, 5, 'Đúng rồi ạ, nhưng Tết là peak season nên giá cao gấp 2-3 lần. Anh/chị có muốn book không?', '2025-02-01 10:36:30', NULL),
(12, NULL, 'Chiều về Business 8.5tr quá đắt. Có cách nào không?', '2025-02-01 10:37:30', NULL),
(12, 5, 'Anh/chị có thể bay HUI-DAD-HAN, transit 2h ở Đà Nẵng, giá Economy 5.8 triệu', '2025-02-01 10:39:00', NULL),
(12, NULL, 'Được rồi, tôi book cả 2 chuyến. Tổng bao nhiêu?', '2025-02-01 10:40:00', NULL),
(12, 5, 'Tổng 10 triệu (4.2tr + 5.8tr). Tôi tạo booking ngay. Cần thông tin: họ tên, CCCD, SĐT', '2025-02-01 10:41:00', NULL),
(12, NULL, 'Trần Thị C, 024567891234, 0912345678', '2025-02-01 10:42:30', NULL),
(12, 5, 'Booking hoàn tất! Code: VN888888. Link thanh toán đã gửi SMS. Thanh toán trong 24h nhé', '2025-02-01 10:44:00', NULL),

-- ============= CHATBOX 46 (Customer 24) - Current issue =============
(46, NULL, 'Chào, tôi đang có vấn đề với app VietnamAir', '2025-06-11 15:30:00', NULL),
(46, 12, 'Chào anh/chị! Tôi là Nam từ IT support. Anh/chị gặp vấn đề gì với app?', '2025-06-11 15:31:00', NULL),
(46, NULL, 'App cứ bị crash khi tôi cố check-in online', '2025-06-11 15:32:00', NULL),
(46, 12, 'Anh/chị dùng iOS hay Android? Và app version bao nhiêu?', '2025-06-11 15:33:00', NULL),
(46, NULL, 'iPhone 13, app version 3.2.1', '2025-06-11 15:34:00', NULL),
(46, 12, 'App đã outdated rồi ạ. Anh/chị update lên version 3.4.2 trên App Store nhé', '2025-06-11 15:35:30', NULL),
(46, NULL, 'Để tôi update xem... OK done. Thử lại được rồi!', '2025-06-11 15:38:00', NULL),
(46, 12, 'Tuyệt! Anh/chị đã check-in thành công chưa?', '2025-06-11 15:39:00', NULL),
(46, NULL, 'Rồi, boarding pass đã có. Cảm ơn bạn support nhanh!', '2025-06-11 15:40:00', NULL),
(46, 12, 'Vui lòng update app thường xuyên để có trải nghiệm tốt nhất. Chúc chuyến bay vui vẻ!', '2025-06-11 15:41:00', NULL),

-- ============= CHATBOX 47 (Customer 53) - Active chat (ONGOING) =============
(47, NULL, 'Xin chào, tôi cần hỗ trợ về vé máy bay bị delay', '2025-06-11 16:45:00', NULL),
(47, 8, 'Chào anh/chị! Tôi là Trang. Anh/chị cho tôi biết chuyến bay nào bị delay?', '2025-06-11 16:46:00', NULL),
(47, NULL, 'Chuyến VN156 SGN-HAN hôm nay 18:00, tôi đang ở sân bay mà thông báo delay 2 tiếng', '2025-06-11 16:47:30', NULL),
(47, 8, 'Tôi check hệ thống... Chuyến VN156 delay do thời tiết xấu ở Hà Nội. Dự kiến khởi hành 20:15', '2025-06-11 16:49:00', NULL),
(47, NULL, 'Vậy tôi có được bồi thường gì không? Và có meal voucher không?', '2025-06-11 16:50:30', NULL),
(47, 8, 'Delay do thời tiết là force majeure nên không có compensation. Nhưng từ 2h trở lên sẽ có meal voucher 200k', '2025-06-11 16:52:00', NULL),
(47, NULL, 'Làm sao để nhận meal voucher?', '2025-06-11 16:53:00', NULL),
(47, 8, 'Anh/chị ra quầy check-in gate 15, staff sẽ cấp voucher. Hoặc tôi có thể gửi digital voucher qua app', '2025-06-11 16:54:30', NULL),
(47, NULL, 'Gửi digital voucher được không? Tiện hơn', '2025-06-11 16:55:30', NULL),
(47, 8, 'Đã gửi digital meal voucher 200k vào VietnamAir app. Check phần "My Vouchers" nhé', '2025-06-11 16:57:00', NULL),
(47, NULL, 'Thấy rồi! Cảm ơn bạn. Chuyến bay có update gì tôi được thông báo không?', '2025-06-11 16:58:00', NULL);

-- ============= THÊM MESSAGES CHO CÁC CHATBOX KHÁC (Rút gọn) =============

-- Chatbox 4 - Payment issue
INSERT INTO message (chatbox_id, employee_id, content, send_time, deleted_at) VALUES
(4, NULL, 'Thẻ tín dụng bị từ chối khi thanh toán', '2025-01-10 11:00:00', NULL),
(4, 6, 'Anh/chị thử thanh toán bằng thẻ khác hoặc chuyển khoản được không?', '2025-01-10 11:01:30', NULL),
(4, NULL, 'Chuyển khoản thì làm như thế nào?', '2025-01-10 11:03:00', NULL),
(4, 6, 'Tôi gửi thông tin chuyển khoản qua SMS. Chuyển xong screenshot gửi lại nhé', '2025-01-10 11:04:00', NULL),
(4, NULL, 'Đã chuyển rồi, số ref: 123456789', '2025-01-10 11:15:00', NULL),
(4, 6, 'Đã nhận được tiền. Vé sẽ được issue trong 30 phút. Cảm ơn anh/chị!', '2025-01-10 11:16:30', NULL);

-- Chatbox 20 - International booking  
INSERT INTO message (chatbox_id, employee_id, content, send_time, deleted_at) VALUES
(20, NULL, 'Tôi muốn book vé HAN-Paris', '2025-03-05 14:20:00', NULL),
(20, 10, 'Chuyến HAN-CDG via Bangkok, giá từ 28 triệu. Anh/chị muốn ngày nào?', '2025-03-05 14:21:30', NULL),
(20, NULL, 'Ngày 20/3, có cần visa không?', '2025-03-05 14:23:00', NULL),
(20, 10, 'Cần visa Schengen. Anh/chị đã có chưa?', '2025-03-05 14:24:00', NULL),
(20, NULL, 'Chưa, apply mất bao lâu?', '2025-03-05 14:25:00', NULL),
(20, 10, 'Thường 10-15 ngày làm việc. Nên book vé trước để có booking confirmation nộp hồ sơ', '2025-03-05 14:26:30', NULL),
(20, NULL, 'OK, tôi book ngay. Business class có không?', '2025-03-05 14:28:00', NULL),
(20, 10, 'Business 168 triệu. Tôi tạo booking. Cần passport number để book international', '2025-03-05 14:29:00', NULL),
(20, NULL, 'Passport: N1234567, expires 2030', '2025-03-05 14:30:30', NULL),
(20, 10, 'Booking complete! Code: VN999999. Invitation letter sẽ được gửi email cho visa application', '2025-03-05 14:32:00', NULL);

-- ============= BẢNG ACCOUNT_CHATBOX =============

INSERT INTO account_chatbox (account_id, chatbox_id, last_visit_time, created_at, updated_at, deleted_at) VALUES

-- Employee accounts (Support staff)
-- Employee 5 (Minh) - Active support agent
(15, 1, '2025-01-03 09:31:00', '2025-01-03 09:16:30', '2025-01-03 09:31:00', NULL),
(15, 12, '2025-02-01 10:44:00', '2025-02-01 10:31:00', '2025-02-01 10:44:00', NULL),

-- Employee 7 (Linh) - Booking specialist  
(17, 2, '2025-01-05 14:32:00', '2025-01-05 14:21:00', '2025-01-05 14:32:00', NULL),

-- Employee 9 (Hùng) - Technical support
(19, 3, '2025-01-07 16:57:00', '2025-01-07 16:46:00', '2025-01-07 16:57:00', NULL),

-- Employee 6 (Payment specialist)
(16, 4, '2025-01-10 11:16:30', '2025-01-10 11:01:30', '2025-01-10 11:16:30', NULL),

-- Employee 10 (International specialist)
(20, 20, '2025-03-05 14:32:00', '2025-03-05 14:21:30', '2025-03-05 14:32:00', NULL),

-- Employee 12 (Nam) - IT Support
(22, 46, '2025-06-11 15:41:00', '2025-06-11 15:31:00', '2025-06-11 15:41:00', NULL),

-- Employee 8 (Trang) - Current active chat
(18, 47, '2025-06-11 16:57:00', '2025-06-11 16:46:00', '2025-06-11 16:58:40', NULL),

-- Customer accounts
-- Customer 15 
(25, 1, '2025-01-03 09:32:30', '2025-01-03 09:15:00', '2025-01-03 09:32:30', NULL),

-- Customer 22
(32, 2, '2025-01-05 14:30:30', '2025-01-05 14:20:00', '2025-01-05 14:30:30', NULL),

-- Customer 8
(18, 3, '2025-01-07 16:56:00', '2025-01-07 16:45:00', '2025-01-07 16:56:00', NULL),

-- Customer 45
(55, 4, '2025-01-10 11:15:00', '2025-01-10 11:00:00', '2025-01-10 11:15:00', NULL),

-- Customer 36 (Tết booking)
(46, 12, '2025-02-01 10:42:30', '2025-02-01 10:30:00', '2025-02-01 10:42:30', NULL),

-- Customer 82 (International)
(92, 20, '2025-03-05 14:30:30', '2025-03-05 14:20:00', '2025-03-05 14:30:30', NULL),

-- Customer 24 (Recent resolved)
(34, 46, '2025-06-11 15:40:00', '2025-06-11 15:30:00', '2025-06-11 15:40:00', NULL),

-- Customer 53 (Currently active)
(63, 47, '2025-06-11 16:58:00', '2025-06-11 16:45:00', '2025-06-11 16:58:40', NULL),

-- Supervisor accounts (monitoring)
-- Manager account monitoring multiple chats
(5, 1, '2025-01-03 10:00:00', '2025-01-03 09:30:00', '2025-01-03 10:00:00', NULL),
(5, 2, '2025-01-05 15:00:00', '2025-01-05 14:30:00', '2025-01-05 15:00:00', NULL),
(5, 12, '2025-02-01 11:00:00', '2025-02-01 10:45:00', '2025-02-01 11:00:00', NULL),
(5, 47, '2025-06-11 16:58:40', '2025-06-11 16:50:00', '2025-06-11 16:58:40', NULL);

-- Dữ liệu vé máy bay (ticket) với order_id hex encoded
INSERT INTO ticket (flight_id, ticket_class_id, book_customer_id, passenger_id, seat_number, ticket_status, payment_time, fare, confirmation_code, order_id, deleted_at) VALUES

-- ============= COMPLETED FLIGHTS TODAY (11/6/2025) =============

-- VN153 (HAN-SGN 2025-06-11 06:00) - Flight ID: 103 - COMPLETED
-- Economy class tickets (SOLD OUT)
(103, 1, 15, 25, '12A', 1, '2025-06-10 20:15:00', 2500000.00, 'FMS-20250610-A1B2', '32303135303046244D532D32303235303631302D41314232', NULL),
(103, 1, 22, 35, '12B', 1, '2025-06-10 20:18:00', 2500000.00, 'FMS-20250610-A1B3', '32303138303046244D532D32303235303631302D41314233', NULL),
(103, 1, 8, 18, '12C', 1, '2025-06-10 20:22:00', 2500000.00, 'FMS-20250610-A1B4', '32303232303046244D532D32303235303631302D41314234', NULL),
(103, 1, 45, 55, '13A', 1, '2025-06-10 20:30:00', 2500000.00, 'FMS-20250610-A1B5', '32303330303046244D532D32303235303631302D41314235', NULL),
(103, 1, 33, 43, '13B', 1, '2025-06-10 20:35:00', 2500000.00, 'FMS-20250610-A1B6', '32303335303046244D532D32303235303631302D41314236', NULL),

-- Premium Economy tickets
(103, 2, 67, 77, '5A', 1, '2025-06-10 21:00:00', 3750000.00, 'FMS-20250610-P1E2', '32313030303046244D532D32303235303631302D50314532', NULL),
(103, 2, 29, 39, '5B', 1, '2025-06-10 21:05:00', 3750000.00, 'FMS-20250610-P1E3', '32313035303046244D532D32303235303631302D50314533', NULL),

-- Business class tickets
(103, 3, 51, 61, '2A', 1, '2025-06-10 21:30:00', 7500000.00, 'FMS-20250610-B1Z1', '32313330303046244D532D32303235303631302D42315A31', NULL),
(103, 3, 18, 28, '2B', 1, '2025-06-10 21:35:00', 7500000.00, 'FMS-20250610-B1Z2', '32313335303046244D532D32303235303631302D42315A32', NULL),

-- VN154 (SGN-HAN 2025-06-11 10:00) - Flight ID: 104 - COMPLETED
-- Sample tickets (all paid and completed)
(104, 1, 36, 46, '15A', 1, '2025-06-10 22:00:00', 2500000.00, 'FMS-20250610-A2C1', '32323030303046244D532D32303235303631302D41324331', NULL),
(104, 1, 88, 98, '15B', 1, '2025-06-10 22:05:00', 2500000.00, 'FMS-20250610-A2C2', '32323035303046244D532D32303235303631302D41324332', NULL),
(104, 1, 41, 51, '15C', 1, '2025-06-10 22:10:00', 2500000.00, 'FMS-20250610-A2C3', '32323130303046244D532D32303235303631302D41324333', NULL),
(104, 2, 56, 66, '6A', 1, '2025-06-10 22:30:00', 3750000.00, 'FMS-20250610-P2E1', '32323330303046244D532D32303235303631302D50324531', NULL),
(104, 3, 23, 33, '3A', 1, '2025-06-10 23:00:00', 7500000.00, 'FMS-20250610-B2Z1', '32333030303046244D532D32303235303631302D42325A31', NULL),

-- VN155 (HAN-SGN 2025-06-11 14:00) - Flight ID: 105 - IN PROGRESS (Current time: 17:23)
-- All tickets sold out and paid
(105, 1, 69, 79, '20A', 1, '2025-06-11 08:00:00', 2500000.00, 'FMS-20250611-A3D1', '30383030303046244D532D32303235303631312D41334431', NULL),
(105, 1, 47, 57, '20B', 1, '2025-06-11 08:05:00', 2500000.00, 'FMS-20250611-A3D2', '30383035303046244D532D32303235303631312D41334432', NULL),
(105, 1, 14, 24, '20C', 1, '2025-06-11 08:10:00', 2500000.00, 'FMS-20250611-A3D3', '30383130303046244D532D32303235303631312D41334433', NULL),
(105, 2, 82, 92, '7A', 1, '2025-06-11 08:30:00', 3750000.00, 'FMS-20250611-P3E1', '30383330303046244D532D32303235303631312D50334531', NULL),
(105, 3, 35, 45, '4A', 1, '2025-06-11 09:00:00', 7500000.00, 'FMS-20250611-B3Z1', '30393030303046244D532D32303235303631312D42335A31', NULL),

-- ============= SCHEDULED FLIGHT TODAY (Available) =============

-- VN156 (SGN-HAN 2025-06-11 18:00) - Flight ID: 106 - SCHEDULED (49 seats remaining)
-- Mixed status: paid and some unpaid bookings

-- Paid Economy tickets (117 sold out of 162)
(106, 1, 59, 69, '25A', 1, '2025-06-11 10:00:00', 2500000.00, 'FMS-20250611-A4E1', '31303030303046244D532D32303235303631312D41344531', NULL),
(106, 1, 26, 36, '25B', 1, '2025-06-11 10:05:00', 2500000.00, 'FMS-20250611-A4E2', '31303035303046244D532D32303235303631312D41344532', NULL),
(106, 1, 73, 83, '25C', 1, '2025-06-11 10:10:00', 2500000.00, 'FMS-20250611-A4E3', '31303130303046244D532D32303235303631312D41344533', NULL),
(106, 1, 11, 21, '25D', 1, '2025-06-11 10:15:00', 2500000.00, 'FMS-20250611-A4E4', '31303135303046244D532D32303235303631312D41344534', NULL),
(106, 1, 48, 58, '25E', 1, '2025-06-11 10:20:00', 2500000.00, 'FMS-20250611-A4E5', '31303230303046244D532D32303235303631312D41344535', NULL),

-- Recent unpaid bookings (made today, payment pending)
(106, 1, 61, 71, '26A', 0, NULL, 2500000.00, 'FMS-20250611-A4F1', '31343030303046244D532D32303235303631312D41344631', NULL),
(106, 1, 37, 47, '26B', 0, NULL, 2500000.00, 'FMS-20250611-A4F2', '31343035303046244D532D32303235303631312D41344632', NULL),
(106, 1, 84, 94, '26C', 0, NULL, 2500000.00, 'FMS-20250611-A4F3', '31343130303046244D532D32303235303631312D41344633', NULL),

-- Premium Economy (9 sold out of 12)
(106, 2, 19, 29, '8A', 1, '2025-06-11 11:00:00', 3750000.00, 'FMS-20250611-P4E1', '31313030303046244D532D32303235303631312D50344531', NULL),
(106, 2, 52, 62, '8B', 1, '2025-06-11 11:30:00', 3750000.00, 'FMS-20250611-P4E2', '31313330303046244D532D32303235303631312D50344532', NULL),
(106, 2, 75, 85, '8C', 1, '2025-06-11 12:00:00', 3750000.00, 'FMS-20250611-P4E3', '31323030303046244D532D32303235303631312D50344533', NULL),

-- Business (5 sold out of 6)
(106, 3, 28, 38, '1A', 1, '2025-06-11 13:00:00', 7500000.00, 'FMS-20250611-B4Z1', '31333030303046244D532D32303235303631312D42345A31', NULL),
(106, 3, 63, 73, '1B', 1, '2025-06-11 13:30:00', 7500000.00, 'FMS-20250611-B4Z2', '31333330303046244D532D32303235303631312D42345A32', NULL),

-- ============= TOMORROW'S FLIGHTS (12/6/2025) =============

-- VN101 (HAN-SGN 2025-06-12 06:00) - Flight ID: 111
-- Early bookings with good availability

-- Paid tickets (early bookings from yesterday/today)
(111, 1, 16, 26, '30A', 1, '2025-06-10 15:00:00', 2500000.00, 'FMS-20250610-A5G1', '31353030303046244D532D32303235303631302D41354731', NULL),
(111, 1, 71, 81, '30B', 1, '2025-06-10 15:30:00', 2500000.00, 'FMS-20250610-A5G2', '31353330303046244D532D32303235303631302D41354732', NULL),
(111, 1, 44, 54, '30C', 1, '2025-06-10 16:00:00', 2500000.00, 'FMS-20250610-A5G3', '31363030303046244D532D32303235303631302D41354733', NULL),
(111, 1, 58, 68, '30D', 1, '2025-06-11 09:00:00', 2500000.00, 'FMS-20250611-A5G4', '30393030303046244D532D32303235303631312D41354734', NULL),
(111, 1, 31, 41, '30E', 1, '2025-06-11 09:30:00', 2500000.00, 'FMS-20250611-A5G5', '30393330303046244D532D32303235303631312D41354735', NULL),

-- Recent bookings (today) - some paid, some pending
(111, 1, 85, 95, '31A', 1, '2025-06-11 14:00:00', 2500000.00, 'FMS-20250611-A5H1', '31343030303046244D532D32303235303631312D41354831', NULL),
(111, 1, 17, 27, '31B', 0, NULL, 2500000.00, 'FMS-20250611-A5H2', '31353030303046244D532D32303235303631312D41354832', NULL),
(111, 1, 49, 59, '31C', 0, NULL, 2500000.00, 'FMS-20250611-A5H3', '31363030303046244D532D32303235303631312D41354833', NULL),

-- Premium Economy bookings
(111, 2, 66, 76, '9A', 1, '2025-06-11 12:00:00', 3750000.00, 'FMS-20250611-P5E1', '31323030303046244D532D32303235303631312D50354531', NULL),
(111, 2, 38, 48, '9B', 1, '2025-06-11 13:00:00', 3750000.00, 'FMS-20250611-P5E2', '31333030303046244D532D32303235303631312D50354532', NULL),

-- Business class
(111, 3, 77, 87, '1C', 1, '2025-06-11 11:00:00', 7500000.00, 'FMS-20250611-B5Z1', '31313030303046244D532D32303235303631312D42355A31', NULL),

-- VN102 (SGN-HAN 2025-06-12 10:00) - Flight ID: 112
-- Regular bookings pattern
(112, 1, 24, 34, '32A', 1, '2025-06-10 18:00:00', 2500000.00, 'FMS-20250610-A6I1', '31383030303046244D532D32303235303631302D41364931', NULL),
(112, 1, 53, 63, '32B', 1, '2025-06-10 19:00:00', 2500000.00, 'FMS-20250610-A6I2', '31393030303046244D532D32303235303631302D41364932', NULL),
(112, 1, 42, 52, '32C', 1, '2025-06-11 10:00:00', 2500000.00, 'FMS-20250611-A6I3', '31303030303046244D532D32303235303631312D41364933', NULL),
(112, 1, 79, 89, '32D', 0, NULL, 2500000.00, 'FMS-20250611-A6I4', '31353530303046244D532D32303235303631312D41364934', NULL),

(112, 2, 65, 75, '10A', 1, '2025-06-11 11:30:00', 3750000.00, 'FMS-20250611-P6E1', '31313330303046244D532D32303235303631312D50364531', NULL),
(112, 3, 39, 49, '2C', 1, '2025-06-11 12:30:00', 7500000.00, 'FMS-20250611-B6Z1', '31323330303046244D532D32303235303631312D42365A31', NULL),

-- ============= INTERNATIONAL FLIGHTS =============

-- VN801 (HAN-BKK 2025-06-12 08:30) - Flight ID: 119
-- International pricing and booking patterns
(119, 1, 54, 64, '35A', 1, '2025-06-09 14:00:00', 4500000.00, 'FMS-20250609-I1J1', '31343030303046244D532D32303235303630392D49314A31', NULL),
(119, 1, 27, 37, '35B', 1, '2025-06-09 15:00:00', 4500000.00, 'FMS-20250609-I1J2', '31353030303046244D532D32303235303630392D49314A32', NULL),
(119, 1, 72, 82, '35C', 1, '2025-06-10 10:00:00', 4500000.00, 'FMS-20250610-I1J3', '31303030303046244D532D32303235303631302D49314A33', NULL),
(119, 1, 43, 53, '35D', 1, '2025-06-10 16:00:00', 4500000.00, 'FMS-20250610-I1J4', '31363030303046244D532D32303235303631302D49314A34', NULL),

(119, 2, 86, 96, '11A', 1, '2025-06-10 11:00:00', 8100000.00, 'FMS-20250610-PI1E1', '31313030303046244D532D32303235303631302D5049314531', NULL),
(119, 2, 20, 30, '11B', 1, '2025-06-10 17:00:00', 8100000.00, 'FMS-20250610-PI1E2', '31373030303046244D532D32303235303631302D5049314532', NULL),

(119, 3, 74, 84, '1D', 1, '2025-06-09 20:00:00', 18000000.00, 'FMS-20250609-BI1Z1', '32303030303046244D532D32303235303630392D4249315A31', NULL),

-- ============= HISTORICAL BOOKINGS (Past flights) =============

-- VN101 (HAN-SGN 2023-09-01) - Flight ID: 1 - Historical completed
(1, 1, 12, 22, '40A', 1, '2023-08-25 10:00:00', 1800000.00, 'FMS-20230825-H1K1', '31303030303046244D532D32303233303832352D48314B31', NULL),
(1, 1, 55, 65, '40B', 1, '2023-08-25 11:00:00', 1800000.00, 'FMS-20230825-H1K2', '31313030303046244D532D32303233303832352D48314B32', NULL),
(1, 1, 30, 40, '40C', 1, '2023-08-26 09:00:00', 1800000.00, 'FMS-20230826-H1K3', '30393030303046244D532D32303233303832362D48314B33', NULL),

(1, 2, 76, 86, '12A', 1, '2023-08-25 15:00:00', 2700000.00, 'FMS-20230825-PH1E1', '31353030303046244D532D32303233303832352D5048314531', NULL),
(1, 3, 21, 31, '3A', 1, '2023-08-24 20:00:00', 5400000.00, 'FMS-20230824-BH1Z1', '32303030303046244D532D32303233303832342D4248315A31', NULL),

-- ============= CURRENT USER BOOKINGS (thinh0704hcm related) =============
-- Assuming thinh0704hcm has customer_id: 91
-- Recent booking for tomorrow's flight (PAID)
(111, 1, 91, 101, '31D', 1, '2025-06-11 16:45:00', 2500000.00, 'FMS-20250611-A5U1', '31363435303046244D532D32303235303631312D41355531', NULL),

-- Pending booking for next week (UNPAID) - Flight ID: 114 (next week flight)
(114, 2, 91, 101, '9C', 0, NULL, 3750000.00, 'FMS-20250611-P7U1', '31373030303046244D532D32303235303631312D50375531', NULL),

-- ============= GROUP BOOKINGS =============

-- Family booking (same customer, multiple passengers)
(117, 1, 25, 102, '45A', 1, '2025-06-10 14:00:00', 2200000.00, 'FMS-20250610-G1F1', '31343030303046244D532D32303235303631302D47314631', NULL),
(117, 1, 25, 103, '45B', 1, '2025-06-10 14:00:00', 2200000.00, 'FMS-20250610-G1F2', '31343030303046244D532D32303235303631302D47314632', NULL),
(117, 1, 25, 104, '45C', 1, '2025-06-10 14:00:00', 2200000.00, 'FMS-20250610-G1F3', '31343030303046244D532D32303235303631302D47314633', NULL),

-- Corporate booking (business travelers)
(115, 3, 60, 105, '2D', 1, '2025-06-09 09:00:00', 5040000.00, 'FMS-20250609-C1B1', '30393030303046244D532D32303235303630392D43314231', NULL),
(115, 3, 60, 106, '2E', 1, '2025-06-09 09:00:00', 5040000.00, 'FMS-20250609-C1B2', '30393030303046244D532D32303235303630392D43314232', NULL),

-- ============= SPECIAL CASES =============

-- Last minute booking (made at current time: 17:23)
(106, 1, 87, 97, '26D', 0, NULL, 2500000.00, 'FMS-20250611-L1M1', '31373233303046244D532D32303235303631312D4C314D31', NULL),

-- Cancelled ticket (soft delete)
(106, 1, 90, 100, '26E', 1, '2025-06-11 12:00:00', 2500000.00, 'FMS-20250611-C1X1', '31323030303046244D532D32303235303631312D43315831', '2025-06-11 15:30:00'),

-- No-show ticket (paid but passenger didn't show)
(105, 1, 89, 99, '20D', 1, '2025-06-11 07:00:00', 2500000.00, 'FMS-20250611-N1S1', '30373030303046244D532D32303235303631312D4E315331', NULL),

-- Overbooked compensation (airline gave free upgrade)
(111, 3, 34, 44, '1E', 1, '2025-06-11 14:30:00', 0.00, 'FMS-20250611-U1P1', '31343330303046244D532D32303235303631312D55315031', NULL),

-- Additional current tickets showing realistic booking velocity
-- VN156 - Last few bookings before departure (current time: 17:23)
(106, 1, 25, 107, '26F', 0, NULL, 2500000.00, 'FMS-20250611-R1T1', '31373230303046244D532D32303235303631312D52315431', NULL),
(106, 1, 55, 108, '27A', 0, NULL, 2500000.00, 'FMS-20250611-R1T2', '31373231303046244D532D32303235303631312D52315432', NULL);

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