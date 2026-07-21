CREATE DATABASE IF NOT EXISTS dbtest; SET NAMES utf8mb4;
USE dbtest;
SET FOREIGN_KEY_CHECKS = 0;


DROP TABLE IF EXISTS persistent_logins;
DROP TABLE IF EXISTS consultation_reports;
DROP TABLE IF EXISTS consultation_orders;
DROP TABLE IF EXISTS consultation_slots;
DROP TABLE IF EXISTS payouts;
DROP TABLE IF EXISTS refunds;
DROP TABLE IF EXISTS course_qa_comments;
DROP TABLE IF EXISTS order_details; 
DROP TABLE IF EXISTS course_orders;
DROP TABLE IF EXISTS shopping_carts; 
DROP TABLE IF EXISTS member_coupons; 
DROP TABLE IF EXISTS coupons;
DROP TABLE IF EXISTS course_bookmarks;
DROP TABLE IF EXISTS courses; 
DROP TABLE IF EXISTS course_categories;
DROP TABLE IF EXISTS activity_follows;
DROP TABLE IF EXISTS activity_registrations;
DROP TABLE IF EXISTS activity_reports;
DROP TABLE IF EXISTS activities;
DROP TABLE IF EXISTS activity_categories;
DROP TABLE IF EXISTS article_likes;
DROP TABLE IF EXISTS article_bookmarks;
DROP TABLE IF EXISTS article_view_histories;
DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS article_categories;
DROP TABLE IF EXISTS psychologist_notice;
DROP TABLE IF EXISTS member_notice;
DROP TABLE IF EXISTS psych_expertise;
DROP TABLE IF EXISTS psych_all_expertise;

DROP TABLE IF EXISTS admin_permissions;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS notice_templates;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS psychologist;
DROP TABLE IF EXISTS admin;
DROP TABLE IF EXISTS member;

-- ==========================================
-- 1. 會員 (member)
-- ==========================================
CREATE TABLE member (
  member_id INT NOT NULL AUTO_INCREMENT,
  member_account VARCHAR(20) NOT NULL,
  member_password VARCHAR(20) NOT NULL,
  account_status TINYINT NOT NULL DEFAULT 0,
  name VARCHAR(20) NOT NULL,
  gender VARCHAR(10) NOT NULL,
  phone_number VARCHAR(10) NOT NULL,
  birthday DATE DEFAULT NULL,
  city VARCHAR(50) DEFAULT NULL,
  dist VARCHAR(50) DEFAULT NULL,
  address VARCHAR(40) DEFAULT NULL,
  regis_at DATETIME NOT NULL,
  profile_pic LONGBLOB DEFAULT NULL,
  card_number VARCHAR(20) DEFAULT NULL,
  nickname VARCHAR(200) DEFAULT NULL,
  email VARCHAR(50) NOT NULL,
  bank_account VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (member_id),
  UNIQUE KEY uk_member_account (member_account)
);

-- 插入 member 假資料
INSERT INTO member VALUES 
(1, 'user01', '1111', 1, '大吳老師', '男', '0912345678', '1900-05-12', '台北市', '大安區', '敦化南路一段100號', '2026-01-15 10:30:00', NULL, '1234567812345678', '大吳', 'xiaowu@example.com', '123456789012'),
(2, 'user02', '2222', 1, '小吳老師', '男', '0923456789', '1950-11-23', '桃園市', '中壢區', '復興路46號8樓', '2026-02-20 14:15:00', NULL, '8765432187654321', '小吳', 'dawu@example.com', NULL),
(3, 'user03', '3333', 1, '郭老師', '男', '0934567890', '1000-07-08', '高雄市', '苓雅區', '成功一路1號', '2026-05-23 18:00:00', NULL, NULL, NULL, 'kuo@example.com', '987654321098'),
(4, 'user04', '4444', 1, '上億', '男', '0945678901', '2000-02-29', '新北市', '板橋區', '縣民大道二段7號', '2025-12-01 09:00:00', NULL, NULL, NULL, '100million@example.com', NULL),
(5, 'user05', '5555', 1, '上上億', '女', '0945678901', '2000-02-29', '新北市', '板橋區', '縣民大道二段7號', '2025-12-01 09:00:00', NULL, NULL, NULL, '1000million@example.com', NULL),
(6, 'user06', '6666', 1, '上上上億', '男', '0945678901', '2000-02-29', '新北市', '板橋區', '縣民大道二段7號', '2025-12-01 09:00:00', NULL, NULL, NULL, '10000million@example.com', NULL),
(7, 'user07', '7777', 1, '上上上上億', '女', '0945678901', '2000-02-29', '新北市', '板橋區', '縣民大道二段7號', '2025-12-01 09:00:00', NULL, NULL, NULL, '100000million@example.com', NULL),
(8, 'user08', '8888', 0, '上上上上上億', '女', '0945678901', '2000-02-29', '新北市', '板橋區', '縣民大道二段7號', '2025-12-01 09:00:00', NULL, NULL, NULL, '1000000million@example.com', NULL),
(9, 'user09', '9999', 1, 'user09', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user09', 'user09@example.com', NULL),
(10, 'user10', '1010', 1, 'user10', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user10', 'user10@example.com', NULL),
(11, 'user11', '1111', 1, 'user11', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user11', 'user11@example.com', NULL),
(12, 'user12', '1212', 1, 'user12', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user12', 'user12@example.com', NULL),
(13, 'user13', '1313', 1, 'user13', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user13', 'user13@example.com', NULL),
(14, 'user14', '1414', 1, 'user14', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user14', 'user14@example.com', NULL),
(15, 'user15', '1515', 1, 'user15', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user15', 'user15@example.com', NULL),
(16, 'user16', '1616', 1, 'user16', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user16', 'user16@example.com', NULL),
(17, 'user17', '1717', 1, 'user17', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user17', 'user17@example.com', NULL),
(18, 'user18', '1818', 1, 'user18', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user18', 'user18@example.com', NULL),
(19, 'user19', '1919', 1, 'user19', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user19', 'user19@example.com', NULL),
(20, 'user20', '2020', 1, 'user20', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user20', 'user20@example.com', NULL),
(21, 'user21', '2121', 1, 'user21', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user21', 'user21@example.com', NULL),
(22, 'user22', '2222', 1, 'user22', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user22', 'user22@example.com', NULL),
(23, 'user23', '2323', 1, 'user23', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user23', 'user23@example.com', NULL),
(24, 'user24', '2424', 1, 'user24', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user24', 'user24@example.com', NULL),
(25, 'user25', '2525', 1, 'user25', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user25', 'user25@example.com', NULL),
(26, 'user26', '2626', 1, 'user26', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user26', 'user26@example.com', NULL),
(27, 'user27', '2727', 1, 'user27', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user27', 'user27@example.com', NULL),
(28, 'user28', '2828', 1, 'user28', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user28', 'user28@example.com', NULL),
(29, 'user29', '2929', 1, 'user29', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user29', 'user29@example.com', NULL),
(30, 'user30', '3030', 1, 'user30', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user30', 'user30@example.com', NULL),
(31, 'user31', '3131', 1, 'user31', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user31', 'user31@example.com', NULL),
(32, 'user32', '3232', 1, 'user32', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user32', 'user32@example.com', NULL),
(33, 'user33', '3333', 1, 'user33', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user33', 'user33@example.com', NULL),
(34, 'user34', '3434', 1, 'user34', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user34', 'user34@example.com', NULL),
(35, 'user35', '3535', 1, 'user35', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user35', 'user35@example.com', NULL),
(36, 'user36', '3636', 1, 'user36', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user36', 'user36@example.com', NULL),
(37, 'user37', '3737', 1, 'user37', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user37', 'user37@example.com', NULL),
(38, 'user38', '3838', 1, 'user38', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user38', 'user38@example.com', NULL),
(39, 'user39', '3939', 1, 'user39', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user39', 'user39@example.com', NULL),
(40, 'user40', '4040', 1, 'user40', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user40', 'user40@example.com', NULL),
(41, 'user41', '4141', 1, 'user41', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user41', 'user41@example.com', NULL),
(42, 'user42', '4242', 1, 'user42', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user42', 'user42@example.com', NULL),
(43, 'user43', '4343', 1, 'user43', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user43', 'user43@example.com', NULL),
(44, 'user44', '4444', 1, 'user44', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user44', 'user44@example.com', NULL),
(45, 'user45', '4545', 1, 'user45', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user45', 'user45@example.com', NULL),
(46, 'user46', '4646', 1, 'user46', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user46', 'user46@example.com', NULL),
(47, 'user47', '4747', 1, 'user47', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user47', 'user47@example.com', NULL),
(48, 'user48', '4848', 1, 'user48', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user48', 'user48@example.com', NULL),
(49, 'user49', '4949', 1, 'user49', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user49', 'user49@example.com', NULL),
(50, 'user50', '5050', 1, 'user50', '男', '0900000000', NULL, NULL, NULL, NULL, '2026-01-01 10:00:00', NULL, NULL, 'user50', 'user50@example.com', NULL);


-- ==========================================
-- 2. 後台員工 (admin)
-- ==========================================
CREATE TABLE admin (
  admin_id INT NOT NULL AUTO_INCREMENT,
  admin_account VARCHAR(20) NOT NULL,
  admin_password VARCHAR(20) NOT NULL,
  account_status TINYINT NOT NULL DEFAULT 0,
  name VARCHAR(20) NOT NULL,
  phone_number VARCHAR(10) NOT NULL,
  hiredate DATETIME NOT NULL,
  profile_pic LONGBLOB DEFAULT NULL,
  PRIMARY KEY (admin_id)
);

-- 插入 admin 假資料
INSERT INTO admin VALUES
(1, 'admin01', '1111', 1, '超級管理員', '0911111111', '2025-01-01 09:00:00', NULL),
(2, 'admin02', '2222', 1, '諮商管理員', '0922222222', '2025-06-15 09:30:00', NULL),
(3, 'admin03', '3333', 1, '課程商城管理員', '0933333333', '2026-05-20 10:00:00', NULL),
(4, 'admin04', '4444', 1, '文章管理員', '0944444444', '2024-03-01 08:30:00', NULL),
(5, 'admin05', '5555', 1, '活動管理員', '0955555555', '2026-05-22 08:30:00', NULL),
(6, 'test01', '4444', 1, '文章管理員', '0966454566', '2024-04-01 08:30:00', NULL),
(7, 'test02', '4444', 1, '文章管理員', '0966930485', '2024-04-01 08:30:00', NULL),
(8, 'test03', '4444', 0, '文章管理員', '0966888889', '2024-04-01 08:30:00', NULL),
(9, 'test04', '4444', 0, '文章管理員', '0966666666', '2024-04-01 08:30:00', NULL);

-- ==========================================
-- 3. 權限 (permissions)
-- ==========================================
CREATE TABLE permissions (
  perm_id INT NOT NULL AUTO_INCREMENT,
  perm_name VARCHAR(50) NOT NULL,
  perm_detail VARCHAR(200) NOT NULL,
  PRIMARY KEY (perm_id)
);

-- 插入 permissions 假資料
INSERT INTO permissions VALUES
(1, 'super_admin', '超級管理員'),
(2, 'consultation', '諮商管理權限'),
(3, 'courses', '課程商城權限'),
(4, 'articles', '文章管理權限'),
(5, 'activities', '活動管理權限');


-- ==========================================
-- 4. 管理員權限 (admin_permissions)
-- ==========================================
CREATE TABLE persistent_logins (
  username  VARCHAR(64) NOT NULL,
  series    VARCHAR(64) PRIMARY KEY,
  token     VARCHAR(64) NOT NULL,
  last_used TIMESTAMP   NOT NULL
);

CREATE TABLE admin_permissions (
  admin_id INT NOT NULL,
  perm_id INT NOT NULL,
  PRIMARY KEY (admin_id, perm_id),
  CONSTRAINT fk_admin_perm_admin FOREIGN KEY (admin_id) REFERENCES admin (admin_id),
  CONSTRAINT fk_admin_perm_permissions FOREIGN KEY (perm_id) REFERENCES permissions (perm_id)
);

-- 插入 admin_permissions 假資料 (分配權限)
INSERT INTO admin_permissions VALUES
(1, 1), -- 系統管理員
(2, 2),
(3, 3),
(4, 4),
(5, 5);


-- ==========================================
-- 5. 系統公告 (notifications)
-- ==========================================
CREATE TABLE notifications (
  notice_id INT NOT NULL AUTO_INCREMENT,
  admin_id INT NOT NULL,
  system_content VARCHAR(1000) NOT NULL,
  created_at DATETIME NOT NULL,
  notice_status TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (notice_id),
  CONSTRAINT fk_notifications_admin FOREIGN KEY (admin_id) REFERENCES admin (admin_id)
);

-- 插入 notifications 假資料
INSERT INTO notifications VALUES
(1, 1, '系統將於本週日凌晨 02:00 - 06:00 進行資料庫定期維護，屆時將暫停登入服務。', '2026-05-20 09:00:00', 1),
(2, 1, '歡迎加入我們！新版會員中心介面已正式上線，優化多項操作體驗。', '2026-05-22 10:30:00', 1),
(3, 2, '【內部公告】端午節連假出勤與值班排班表已公布，請全體同仁至後台確認。', '2026-05-23 14:00:00', 0),
(4, 3, '因應近期詐騙頻傳，系統已加強密碼強度驗證，請定期更改您的密碼。', '2026-05-23 16:15:00', 1);


-- ==========================================
-- 6. 通知範本 (notice_templates)
-- ==========================================
CREATE TABLE notice_templates (
  template_id INT NOT NULL AUTO_INCREMENT,
  admin_id INT NOT NULL,
  template_content VARCHAR(1000) NOT NULL,
  PRIMARY KEY (template_id),
  CONSTRAINT fk_templates_admin FOREIGN KEY (admin_id) REFERENCES admin (admin_id)
);


-- 插入 notice_templates 假資料
INSERT INTO notice_templates VALUES
(1, 1, '親愛的 {name} 您好，您的驗證碼為：{code}，請於 10 分鐘內輸入驗證，謝謝。'),
(2, 1, '恭喜您 {name} 已成功註冊成為會員！我們為您準備了迎新禮包，請至外箱查收。'),
(3, 3, '【密碼變更通知】您的帳號於 {time} 成功變更密碼。若非本人操作，請速聯絡客服。'),
(4, 3, '【帳號啟用成功】親愛的會員，您的帳號已審核通過並順利啟用，歡迎開始使用服務。');


USE dbtest;
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS psychologist;
DROP TABLE IF EXISTS psych_all_expertise;
DROP TABLE IF EXISTS psych_expertise;
DROP TABLE IF EXISTS member_notice;
DROP TABLE IF EXISTS psychologist_notice;



-- ==========================================
-- 心理師(psychologist)
-- ==========================================
CREATE TABLE psychologist (
  psych_id              INT         NOT NULL AUTO_INCREMENT,
  psych_account         VARCHAR(20) NOT NULL,
  psych_password        VARCHAR(20) NOT NULL,
  account_status        TINYINT     NOT NULL DEFAULT 0,
  name                  VARCHAR(20) NOT NULL,
  gender                VARCHAR(10) NOT NULL,
  phone_number          VARCHAR(10) NOT NULL,
  email                 VARCHAR(50) NOT NULL,
  psych_certificate     VARCHAR(30) NOT NULL,
  has_practice_license  BOOLEAN     NOT NULL DEFAULT 0,
  psych_loc             VARCHAR(50) NOT NULL,
  psych_fee             INT         NOT NULL,
  weekly_availability    VARCHAR(168) DEFAULT NULL,
  regis_at              DATETIME    NOT NULL,
  profile_pic           VARCHAR(100)    DEFAULT NULL,
  bank_account          VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (psych_id)
);


INSERT INTO psychologist 
  (psych_account, psych_password, account_status, name, gender, phone_number, email, 
   psych_certificate, has_practice_license, psych_loc, psych_fee, 
   weekly_availability, regis_at, profile_pic, bank_account)
VALUES 
  (
    '1', '1', 1, '陳雅婷', '女', '0912345678', 'chen@example.com', 
    'CERT-2021-00123', 1, '桃園市中壢區中山路52號', 2000, 
    '000000001111111110000000000000001111111110000000000000001111111110000000000000001111111110000000000000001111111110000000000000001111111110000000000000001111111110000000', 
    '2023-03-15 10:00:00','f16d1b0d-0bd0-4598-80a3-b6b117cb16a9.png', '12345678901234'
  ),
  (
    '2', '2', 1, '林志遠', '男', '0923456789', 'lin@example.com', 
    'CERT-2019-00456', 1, '新北市板橋區文化路一段188號', 1800, 
    '000000000000000000000000000000111111100000000111100000000000000000000000000000000000111111111000000011000000011111000000011111000000011000000011100000001111111100000000', 
    '2022-11-20 09:30:00','768407d2-85aa-4c49-aee5-76ba58828ca1.png', '98765432109876'
  ),


('psych007','Pw007Aa',1,'林詩涵','女','0900000007','psych007@mindcare.com.tw','諮商心理師證書第2007號',1,'桃園市中壢區延平路 500 號',2000,
'000000000000011111111000000000000000011111111000000000000000011111111000000000000000011111111000000000000000011111111000000000000000011111111000000000000000011111111000',
'2025-03-03 09:00:00','0ce3fffb-2f9a-49fb-b648-fb6726668b8d.png','00007100001007'),

('psych008','Pw008Aa',1,'張家豪','男','0900000008','psych008@mindcare.com.tw','諮商心理師證書第2008號',1,'桃園市桃園區中正路 108 號',1600,
'111111110000111100001111111100001111000011110000111111110000111100001111000011110000111111110000111100000000000000000000000000000000000000000000000000000000000000000000',
'2025-04-04 09:00:00','c052de1f-25fb-425c-a9fe-968b9f51f3e9.png','00008100001008'),

('psych009','Pw009Aa',1,'李思穎','女','0900000009','psych009@mindcare.com.tw','諮商心理師證書第2009號',1,'桃園市平鎮區環南路二段 11 號',2200,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111111111111111111111111111111111111111111111',
'2025-05-05 09:00:00','772ed3e2-8d5e-40c5-9b1c-a808f58dfc7d.png','00009100001009'),

('psych010','Pw010Aa',1,'黃彥誠','男','0900000010','psych010@mindcare.com.tw','諮商心理師證書第2010號',1,'新北市板橋區中山路一段 161 號',1700,
'000000000111111111000000000111111111000000000111111111111111111111111111111111111111111111111111111111111111111111000000000111111111111111110000000001000000000111111111',
'2025-06-06 09:00:00','b12ee775-e879-4a19-bf5e-4732902db289.png','00010100001010'),

('psych011','Pw011Aa',1,'吳佳玲','女','0900000011','psych011@mindcare.com.tw','諮商心理師證書第2011號',1,'新北市新莊區中正路 248 號',1900,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111111111111111111111111111111111111111111111111111111000000000',
'2025-07-07 09:00:00','66cf62cb-2aa0-4e53-999a-114f5dcf4ae1.png','00011100001011'),

('psych012','Pw012Aa',1,'劉冠廷','男','0900000012','psych012@mindcare.com.tw','諮商心理師證書第2012號',1,'新北市三重區重新路五段 609 號',2100,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000100000100000111111111001111111100111111110011111111000000000111111111',
'2025-08-08 09:00:00','adb0dc08-4004-4419-8b7a-2b4d3fb02813.png','00012100001012'),
('psych013','Pw013Aa',1,'周美芳','女','0900000013','psych013@mindcare.com.tw','諮商心理師證書第2013號',1,'臺北市中正區重慶南路一段 122 號',1500,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111111111111100000000011111101111111110000000001111110111111111000000000111111111',
'2025-09-09 09:00:00','f599ce11-74c9-41f9-add1-3bc72c68656c.png','00013100001013'),

('psych014','Pw014Aa',1,'蔡博文','男','0900000014','psych014@mindcare.com.tw','諮商心理師證書第2014號',1,'臺北市信義區松智路 1 號',2300,
'000000000111111111000000000111111111000000000111111111000000000111111111000000110001111111110000000000011111111100000000000111111111000000000111111111000000000111111111',
'2025-10-10 09:00:00','9d877e42-38f5-4043-8b03-5adc28aa56d8.png','00014100001014'),

('psych015','Pw015Aa',0,'許雅筑','女','0900000015','psych015@mindcare.com.tw','諮商心理師證書第2015號',1,'臺北市大安區敦化南路二段 207 號',1650,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111111110000000001111111111111100000000011111',
'2025-11-11 09:00:00','','00015100001015'),

('psych016','Pw016Aa',2,'鄭子軒','男','0900000016','psych016@mindcare.com.tw','諮商心理師證書第2016號',1,'臺中市西屯區台灣大道三段 99 號',1750,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000010000000001111111110000100000000011111111100000111111111',
'2025-12-12 09:00:00','','00016100001016'),

('psych017','Pw017Aa',1,'謝欣妤','女','0900000017','psych017@mindcare.com.tw','諮商心理師證書第2017號',0,'臺中市北區三民路三段 161 號',2000,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000001111111110000000001111111110000001111111000111111111000000000111111111',
'2026-01-13 09:00:00','304ed9f-b817-49e5-a9f6-3b40f97303df.png','00017100001017'),

('psych018','Pw018Aa',1,'楊智凱','男','0900000018','psych018@mindcare.com.tw','',1,'臺南市東區中華東路三段 336 號',1850,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111001111111110000000001111111110000000001111110000000111111111',
'2026-02-14 09:00:00','80964f5b-5673-4bce-8939-bac88f9e0efd.png','00018100001018'),

('psych019','Pw019Aa',1,'賴怡君','女','0900000019','psych019@mindcare.com.tw','',0,'臺南市中西區西門路二段 120 號',1950,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111011111111100000000011111111100000000011111111000000000111111111',
'2026-03-15 09:00:00','','00019100001019');

-- ==========================================
-- 心理師專長(psych_expertise)psych_all_expertise
-- ==========================================
CREATE TABLE psych_all_expertise (
  expertise_id   INT         NOT NULL AUTO_INCREMENT,
  expertise_name VARCHAR(50) NOT NULL,
  PRIMARY KEY (expertise_id)
);

INSERT INTO psych_all_expertise (expertise_name) VALUES
  ('焦慮與壓力管理'),
  ('憂鬱症輔導'),
  ('親子關係'),
  ('婚姻與家庭治療'),
('創傷後壓力症候群(PTSD)'),
('職場壓力'),
('人際關係'),
('成癮行為'),
('兒童與青少年心理'),
('老年心理');

-- ==========================================
-- 心理師擁有專長(psych_all_expertise)psych_expertise
-- ==========================================
CREATE TABLE psych_expertise (
  psych_id     INT NOT NULL,
  expertise_id INT NOT NULL,
  PRIMARY KEY (psych_id, expertise_id),
  CONSTRAINT fk_ep_psych     FOREIGN KEY (psych_id)     REFERENCES psychologist  (psych_id),
  CONSTRAINT fk_ep_expertise FOREIGN KEY (expertise_id) REFERENCES psych_all_expertise (expertise_id)
);

INSERT INTO psych_expertise (psych_id, expertise_id) VALUES
  (1, 1),
  (1, 2),
  (2, 2),
  (2, 3),
  (3, 1),
  (5,1),
(5,3),
(5,7),

(6,2),
(6,4),
(6,6),
(6,7),
(4,1),
(4,3),
(4,7),
(7,1),
(7,5),
(7,9),

(8,3),
(8,4),
(8,7),
(8,9),
(8,10),

(9,2),
(9,5),
(9,6),

(10,1),
(10,4),
(10,6),
(10,8),

(11,2),
(11,3),
(11,9),

(12,1),
(12,6),
(12,7),
(12,10),

(13,3),
(13,4),
(13,5),
(13,9),

(14,2),
(14,6),
(14,8),

(15,1),
(15,5),
(15,7),
(15,9),

(16,2),
(16,4),
(16,6),

(17,3),
(17,7),
(17,10),

(18,1),
(18,5),
(18,8),

(19,4),
(19,7),
(19,9),
(19,10);


-- ==========================================
-- 會員通知(member_notice)
-- ==========================================
CREATE TABLE member_notice (
  member_notice_id INT           NOT NULL AUTO_INCREMENT,
  member_id        INT           NOT NULL,
  admin_id         INT           NOT NULL,
  notice_content   VARCHAR(1000) NOT NULL,
  notice_type      TINYINT       NOT NULL,
  created_at       DATETIME      NOT NULL,
  is_read          BOOLEAN       NOT NULL DEFAULT 0,
  PRIMARY KEY (member_notice_id),
  CONSTRAINT fk_mn_member FOREIGN KEY (member_id) REFERENCES member (member_id),
  CONSTRAINT fk_mn_admin  FOREIGN KEY (admin_id)  REFERENCES admin  (admin_id)
);

INSERT INTO member_notice
  (member_id, admin_id, notice_content, notice_type, created_at, is_read)
VALUES
  (1, 1, '您的預約（陳雅婷心理師 / 2024-06-01 10:00）已確認，請準時出席。', 0, '2024-05-28 09:00:00', 1),
  (1, 2, '您報名的「親子溝通技巧」線上課程教材已上傳，請至課程頁面查看。', 1, '2026-07-05 11:20:00', 0),
  (2, 2, '您報名的「情緒管理工作坊」將於 2024-06-10 開課，請留意後續通知。', 1, '2024-06-01 11:30:00', 0),
  (3, 1, '本平台將於 2024-07-01 舉辦年度心理健康嘉年華，歡迎報名參加！',    2, '2024-06-15 08:00:00', 0),
  (4, 2, '您預約的心理諮商時段因心理師臨時請假，需另行改約，請至系統重新選擇時段。', 0, '2026-07-10 09:00:00', 0),
(5, 3, '您的心理諮商預約已確認，心理師將於指定時間提供服務。', 0, '2026-07-12 10:30:00', 1),

(6, 2, '您報名的「親子溝通技巧」線上課程教材已上傳，請至課程頁面查看。', 1, '2026-07-05 11:20:00', 1),
(7, 4, '您所報名的心理健康課程即將開始，請準時登入課程平台。', 1, '2026-07-08 15:00:00', 0),

(8, 1, '平台將於本月舉辦「心理健康講座」活動，歡迎會員踴躍報名參加。', 2, '2026-06-20 16:00:00', 1),
(3, 5, '本週末舉辦免費心理健康推廣活動，歡迎會員參加。', 2, '2026-07-15 09:30:00', 0);


-- ==========================================
-- 心理師通知(psychologist_notice)
-- ==========================================
CREATE TABLE psychologist_notice (
  psych_notice_id INT           NOT NULL AUTO_INCREMENT,
  psych_id        INT           NOT NULL,
  admin_id        INT           NOT NULL,
  notice_content  VARCHAR(1000) NOT NULL,
  notice_type     TINYINT       NOT NULL,
  created_at      DATETIME      NOT NULL,
  is_read         BOOLEAN       NOT NULL DEFAULT 0,
  PRIMARY KEY (psych_notice_id),
  CONSTRAINT fk_pn_psych FOREIGN KEY (psych_id)  REFERENCES psychologist (psych_id),
  CONSTRAINT fk_pn_admin FOREIGN KEY (admin_id)  REFERENCES admin        (admin_id)
);

INSERT INTO psychologist_notice
  (psych_id, admin_id, notice_content, notice_type, created_at, is_read)
VALUES
  (1, 1, '您投稿的文章「如何舒緩工作壓力」審核通過，已公開發布。',0, '2024-05-30 10:00:00', 0),
  (1, 1, '您投稿的文章「人生的意義」審核通過，已公開發布。',0, '2024-06-15 10:00:00', 0),
  (1, 1, '您稿的文章「學習愛人與被愛」審核通過，已公開發布。',0, '2024-06-30 10:00:00', 0),
  (2, 2, '會員林小華已預約您 2024-06-05 14:00 的諮詢時段，請確認行事曆。',1, '2024-06-01 13:00:00', 0),
  (3, 1, '您報名的「認知行為治療進階課程」已通過審核，開課日期為 2024-07-10。',2, '2024-06-10 09:00:00', 0),
  (5, 4, '您投稿的文章「認識焦慮症」審核通過，已公開發布。',0, '2026-06-02 09:30:00', 1),
(8, 2, '您投稿的文章「情緒壓力管理技巧」目前正在等待管理員審核。',0, '2026-07-01 13:20:00', 0),

(6, 2, '會員已成功預約您於2026-07-20 14:00的諮詢時段，請確認行事曆。',1, '2026-06-18 10:00:00', 1),
(9, 3, '您有新的諮商預約申請，請至心理師後台確認。',1, '2026-07-13 15:40:00', 0),

(7, 3, '您報名的「心理師專業成長課程」已通過審核，開課日期為2026-07-25。',2, '2026-06-25 09:00:00', 1),
(10, 5, '新的心理專業進修課程已開放報名，歡迎參加。',2, '2026-07-16 11:00:00', 0);

-- ==========================================
-- 文章分類(article_categories)
-- ==========================================
CREATE TABLE article_categories(
    article_cat_id INT PRIMARY KEY AUTO_INCREMENT,
    article_cat_name VARCHAR(50) NOT NULL,
    article_cat_status BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO article_categories (article_cat_name) VALUES 
('自我探索與成長'),
('職場壓力與焦慮'),
('親密關係與溝通'),
('大腦科學與心理學'),
('原生家庭與愛的分際'),
('改善焦慮與自己和解');

-- ==========================================
-- 文章(articles)
-- ==========================================
CREATE TABLE articles(
	article_id INT PRIMARY KEY AUTO_INCREMENT,
    parent_article_id INT,
    psych_id INT NOT NULL,
    article_cat_id INT,
    admin_id INT,
	cover_image LONGBLOB,
    title VARCHAR(50),
    article_content LONGTEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
	submitted_at DATETIME,
    reviewed_at DATETIME,
    published_at DATETIME,
    unpublished_at DATETIME,
    article_status TINYINT NOT NULL DEFAULT 0, -- 0草稿, 1送審中, 2審核通過, 3審核未通過, 4已下架
    reject_reason TINYINT,                     -- 0內容品質, 1違反專業法規, 2版權問題, 3違反平台規範, 4其他
    reject_note VARCHAR(200),
    view_count INT UNSIGNED NOT NULL DEFAULT 0,
    share_count INT UNSIGNED NOT NULL DEFAULT 0,
    CONSTRAINT fk_articles_psychologist
		FOREIGN KEY (psych_id) REFERENCES psychologist(psych_id),
    CONSTRAINT fk_articles_article_categories
		FOREIGN KEY (article_cat_id) REFERENCES article_categories(article_cat_id)
        ON DELETE RESTRICT,
	CONSTRAINT fk_articles_admin
		FOREIGN KEY (admin_id) REFERENCES admin(admin_id)
);

INSERT INTO articles (
    parent_article_id, psych_id, article_cat_id, admin_id, cover_image, title, 
    article_content, 
    created_at, updated_at, submitted_at, reviewed_at, published_at, unpublished_at, 
    article_status, reject_reason, reject_note, view_count, share_count
) VALUES 
-- 1. 大腦科學與心理學 (cat_id = 4)
(
NULL, 1, 4, 2, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover1.png'), '找不到原因的身體不適？解密現代人的隱形危機「自律神經失調」',
'<h3 data-path-to-node="1">身體在抗議，心靈在呼救</h3>
<p>你是否經常感到胸悶、心悸、莫名頭暈、失眠，或者腸胃不適，但跑遍了各大醫院做全身檢查，報告卻都顯示一切正常？醫生最後只能對你說：「這可能只是壓力太大。」 <br>在心理學與醫學交織的領域中，這種找不到器官實質病變、卻有一大堆痛苦症狀的現象，往往就是「自律神經失調」的典型表現。自律神經就像是身體的自動導航系統，當這套系統因長期壓力而全面失控，身體就會陷入停不下來的混亂中。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">交感與副交感神經的「油門與煞車」失衡，交感神經（身體的油門）過度踩死</h4>
<p data-path-to-node="2">當我們面對職場壓力、人際拉扯 or 對未來的焦慮時，交感神經會強制讓身體處於「戰或逃」的備戰狀態，導致心跳加速、肌肉緊繃。如果油門踩久了放不開，人就會變得極度神經質與焦慮。 副交感神經（身體的煞車）完全失靈：副交感神經負責讓我們放鬆、休息與消化。當它失去作用，即使你躺在床上，大腦和身體依然在瘋狂運轉，導致「明明身體累到快崩潰，大腦卻醒著無法入睡」的慢性失眠。 長期內耗引發全身性抗議：自律神經失調並不是一種特定的「疾病」，而是一組「症狀群」。從頭痛、耳鳴、恐慌感、胃食道逆流到手腳冰冷，都是身體在用它僅剩的力氣，對你發出「該停下來」的紅色警訊。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">自律神經失調自我評測表：</h4>
<p><img src="/article-images/0ce66e2e-c49c-4107-b8a7-5bc246ea94f4.png" alt="" width="727" height="484"></p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">親愛的，這不是你想太多，你的身體真的累了。</h4>
<p data-path-to-node="4">自律神經失調，其實是心靈藉由身體在向你發出求救訊號。盲目地吃藥只能壓制症狀，找出底層的壓力源、學會安撫內心的焦慮，才是重啟大腦煞車機制的關鍵。如果你正被這些找不到原因的身體不適折磨得無助又疲憊，請記得，你不需要一個人硬撐。平台的專業諮商心理師隨時在這裡，願意陪伴你一起梳理積壓的情緒、調節神經系統，陪你重新找回身體與心靈的平衡安全感。</p>',
'2026-07-08 07:03:11', '2026-07-08 07:38:15', '2026-07-08 07:38:15', '2026-07-08 07:50:38', '2026-07-08 07:50:38', NULL, 
2, NULL, NULL, 525, 70
),

-- 2. 職場壓力與焦慮 (cat_id = 2)
(
NULL, 1, 2, 1, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover2.png'), '總是覺得自己不夠好？擺脫「冒牌者症候群」的職場內耗',
'<h3 data-path-to-node="1">明明很努力，卻總害怕被拆穿？</h3>
<p>在職場上，你是否常有這種感覺：當獲得主管稱讚或成功完成專案時，心裡第一時間想到的不是驕傲，而是「我只是運氣好罷了」、「下次可能就沒這麼幸運了」？ <br>這種無法將成功歸因於自身能力的心理現象，就是心理學上著名的「冒牌者症候群」（Impostor Syndrome）。這類人在外人眼中往往表現優秀、做事可靠，但內心卻無時無刻不被焦慮籠罩，深怕有一天別人會發現自己的「真面目」。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">為什麼我們會陷入「冒牌者」的思維陷阱？完美主義與極端評價</h4>
<p data-path-to-node="2">這種心理狀態通常源於極高標準的完美主義，或是成長過程中過度強調「成就價值」的環境。當我們把成功看作理所當然，而把微小的瑕疵視為致命失敗時，大腦就會開始過度檢視自己的短處。 長期處於冒牌者症候群中，容易引發嚴重的職場內耗，甚至導致過度努力以求掩飾、或是因害怕失敗而嚴重拖延的兩種極端行為。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">重構自我價值感的實踐法則：</h4>
<p data-path-to-node="4"><strong>1. 分清「事實」與「感覺」：</strong>「我覺得自己不行」是一種情緒感覺，並不等於客觀事實。當這種想法浮現時，試著在心中問自己客觀證據在哪裡。 <br><strong>2. 建立自己的成就記憶庫：</strong>將過去收到的讚美、完成的專案回饋記錄下來。當自我懷疑襲來時，拿出實體紀錄提醒自己。 <br><strong>3. 重塑對不完美的定義：</strong>優秀的人並非什麼都會，允許自己有知識盲區，將不懂視為成長的起點。 <br><strong>4. 練習坦然接收讚美：</strong>下次當別人誇獎你時，試著把「沒有啦，只是運氣好」換成堅定而有禮貌的「謝謝你，我也付出了很多努力」。</p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">你所擁有的成就與位置，都是憑實力得來的。</h4>
<p data-path-to-node="4">停止對自己的嚴苛審判，試著像對待最珍貴的朋友一樣，給予自己客觀而溫柔的肯定。平台的專業諮商心理師隨時在這裡，陪伴你一起解開內心的緊箍咒，重新找回自信與職場安全感。</p>',
'2026-07-10 09:15:20', '2026-07-10 10:00:00', '2026-07-10 10:00:00', '2026-07-10 11:30:00', '2026-07-10 11:30:00', NULL, 
2, NULL, NULL, 680, 180
),

-- 3. 親密關係與溝通 (cat_id = 3)
(
NULL, 1, 3, 2, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover3.png'), '為什麼我們越愛越常吵架？談情侶溝通中的「非暴力溝通」',
'<h3 data-path-to-node="1">我們是在溝通，還是在爭輸贏？</h3>
<p>「你每次都不關心我！」、「你到底想怎樣？」這些對話是否聽起來很熟悉？在親密關係中，我們往往因為距離太近、期待太高，不小心用指責代替了表達需求，讓原本出於愛的關心，變成了傷人的武器。 <br>當爭執發生時，大腦中的防禦機制會被啟動，我們開始急於防衛自己或攻擊對方，結果溝通變成了對錯之爭。最終就算贏了道理，卻輸掉了彼此之間的信任與親密感。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">何謂「非暴力溝通」？核心在於連結而非戰勝</h4>
<p data-path-to-node="2">知名心理學家馬歇爾．盧森堡提出的「非暴力溝通」（Nonviolent Communication），旨在幫助我們脫離習慣性的評判與防禦，專注於彼此真實的情緒與深層需求。這不是教你委屈求全，而是教你如何清晰且不帶攻擊性地說出真心話。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">實踐非暴力溝通的四個步驟：</h4>
<p data-path-to-node="4"><strong>1. 客觀觀察（Observation）：</strong>描述發生的事實，不帶任何批判或誇大詞彙。例如：「你這週有三天晚上超過十點才回到家。」 <br><strong>2. 覺察感受（Feeling）：</strong>表達當下的真實情緒，而非對對方的評價。例如：「我覺得有點孤單，也有點擔心。」 <br><strong>3. 釐清需求（Need）：</strong>找出情緒背後未被滿足的核心需求。例如：「因為我很重視我們每天能一起放鬆聊天、吃晚飯的時間。」 <br><strong>4. 提出具體請求（Request）：</strong>提出明確、正向且對方可執行的行動要求。例如：「這週五晚上，我們一起在家煮晚餐、不接工作電話，好嗎？」</p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">讓愛重新順暢流動，給彼此一個被聽見的機會。</h4>
<p data-path-to-node="4">好的溝通不是為了說服對方承認錯誤，而是為了讓彼此在安全感中被聽見與理解。當你改變了說話的方式，關係中的防禦堡壘就會漸漸瓦解。如果你們正卡在重複的衝突迴圈中，歡迎預約諮商，讓心理師陪伴你們建立專屬的溝通橋樑。</p>',
'2026-07-12 14:20:00', '2026-07-12 15:10:00', '2026-07-12 15:10:00', '2026-07-12 16:00:00', '2026-07-12 16:00:00', NULL, 
2, NULL, NULL, 750, 240
),

-- 4. 原生家庭與愛的分際 (cat_id = 5)
(
NULL, 1, 5, 1, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover4.png'), '愛與控制的界線在哪裡？學會設立「健康的心理界線」',
'<h3 data-path-to-node="1">拒絕父母，不等於我不孝順</h3>
<p>「我們這都是為了你好！」、「你這樣做真的很讓我失望……」這些話語是否曾讓你感到無比沉重？許多人在成長過程中，常因為無法達到父母的期待而背負強烈的罪惡感，甚至在成年後，依然難以獨立決定自己的職涯或生活型態。 <br>這種現象往往源於原生家庭中界線模糊（Enmeshment）。當父母將自己的喜怒哀樂與孩子的選擇過度綑綁時，愛就漸漸演變成了無形的控制，讓孩子在追求自我與維護孝道之間陷入撕裂般的痛苦。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">什麼是健康的「心理界線」？劃清個人情緒責任</h4>
<p data-path-to-node="2">心理界線（Psychological Boundaries）就像是一扇門，決定了什麼可以進入你的內心世界，什麼該被擋在外面。建立界線並不是要切斷與家人的關係，而是明確區分什麼是我的責任，什麼是父母的責任。 你需要明白，父母有表達關心與期待的自由，但你也有選擇自己人生軌跡的權利。承接父母的情緒並非你的義務，你不需要為他們的失落感負全責。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">如何溫和而堅定地建立心理界線：</h4>
<p data-path-to-node="4"><strong>1. 劃分情緒責任：</strong>當父母因你的決定而不高興時，試著在內心提醒自己，我尊重他們的感受，但我不需要為了消解他們的情緒而犧牲自己的人生。 <br><strong>2. 練習溫和拒絕：</strong>面對不合理要求時，不必立刻反駁或順從。可以用「我知道你們關心我，但我需要時間思考」來爭取空間。 <br><strong>3. 保持健康的物理與心理距離：</strong>當談話轉變為情感勒索時，你有權利禮貌地暫時結束對話，保護自己的情緒能量。</p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">設立界線不是為了推開家人，而是為了完整做你自己。</h4>
<p data-path-to-node="4">只有當你學會尊重自己的需求，才能真正建立健康且長久的家庭關係。解開原生家庭的枷鎖需要勇氣，心理諮商能提供一個安全的空間，陪伴你一步一步建構起屬於你的健康界線。</p>',
'2026-07-15 10:00:00', '2026-07-15 11:20:00', '2026-07-15 11:20:00', '2026-07-15 14:00:00', '2026-07-15 14:00:00', NULL, 
2, NULL, NULL, 620, 150
),

-- 5. 改善焦慮與自己和解 (cat_id = 6)
(
NULL, 1, 6, 2, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover5.png'), '當焦慮海嘯襲來：3個能立刻著地的「正念接地法」',
'<h3 data-path-to-node="1">大腦轉個不停，該如何讓自己停下來？</h3>
<p>深夜裡，你是否也曾因為對未來的失控感而呼吸急促、胸口發緊，大腦不斷播放各種最壞的打算？焦慮是一種專注於「尚未發生的威脅」的心理預警機制。當我們過度沉溺於未來的恐懼時，大腦的神經系統會誤以為我們正面臨即刻的生命危險。 <br>要破解這種焦慮風暴，最有效的切斷機制就是將專注力從抽象的想像拉回當下的身體感受。這就是心理學上常說的著地（Grounding）過程。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">重啟平靜神經系統的實用著地技巧：</h4>
<p data-path-to-node="2"><strong>1. 5-4-3-2-1 五感接地法：</strong>當焦慮襲來時，環顧四周並依序找尋：5個看得到的物體、4個摸得到的觸感、3個聽得到的聲音、2個聞得到的氣味、1個嚐到的味道。透過強制調動五感，大腦會意識到「我現在很安全」。 <br><strong>2. 腹式呼吸法（4-7-8呼吸）：</strong>吸氣4秒，憋氣7秒，慢吐氣8秒。延長吐氣的時間能有效刺激副交感神經，強制放慢心跳，舒緩身體的戰逃反應。 <br><strong>3. 漸進式肌肉鬆弛練習：</strong>將雙肩用力往上聳緊5秒，然後瞬間完全放鬆。體驗緊繃與鬆弛之間的差異，讓累積在身體裡的焦慮張力釋放出來。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">焦慮就像暴風雨，你不需要試圖去控制風向。</h4>
<p data-path-to-node="4">你只需要找到一把傘，靜靜地陪伴自己度過這個時刻。這些接地技巧就是你的安全傘，隨時能帶你回歸內心的平靜。如果焦慮情況持續影響生活品質，建議透過專業心理諮商進行深層的心靈整理與自我和解。</p>',
'2026-07-18 16:30:00', '2026-07-18 17:00:00', '2026-07-18 17:00:00', '2026-07-18 18:00:00', '2026-07-18 18:00:00', NULL, 
2, NULL, NULL, 790, 290
),

-- 6. 自我探索與成長 (cat_id = 1)
(
NULL, 1, 1, 1, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover6.png'), '擺脫迷惘與盲從：如何透過「價值觀澄清」找回人生方向？',
'<h3 data-path-to-node="1">你現在過的生活，是自己想要的，還是社會期望的？</h3>
<p>在忙碌的日常生活中，你是否偶爾會產生一種空虛感，覺得自己每天都在盲目地奔波，卻不知道自己究竟為了什麼而努力？ <br>這種迷惘感往往源於我們的「行為」與內心深處的「核心價值觀」產生了脫節。當我們習慣順應社會的期待、父母的期許或同儕的眼光時，就容易迷失自我，甚至在達成目標後依然感到失落。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">什麼是「價值觀澄清」？找到屬於你的內在指南針</h4>
<p data-path-to-node="2">價值觀澄清（Value Clarification）是自我探索中非常關鍵的一環。價值觀並非高不可攀的道德口號，而是你在做決策、分配時間與選擇人生路徑時，最看重的底層原則（例如：自由、安全感、創造力、親密關係或影響力）。 理解自己的核心價值觀，就像在迷霧中獲得了一把導航指南針，能幫助你在面臨重大抉擇時，做出最符合真實內心的選擇。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">三步進行內在價值觀梳理：</h4>
<p data-path-to-node="4"><strong>1. 回顧巔峰與低谷時刻：</strong>思考過去最讓你感到充滿活力與成就感的時刻，那時滿足了你的什麼價值？再回顧最讓你沮喪痛苦的時刻，當時又是什麼價值受到了壓迫？ <br><strong>2. 篩選核心優先順序：</strong>列出10個你認同的價值關鍵字，嘗試強制排序，挑選出最不可退讓的「前3名核心價值」。 <br><strong>3. 檢視現狀並進行微調：</strong>檢視目前的日常生活，時間與精力是否有投注在你的核心價值上？如果沒有，嘗試做出微小的調整。 <br></p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">自我探索是一場持續終生的旅程。</h4>
<p data-path-to-node="4">允許自己隨時停下來檢視與修正方向。透過心理諮商的對話與引導，能幫助你更清晰地看見內心渴望，活出具備主體性與滿足感的人生。</p>',
'2026-07-20 08:30:00', '2026-07-20 09:10:00', '2026-07-20 09:10:00', '2026-07-20 10:00:00', '2026-07-20 10:00:00', NULL, 
2, NULL, NULL, 580, 130
),

-- 7. 自我探索與成長 (cat_id = 1) | Psych 3
(
NULL, 3, 1, 2, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover7.png'), '允許自己「今天先這樣」：停止完美主義的自我批判與內耗',
'<h3 data-path-to-node="1">你是不是也習慣對自己太嚴苛？</h3>
<p>「這件事我應該做得更好」、「我又搞砸了，我真的很沒用……」這些聲音是否經常在你腦海中輪播？許多長期受焦慮所苦的人，背後都有一個極度苛刻的內在審判官。我們常誤以為對自己嚴格才會進步，但事實上，過度的完美主義只會帶來拖延、恐懼與無休止的情緒消耗。 <br>當我們把自我價值完全建立在表現完美上時，任何微小的失誤都會被放大成對個人能力的全面否定，最終讓人精疲力竭。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">學習「自我關懷」（Self-Compassion）的三個核心階段：</h4>
<p data-path-to-node="2"><strong>1. 對自己溫柔（Self-Kindness）：</strong>試著用對待最親密朋友的方式對待自己。當朋友犯錯時，你不會痛罵他，而是安慰他「沒關係，你已經盡力了」，對待自己也請保持這份體貼。 <br><strong>2. 理解這是人類的共通性（Common Humanity）：</strong>明白挫折、不完美與焦慮是所有人共同的生活體驗。你所經歷的掙扎並不代表你特別失敗，你並不孤單。 <br><strong>3. 正念接納（Mindfulness）：</strong>對當下的負面情緒保持客觀覺察，不壓抑也不放大。試著跟自己說：「是的，我現在感到很沮喪，但這只是一種暫時的情緒，它終究會過去的。」</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">允許自己暫停，給心靈一個喘息修復的空間。</h4>
<p data-path-to-node="4">接納不完美的自己，不是放棄努力，而是給心靈一個喘息與修復的空間。今天晚上，試著對自己說一句：「今天已經做得夠好了，允許自己今天先這樣吧。」若您想了解更多如何安撫內在批判者的技巧，諮商心理師隨時準備陪伴您踏上自我和解的旅程。</p>',
'2026-04-21 10:15:00', '2026-04-21 11:00:00', '2026-04-21 11:00:00', '2026-04-21 12:00:00', '2026-04-21 12:00:00', NULL, 
2, NULL, NULL, 320, 42
),

-- 8. 職場壓力與焦慮 (cat_id = 2) | Psych 5
(
NULL, 5, 2, 1, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover8.png'), '下班後依然靜不下心？建立「職場與生活」的心理防火牆',
'<h3 data-path-to-node="1">明明已經離開辦公室，心卻還在加班？</h3>
<p>現代人通訊軟體不離身，模糊了工作與生活的界線。你是否常在下班後，一看到社群訊息跳出就心跳加速，或是躺在床上依然在大腦裡演算明天的待辦事項？ <br>這種「永遠在線」的生理警戒狀態，會持續消耗大腦的神經資源，長期下來容易引發嚴重的職業倦怠（Burnout）與慢性疲勞。要保護自己的心靈健康，我們必須主動打造一套「心理儀式感」，強制大腦進行下班轉換。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">劃清邊界：打造個人過渡儀式</h4>
<p data-path-to-node="2">大腦需要明確的訊號來判斷危險（工作）已經結束。如果沒有明確的儀式，大腦會誤以為你依然處於戰鬥狀態。建立防火牆並非要你完全不負責任，而是為了讓你在休息時深度充電，進而在工作時保持更持久的專注力與創造力。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">實踐心理過渡的三大策略：</h4>
<p data-path-to-node="4"><strong>1. 創造實體轉場儀式：</strong>下班離開公司前，將桌面整理乾淨，並寫下明天的前三優先事項。回家後立刻換上居家服、洗個熱水澡，用肢體動作暗示大腦「角色已切換」。 <br><strong>2. 數位降噪與界線設定：</strong>設定工作通訊軟體的靜音時段，並向團隊明確溝通緊急聯繫管道，減少無意義的訊息焦慮。 <br><strong>3. 培育非生產導向的興趣：</strong>找尋一件「不需要計算績效與回報」的事情（如烹飪、園藝、運動），讓大腦的預設模式網路（DMN）獲得真正的修復。</p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">工作只是生活的一部分，而不是你的全部。</h4>
<p data-path-to-node="4">學會在下班後卸下專業角色的重擔，給自己充裕的休養空間。如果你發現自己長期無法脫離職場焦慮、甚至出現嚴重失眠，歡迎預約心理諮商，讓我們一起探索適合你的職涯減壓之道。</p>',
'2026-05-22 09:00:00', '2026-05-22 09:40:00', '2026-05-22 09:40:00', '2026-05-22 10:30:00', '2026-05-22 10:30:00', NULL, 
2, NULL, NULL, 410, 85
),

-- 9. 親密關係與溝通 (cat_id = 3) | Psych 8
(
NULL, 8, 3, 2, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover9.png'), '解密「成人依附理論」：為什麼我在愛情裡總是缺乏安全感？',
'<h3 data-path-to-node="1">為什麼越在乎，越容易把對方推開？</h3>
<p>在感情中，你是否常因為伴侶慢回訊息而感到鋪天蓋地的焦慮，忍不住狂發訊息？或者當兩人出現衝突時，你的第一反應是關閉心門、冷漠閃躲，覺得「我自己一個人處理就好」？ <br>這些在親密關係中展現的防衛機制，大多源於我們早年的依附經驗。心理學家約翰·鮑比（John Bowlby）提出的依附理論，揭示了我們如何在成年後的愛情中，複製童年與照顧者互動的心理模式。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">三種主要的成人依附風格：</h4>
<p data-path-to-node="2"><strong>1. 焦慮型依附（Anxious Attachment）：</strong>渴望極度親密，對伴侶的冷淡高度敏感，常藉由討好或發脾氣來確認對方的愛。 <br><strong>2. 逃避型依附（Avoidant Attachment）：</strong>過度強調獨立，害怕過度靠近會失去自我，遇到情緒衝突時習慣壓抑與撤退。 <br><strong>3. 安全型依附（Secure Attachment）：</strong>能坦然表達脆弱，既不害怕親密，也不恐懼孤獨，相信自己值得被愛。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">從不安全走向安全依附的重構之路：</h4>
<p data-path-to-node="4">理解自己的依附風格並非為了貼標籤，而是看清情緒背後的渴望。焦慮型需要學習自我安撫與給予空間，逃避型則需要練習覺察感受並勇敢表達需求。當我們開始對自己的不安保持正念，就能打破無意識的感情代償迴圈。</p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">安全感是可以透過後天學習重新修復的。</h4>
<p data-path-to-node="4">在一段具備包容力的關係中，或是透過專業心理諮商的陪伴，我們能夠重新體驗被理解與被接納的過程，建立起健康而穩固的安身之處。</p>',
'2026-04-23 13:15:00', '2026-04-23 14:00:00', '2026-04-23 14:00:00', '2026-04-23 15:30:00', '2026-04-23 15:30:00', NULL, 
2, NULL, NULL, 390, 72
),

-- 10. 大腦科學與心理學 (cat_id = 4) | Psych 2
(
NULL, 2, 4, 1, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover10.png'), '多巴胺與快樂的陷阱：為什麼我們總是滑手機滑到停不下來？',
'<h3 data-path-to-node="1">無意識地刷社群，真的讓你放鬆了嗎？</h3>
<p>結束了一整天疲憊的工作，你躺在沙發上打開手機，原本只想滑個五分鐘，卻不知不覺過了兩個小時。奇怪的是，關掉螢幕後，你並沒有感到精力充沛，反而覺得更加空虛與疲憊。 <br>這並不是因為你意志力薄弱，而是你的大腦陷入了現代科技精心設計的「多巴胺迴圈」（Dopamine Loop）中。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">多巴胺不是「快樂分子」，而是「預期分子」</h4>
<p data-path-to-node="2">許多人誤以為多巴胺（Dopamine）帶來的是滿足感，但大腦神經科學證實，多巴胺主要負責驅動「渴望與尋求」。當你下滑螢幕時，大腦無法預知下一支影片是什麼，這種「不確定的獎勵機制」會刺激多巴胺大量分泌，誘使你不斷尋找下一次刺激。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">重塑大腦獎勵機制的「多巴胺重置」練習：</h4>
<p data-path-to-node="4"><strong>1. 移除無意識觸發源：</strong>將手機社群 App 的通知完全關閉，或是把螢幕調成黑白模式，降低視覺刺激。 <br><strong>2. 延遲滿足感：</strong>當出現「想拿手機」的衝動時，試著在心裡默數 60 秒，給前額葉皮質（理性大腦）重新搶回主導權的時間。 <br><strong>3. 替換為「慢多巴胺」活動：</strong>透過閱讀、手作、戶外運動等需要持續投入專注力的活動，獲得持久而穩定的內在平靜。</p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">奪回專注力的主導權，才能擁有真實的生活體驗。</h4>
<p data-path-to-node="4">科技應當是輔助生活的工具，而非支配情緒的主人。透過覺察大腦的神經機制，我們能逐步擺脫數位成癮，重新在現實生活中尋得深層的幸福感。</p>',
'2026-05-24 10:20:00', '2026-05-24 11:00:00', '2026-05-24 11:00:00', '2026-05-24 13:00:00', '2026-05-24 13:00:00', NULL, 
2, NULL, NULL, 450, 95
),

-- 11. 大腦科學與心理學 (cat_id = 4) | Psych 7
(
NULL, 7, 4, 2, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover11.png'), '慢性壓力如何重塑大腦？解析皮質醇對記憶與情緒的生理影響',
'<h3 data-path-to-node="1">最近總是記性變差、容易發脾氣？</h3>
<p>你是否覺得最近工作效率暴跌、經常剛講過的事情轉頭就忘，甚至對身邊的人失去了耐心？這可能不是你變笨了，而是大腦正在遭受慢性壓力荷爾蒙的侵蝕。 <br>當我們面對持續性壓力時，腎上腺皮質會大量分泌皮質醇（Cortisol）。短期分泌皮質醇能激發潛能應對危機，但若長期處於高濃度狀態，將對大腦結構造成不可逆的負面影響。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">皮質醇對大腦三大區域的破壞：</h4>
<p data-path-to-node="2"><strong>1. 海馬迴萎縮（記憶力衰退）：</strong>海馬迴負責長期記憶與學習，過量的皮質醇會破壞海馬迴的神經細胞連結，導致記憶力下滑。 <br><strong>2. 杏仁核過度活化（情緒失控）：</strong>杏仁核是大腦的情緒恐懼中心，高皮質醇會使其變得極度敏感，讓人更容易感到恐慌、焦慮與暴躁。 <br><strong>3. 前額葉皮質功能受抑制（決策力下降）：</strong>負責邏輯分析與理性控制的前額葉皮質在壓力下會暫停運作，使我們難以做出正確的長遠規劃。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">利用「神經可塑性」修復高壓大腦：</h4>
<p data-path-to-node="4">幸運的是，大腦具備極強的神經可塑性（Neuroplasticity）。透過每週三次 30 分鐘的有氧運動、每天 10 分鐘的正念冥想，以及補充足夠的 Omega-3 與優質睡眠，能有效降低體內皮質醇濃度，促進大腦衍生神經滋養因子（BDNF）的分泌，重啟大腦的自我修復機制。</p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">給大腦修復的時間，就是對自己最大的善意。</h4>
<p data-path-to-node="4">意識到壓力帶來的生理轉變，是改變的第一步。請給予自己喘息的空間與時間，讓過熱的大腦神經系統重新降溫恢復健康。</p>',
'2026-03-25 15:00:00', '2026-03-25 15:45:00', '2026-03-25 15:45:00', '2026-03-25 17:00:00', '2026-03-25 17:00:00', NULL, 
2, NULL, NULL, 290, 40
),

-- 12. 原生家庭與愛的分際 (cat_id = 5) | Psych 10
(
NULL, 10, 5, 1, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover12.png'), '走出「討好型人格」：不再為了成全他人而委屈自己',
'<h3 data-path-to-node="1">習慣性討好，其實是內心深處的生存策略</h3>
<p>在團體中，你是否總扮演那個「好說話」的配合者？即使內心一萬個不願意，當別人提出要求時，嘴巴依然會自動回答「沒問題」；甚至當發生衝突時，不管誰對誰錯，你總是第一個道歉的人。 <br>這種「討好型人格」（People Pleaser）常源於童年時期缺乏無條件的接納。我們從小學會了「只有當我乖巧、聽話、不帶來麻煩時，我才是安全且值得被愛的」，進而在成年後將這種討好模式帶入所有際遇中。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">討好背後的代價：自我消融與隱性怨恨</h4>
<p data-path-to-node="2">長期壓抑自己的需求去迎合他人，就像持續從空桶裡倒水，最終會引發嚴重的心理枯竭。更糟糕的是，討好者內心往往會積累起無法言語的隱性怨恨：「為什麼我對你這麼好，你卻從不考慮我的感受？」這種不健康的互動模式最終會毒害所有關係。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">重構自我價值的練習心法：</h4>
<p data-path-to-node="4"><strong>1. 覺察「自動化回應」：</strong>下次當別人提出請求時，練習不要立刻回答。用「我需要先確認一下行程，稍後回覆你」來爭取思考時間。 <br><strong>2. 區分「拒絕要求」與「拒絕這個人」：</strong>明白拒絕別人的合理要求不等於你不善良，也不代表你是在攻擊對方。 <br><strong>3. 接納被討厭的勇氣：</strong>別人的情緒是別人的課題，你不需要也不可能讓全世界的所有人都滿意你。</p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">最需要你溫柔對待的人，其實是你自己。</h4>
<p data-path-to-node="4">停止用無止境的委屈來換取認同。當你開始尊重自己的界線與需求，身邊才會吸引來真正尊重並珍視你原本樣貌的人。</p>',
'2026-03-26 08:40:00', '2026-03-26 09:20:00', '2026-03-26 09:20:00', '2026-03-26 10:15:00', '2026-03-26 10:15:00', NULL, 
2, NULL, NULL, 360, 62
),

-- 13. 親密關係與溝通 (cat_id = 3) | Psych 4
(
NULL, 4, 3, 2, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover13.png'), '接納脆弱的勇氣：自我關懷如何幫助我們走出感情低潮？',
'<h3 data-path-to-node="1">當感情跌入谷底，你選擇鞭策還是擁抱自己？</h3>
<p>當面臨失戀、被背叛或關係破裂時，你腦海裡第一個出現的聲音是什麼？是「你真沒用，怎麼連這點感情都處理不好」，還是「這真的很艱難，但沒關係，我會陪著你」？ <br>大多數人習慣用嚴厲的批判來面對失敗，以為唯有對自己狠一點才能重新站起來。然而心理學研究發現，過度的自我鞭策只會激活大腦的威脅系統，引發更多焦慮與防禦行為。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">什麼是「自我關懷」（Self-Compassion）？</h4>
<p data-path-to-node="2">德州大學的克莉絲汀·內夫博士（Dr. Kristin Neff）指出，自我關懷絕非自我憐憫或軟弱，而是一種強大的復原力（Resilience）。它包含三個不可或缺的要素：自我溫柔、理解人類共通性，以及正念覺察。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">走出低潮的自我關懷三部曲：</h4>
<p data-path-to-node="4"><strong>1. 以自我溫柔取代自我批判：</strong>當挫折發生時，將手放在心口上，深呼吸並對自己說一些溫暖、體貼的話語，就像你在安慰受傷的好朋友一樣。 <br><strong>2. 認知痛苦是人類的共通體驗：</strong>提醒自己受挫與不完美是每個人都會經歷的旅程，你現在所體驗到的痛苦並不代表你特別失敗，你並不孤單。 <br><strong>3. 保持正念覺察：</strong>不誇大也不壓抑當下的痛苦感受，給予情緒一個合適的空間，觀察它如雲朵般升起又消散。</p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">脆弱並非軟弱，而是展現真正勇氣的起點。</h4>
<p data-path-to-node="4">允許自己在受傷時休息，給予內心源源不絕的理解與支持。當你學會成為自己最堅實的後盾，低潮將不再是無法跨越的絕壁，而是轉化成長的契機。</p>',
'2026-06-27 14:10:00', '2026-06-27 15:00:00', '2026-06-27 15:00:00', '2026-06-27 16:20:00', '2026-06-27 16:20:00', NULL, 
2, NULL, NULL, 310, 50
),

-- 14. 改善焦慮與自己和解 (cat_id = 6) | Psych 6
(
NULL, 6, 6, 1, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover14.png'), '睡前總是想太多？給失眠焦慮者的夜間放鬆指南',
'<h3 data-path-to-node="1">為什麼一關燈，大腦就開始播映反芻電影？</h3>
<p>深夜裡，身體明明已經疲憊不堪，但只要一躺上床，大腦就開始瘋狂運轉：重複播放白天講錯的話、擔心明天的會議，甚至焦慮「如果我現在還睡著明天一定會完蛋」。 <br>這種對失眠的恐慌，會讓交感神經高度興奮，形成「越想睡越睡不著」的惡性循環。要把床從「焦慮戰場」重新變回「休息聖地」，我們需要對大腦進行睡眠衛生的重新設定。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">打破睡前反芻思考的刺激控制療法：</h4>
<p data-path-to-node="2">大腦具有極強的聯想能力。如果你經常躺在床上焦慮，大腦就會把「床」與「焦慮警戒」綁定在一起。要打破這個連結，首要原則就是：只有在真正產生睡意時才躺上床，絕不在床上進行工作、看電視或陷入長時間的思考。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">今晚就能嘗試的夜間放鬆三步驟：</h4>
<p data-path-to-node="4"><strong>1. 建立「大腦垃圾桶」（Brain Dump）：</strong>睡前準備一本筆記本，將腦海中所有未完成的事項與擔憂全部寫下來，告訴自己「這些事已經被妥善記錄，明天起床後再處理」。 <br><strong>2. 實施 20 分鐘離床原則：</strong>若躺在床上超過 20 分鐘依然毫無睡意，請立刻起身離開臥室，在微弱燈光下進行靜態活動（如聽輕音樂、讀書），待睡意襲來再回到床上。 <br><strong>3. 進行身體掃描冥想：</strong>將注意力從抽象的思考轉移到身體部位，從腳趾開始一步步向上感知並放鬆每個肌群，引導身體進入睡眠準備。</p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">今夜，請放心地將世界交付出去。</h4>
<p data-path-to-node="4">睡眠是身體自我修復的聖殿。放下對掌控一切的執念，允許自己在這個夜晚什麼都不做，安心地沉入平靜的夢鄉。</p>',
'2026-05-28 21:00:00', '2026-05-28 21:30:00', '2026-05-28 21:30:00', '2026-05-28 22:10:00', '2026-05-28 22:10:00', NULL, 
2, NULL, NULL, 480, 110
),

-- 15. 職場壓力與焦慮 (cat_id = 2) | Psych 1 
(
NULL, 1, 2, 2, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover15.png'), '轉職期的心理陣痛：如何面對不確定性帶來的未知焦慮？',
'<h3 data-path-to-node="1">站在人生的十字路口，你是否感到恐懼與孤立？</h3>
<p>無論是被動面臨產業變革，或是主動選擇跨出舒適圈，轉職期往往是人生中最充滿不安與挑戰的階段。離開了原本熟悉的職稱、團隊與薪資保障，我們常會產生一種「自我身分認同解體」的失落感。 <br>對未知未來的失控感，容易讓人陷入「萬一找不到更好的怎麼辦」的災難化思維。這種心理陣痛是每位求變者都會經歷的正常心理轉折。</p>
<p>&nbsp;</p>
<h4 data-path-to-node="2">理解渡過過渡期（Transition）的三個心理階段：</h4>
<p data-path-to-node="2">威廉·布里奇斯（William Bridges）的過渡理論指出，任何改變都包含三個階段：舊局面的終結（Ending）、中立地帶的迷惘（Neutral Zone），以及新局面的開始（New Beginning）。多數人的焦慮源於試圖跳過中立地帶，急於求成而做出草率決策。</p>
<p data-path-to-node="2">&nbsp;</p>
<h4 data-path-to-node="4">轉職過渡期的心理調適實踐策略：</h4>
<p data-path-to-node="4"><strong>1. 盤點「可遷移技能」（Transferable Skills）：</strong>釐清你的核心價值並不等同於過去的公司名稱或職稱。溝通協調、問題解決與學習能力是能跟隨你一生的資本。 <br><strong>2. 設定微型目標（Micro-Goals）：</strong>將龐大的轉職計畫拆解為每天可執行的小任務（如修正一份履歷、聯繫一位業界前輩），透過微小的進展重新建立控制感。 <br><strong>3. 尋求外部支持網絡：</strong>不要獨自承擔焦慮，積極與親友、同行或專業諮商師聊聊，獲得客觀的反饋與心理支持。</p>
<h4 data-path-to-node="4">&nbsp;</h4>
<h4 data-path-to-node="4">迷惘是成長的必經之路，代表你正在朝更好的自己邁進。</h4>
<p data-path-to-node="4">勇敢跨越不安的迷霧，給予自己探索的時間與耐心。每一次對現狀的重新選擇，都是你重塑人生精彩篇章的契機。</p>',
'2026-04-29 11:30:00', '2026-04-29 12:00:00', '2026-04-29 12:00:00', '2026-04-29 13:30:00', '2026-04-29 13:30:00', NULL, 
2, NULL, NULL, 260, 32
),
-- 16 已下架 --
(
    NULL, 1, 2, 2, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover16.png'), '慢性耗竭與高功能焦慮：當「完美主義」成為侵蝕身心的毒藥',
    '<h3 data-path-to-node="1">你也是外表堅強、內心卻早已不堪重負的「高功能焦慮者」嗎？</h3><p>在競爭激烈的現代職場中，我們經常讚賞那些工作表現卓越、按時交付任務且隨時保持冷靜的人。然而，在這層完美無瑕的外表下，許多人正承受著嚴重的「高功能焦慮」（High-Functioning Anxiety）。這類族群往往透過過度努力與完美主義來掩飾內心的不安與恐懼。</p><p>&nbsp;</p><h4 data-path-to-node="2">高功能焦慮的常見行為特徵：</h4><p data-path-to-node="2"><strong>1. 無法停止思考與工作：</strong>即使在休息時間，腦袋依然不斷盤旋著未完成的事項或未來的風險。<br><strong>2. 對「失敗」與「否定」有極度的恐懼：</strong>將個人價值完全建立在工作成就上，拒絕顯露任何脆弱。<br><strong>3. 身體發出慢性耗竭訊號：</strong>長期伴隨睡眠障礙、偏頭痛、肌肉緊繃或腸胃不適等官能症狀。</p><p data-path-to-node="2">&nbsp;</p><h4 data-path-to-node="3">從「自我要求」到「自我慈悲」的調適之道：</h4><p data-path-to-node="3">心理學家克莉絲汀·奈夫（Kristin Neff）提出的「自我慈悲」（Self-Compassion）是解開慢性耗竭的鑰匙。當你再次感到焦慮排山倒海而來時，嘗試運用以下三個步驟：</p><p data-path-to-node="3"><strong>• 正念覺察（Mindfulness）：</strong>接納當下的焦慮感受，承認「我現在確實感到很累與不安」，而不對自己進行批判。<br><strong>• 理解共通人性（Common Humanity）：</strong>提醒自己痛苦與不完美是人類經驗的一部分，你並不孤單，無須要求自己隨時無懈可擊。<br><strong>• 對自己展現善意（Self-Kindness）：</strong>用安慰好朋友的口吻對自己說話，允許自己適度休息與放下負擔。</p><h4 data-path-to-node="4">&nbsp;</h4><h4 data-path-to-node="4">學會放下接納不完美，才是真正強大的開始。</h4><p data-path-to-node="4">卸下完美的假面，給予自己喘息與修復的空間。如果你發現焦慮已嚴重影響生活，尋求專業心理諮商的協助將是你重獲內心平靜的重要一步。</p>',
    '2026-06-08 09:30:00', '2026-06-08 16:30:00', '2026-06-08 17:0:00', '2026-06-08 18:00:00', '2026-06-09 12:00:00', '2026-07-10 16:05:00',
    4, NULL, NULL, 185, 24
),
-- 17 草稿
(
    NULL, 1, 1, NULL, NULL, '討好型人格的心理陷阱：給「高敏感族群」的心理界線建立指南',
    '<h3 data-path-to-node="1">你是否總是習慣優先滿足別人的需求，卻忽視了自己的疲憊？</h3><p>在諮商室裡，我經常遇到許多擁有「高敏感特質」（Highly Sensitive Person, HSP）的來訪者。他們對他人的情緒變化極為敏銳，能夠輕易察覺他人的不悅或需求。然而，這種強大的共情能力，若缺乏明確的個人邊界（Personal Boundaries），往往會演變成「習慣性討好」（People-Pleasing）與情緒過載。</p><p>&nbsp;</p><h4 data-path-to-node="2">為什麼高敏感者特別容易掉入「討好陷阱」？</h4><p data-path-to-node="2"><strong>1. 恐懼衝突與被拒絕：</strong>將別人的負面情緒歸咎於自己，認為「只要我做得夠好，大家就會開心」。<br><strong>2. 過度承擔他人的情緒責任：</strong>無法區隔「對方的問題」與「自己的責任」，習慣將別人的困擾扛在自己身上。<br><strong>3. 忽視內心真正的聲音：</strong>長期將自我需求置於最後，導致心理能量長期處於虧空狀態。</p><p data-path-to-node="2">&nbsp;</p><h4 data-path-to-node="3">建立健全心理界線的三個練習步驟：</h4><p data-path-to-node="3"><strong>步驟一：練習「延遲回應」（Pause Before Responding）</strong><br>當他人提出請求時，不要立刻習慣性地回答「好」。給自己 10 秒鐘的緩衝期，或說：「我確認一下時間，待會回覆你。」這能為你爭取思考個人界線的時間。</p><p data-path-to-node="3"><strong>步驟二：區分「同理」與「承擔」的差別</strong><br>你可以對他人的遭遇表達理解與關心，但無須為對方的問題提供完美的解決方案，更不必為對方的情緒反應負責。</p><p data-path-to-node="3"><strong>步驟三：接納拒絕帶來的罪惡感</strong><br>拒絕他人時感到內疚是正常的心理反應，但請記住：「設立界線不是對別人的冷酷，而是對自己的尊重與保護。」</p><h4 data-path-to-node="4">&nbsp;</h4><h4 data-path-to-node="4">學會設立界線，你的善良才能擁有真正的力量。</h4><p data-path-to-node="4">先照顧好自己，才能擁有健康的關係。從今天開始，嘗試溫和但堅定地向不合理的要求說「不」吧！</p>',
    '2026-07-15 10:30:00', '2026-07-15 11:45:00', NULL, NULL, NULL, NULL,
    0, NULL, NULL, 0, 0
);

-- ==========================================
-- 文章瀏覽紀錄	(article_view_histories)
-- ==========================================
CREATE TABLE article_view_histories (
    article_id  INT NOT NULL,
    member_id   INT NOT NULL,
    viewed_at   DATETIME NOT NULL,
    PRIMARY KEY (article_id, member_id),
    CONSTRAINT fk_view_histories_articles
        FOREIGN KEY (article_id) REFERENCES articles(article_id),
    CONSTRAINT fk_view_histories_member
        FOREIGN KEY (member_id) REFERENCES member(member_id)
);

INSERT INTO article_view_histories (article_id, member_id, viewed_at) VALUES
-- Article 1
(1, 1, '2026-05-05 09:50:00'), (1, 2, '2026-05-05 09:58:00'), (1, 3, '2026-05-05 10:13:00'), (1, 4, '2026-05-05 10:28:00'), (1, 5, '2026-05-05 10:58:00'),
(1, 6, '2026-05-05 11:18:00'), (1, 7, '2026-05-05 11:58:00'), (1, 8, '2026-05-05 12:28:00'), (1, 9, '2026-05-05 12:58:00'), (1, 10, '2026-05-05 13:58:00'),
(1, 11, '2026-05-05 14:13:00'), (1, 12, '2026-05-05 14:58:00'), (1, 13, '2026-05-05 15:38:00'), (1, 14, '2026-05-05 15:58:00'), (1, 15, '2026-05-05 16:28:00'),
(1, 16, '2026-05-05 16:58:00'), (1, 17, '2026-05-05 17:18:00'), (1, 18, '2026-05-05 17:58:00'), (1, 19, '2026-05-05 18:28:00'), (1, 20, '2026-05-05 18:58:00'),
(1, 21, '2026-05-05 19:13:00'), (1, 22, '2026-05-05 19:43:00'), (1, 23, '2026-05-05 19:58:00'), (1, 24, '2026-05-05 20:28:00'), (1, 25, '2026-05-05 20:58:00'),
(1, 26, '2026-05-05 21:13:00'), (1, 27, '2026-05-05 21:28:00'), (1, 28, '2026-05-05 21:58:00'), (1, 29, '2026-05-05 22:13:00'), (1, 30, '2026-05-05 22:28:00'),
(1, 31, '2026-05-05 22:43:00'), (1, 32, '2026-05-05 22:58:00'), (1, 33, '2026-05-05 23:13:00'), (1, 34, '2026-05-05 23:28:00'), (1, 35, '2026-05-05 23:58:00'),
(1, 36, '2026-05-06 00:28:00'), (1, 37, '2026-05-06 00:58:00'), (1, 38, '2026-05-06 07:58:00'), (1, 39, '2026-05-06 08:58:00'), (1, 40, '2026-05-06 09:28:00'),
(1, 41, '2026-05-06 09:58:00'), (1, 42, '2026-05-06 10:28:00'), (1, 43, '2026-05-06 10:58:00'), (1, 44, '2026-05-06 11:28:00'), (1, 45, '2026-05-06 11:58:00'),
(1, 46, '2026-05-06 12:58:00'), (1, 47, '2026-05-06 13:58:00'), (1, 48, '2026-05-06 14:58:00'), (1, 49, '2026-05-06 15:58:00'), (1, 50, '2026-05-06 16:58:00'),

-- Article 2
(2, 1, '2026-05-19 08:58:00'), (2, 2, '2026-05-19 09:13:00'), (2, 3, '2026-05-19 09:28:00'), (2, 4, '2026-05-19 09:58:00'), (2, 5, '2026-05-19 10:28:00'),
(2, 6, '2026-05-19 10:58:00'), (2, 7, '2026-05-19 11:28:00'), (2, 8, '2026-05-19 11:58:00'), (2, 9, '2026-05-19 12:58:00'), (2, 10, '2026-05-19 13:28:00'),
(2, 11, '2026-05-19 13:58:00'), (2, 12, '2026-05-19 14:28:00'), (2, 13, '2026-05-19 14:58:00'), (2, 14, '2026-05-19 15:28:00'), (2, 15, '2026-05-19 15:58:00'),
(2, 16, '2026-05-19 16:28:00'), (2, 17, '2026-05-19 16:58:00'), (2, 18, '2026-05-19 17:28:00'), (2, 19, '2026-05-19 17:58:00'), (2, 20, '2026-05-19 18:28:00'),
(2, 21, '2026-05-19 18:58:00'), (2, 22, '2026-05-19 19:28:00'), (2, 23, '2026-05-19 19:58:00'), (2, 24, '2026-05-19 20:28:00'), (2, 25, '2026-05-19 20:58:00'),
(2, 26, '2026-05-19 21:28:00'), (2, 27, '2026-05-19 21:58:00'), (2, 28, '2026-05-19 22:28:00'), (2, 29, '2026-05-19 22:58:00'), (2, 30, '2026-05-19 23:58:00'),
(2, 31, '2026-05-20 07:58:00'), (2, 32, '2026-05-20 08:58:00'), (2, 33, '2026-05-20 09:58:00'), (2, 34, '2026-05-20 10:58:00'), (2, 35, '2026-05-20 11:58:00'),
(2, 36, '2026-05-20 12:58:00'), (2, 37, '2026-05-20 13:58:00'), (2, 38, '2026-05-20 14:58:00'), (2, 39, '2026-05-20 15:58:00'), (2, 40, '2026-05-20 16:58:00'),
(2, 41, '2026-05-20 17:58:00'), (2, 42, '2026-05-19 11:40:00'), (2, 43, '2026-05-19 12:07:00'), (2, 44, '2026-05-19 13:01:00'), (2, 45, '2026-05-19 14:47:00'),
(2, 46, '2026-05-19 15:17:00'),

-- Article 3
(3, 1, '2026-06-02 08:58:00'), (3, 2, '2026-06-02 09:28:00'), (3, 3, '2026-06-02 09:58:00'), (3, 4, '2026-06-02 10:28:00'), (3, 5, '2026-06-02 10:58:00'),
(3, 6, '2026-06-02 11:28:00'), (3, 7, '2026-06-02 11:58:00'), (3, 8, '2026-06-02 12:58:00'), (3, 9, '2026-06-02 13:58:00'), (3, 10, '2026-06-02 14:58:00'),
(3, 11, '2026-06-02 15:58:00'), (3, 12, '2026-06-02 16:58:00'), (3, 13, '2026-06-02 17:58:00'), (3, 14, '2026-06-02 18:58:00'), (3, 15, '2026-06-02 19:58:00'),
(3, 16, '2026-06-02 20:58:00'), (3, 17, '2026-06-02 21:58:00'), (3, 18, '2026-06-03 07:58:00'), (3, 19, '2026-06-03 08:58:00'), (3, 20, '2026-06-03 09:58:00'),
(3, 21, '2026-06-03 10:58:00'), (3, 22, '2026-06-03 11:58:00'), (3, 23, '2026-06-03 12:58:00'), (3, 24, '2026-06-03 13:58:00'), (3, 25, '2026-06-03 14:58:00'),
(3, 26, '2026-06-03 15:58:00'), (3, 27, '2026-06-03 16:58:00'), (3, 28, '2026-06-03 17:58:00'), (3, 29, '2026-06-03 18:58:00'), (3, 30, '2026-06-03 19:58:00'),
(3, 31, '2026-06-03 20:58:00'), (3, 32, '2026-06-03 21:58:00'), (3, 33, '2026-06-04 08:58:00'), (3, 34, '2026-06-04 09:58:00'), (3, 35, '2026-06-04 10:58:00'),
(3, 36, '2026-06-02 15:15:00'), (3, 37, '2026-06-02 16:14:00'), (3, 38, '2026-06-02 17:08:00'), (3, 39, '2026-06-02 18:47:00'), (3, 40, '2026-06-02 09:06:00'),

-- Article 4
(4, 1, '2026-06-16 08:58:00'), (4, 2, '2026-06-16 09:58:00'), (4, 3, '2026-06-16 10:58:00'), (4, 4, '2026-06-16 11:58:00'), (4, 5, '2026-06-16 12:58:00'),
(4, 6, '2026-06-16 13:58:00'), (4, 7, '2026-06-16 14:58:00'), (4, 8, '2026-06-16 15:58:00'), (4, 9, '2026-06-16 16:58:00'), (4, 10, '2026-06-16 17:58:00'),
(4, 11, '2026-06-16 18:58:00'), (4, 12, '2026-06-16 19:58:00'), (4, 13, '2026-06-16 20:58:00'), (4, 14, '2026-06-17 08:58:00'), (4, 15, '2026-06-17 09:58:00'),
(4, 16, '2026-06-17 10:58:00'), (4, 17, '2026-06-17 11:58:00'), (4, 18, '2026-06-17 12:58:00'), (4, 19, '2026-06-17 13:58:00'), (4, 20, '2026-06-17 14:58:00'),
(4, 21, '2026-06-17 15:58:00'), (4, 22, '2026-06-17 16:58:00'), (4, 23, '2026-06-17 17:58:00'), (4, 24, '2026-06-17 18:58:00'), (4, 25, '2026-06-17 19:58:00'),
(4, 26, '2026-06-17 20:58:00'), (4, 27, '2026-06-18 08:58:00'), (4, 28, '2026-06-18 09:58:00'), (4, 29, '2026-06-16 18:43:00'), (4, 30, '2026-06-16 09:47:00'),
(4, 31, '2026-06-16 10:57:00'), (4, 32, '2026-06-16 11:34:00'), (4, 33, '2026-06-16 12:05:00'),

-- Article 5
(5, 1, '2026-06-30 08:58:00'), (5, 2, '2026-06-30 09:58:00'), (5, 3, '2026-06-30 10:58:00'), (5, 4, '2026-06-30 11:58:00'), (5, 5, '2026-06-30 12:58:00'),
(5, 6, '2026-06-30 13:58:00'), (5, 7, '2026-06-30 14:58:00'), (5, 8, '2026-06-30 15:58:00'), (5, 9, '2026-06-30 16:58:00'), (5, 10, '2026-06-30 17:58:00'),
(5, 11, '2026-06-30 18:58:00'), (5, 12, '2026-06-30 19:58:00'), (5, 13, '2026-07-01 08:58:00'), (5, 14, '2026-07-01 09:58:00'), (5, 15, '2026-07-01 10:58:00'),
(5, 16, '2026-07-01 11:58:00'), (5, 17, '2026-07-01 12:58:00'), (5, 18, '2026-07-01 13:58:00'), (5, 19, '2026-07-01 14:58:00'), (5, 20, '2026-07-01 15:58:00'),
(5, 21, '2026-07-01 16:58:00'), (5, 22, '2026-07-01 17:58:00'), (5, 23, '2026-06-30 12:37:00'), (5, 24, '2026-06-30 13:27:00'), (5, 25, '2026-06-30 14:02:00'),
(5, 26, '2026-06-30 15:01:00'), (5, 27, '2026-06-30 16:05:00'),

-- Article 6
(6, 1, '2026-07-03 08:58:00'), (6, 2, '2026-07-03 09:28:00'), (6, 3, '2026-07-03 09:58:00'), (6, 4, '2026-07-03 10:28:00'), (6, 5, '2026-07-03 10:58:00'),
(6, 6, '2026-07-03 11:28:00'), (6, 7, '2026-07-03 11:58:00'), (6, 8, '2026-07-03 12:58:00'), (6, 9, '2026-07-03 13:58:00'), (6, 10, '2026-07-03 14:58:00'),
(6, 11, '2026-07-03 15:58:00'), (6, 12, '2026-07-03 16:58:00'), (6, 13, '2026-07-03 17:58:00'), (6, 14, '2026-07-03 18:58:00'), (6, 15, '2026-07-03 19:58:00'),
(6, 16, '2026-07-03 20:58:00'), (6, 17, '2026-07-03 21:58:00'),

-- Article 7
(7, 1, '2026-07-07 08:58:00'), (7, 2, '2026-07-07 09:28:00'), (7, 3, '2026-07-07 09:58:00'), (7, 4, '2026-07-07 10:28:00'), (7, 5, '2026-07-07 10:58:00'),
(7, 6, '2026-07-07 11:28:00'), (7, 7, '2026-07-07 11:58:00'), (7, 8, '2026-07-07 12:58:00'), (7, 9, '2026-07-07 13:58:00'), (7, 10, '2026-07-07 14:58:00'),
(7, 11, '2026-07-07 15:58:00'), (7, 12, '2026-07-07 16:58:00'), (7, 13, '2026-07-07 17:58:00'), (7, 14, '2026-07-07 18:58:00'), (7, 15, '2026-07-07 19:58:00'),

-- Article 8
(8, 1, '2026-07-09 08:58:00'), (8, 2, '2026-07-09 09:28:00'), (8, 3, '2026-07-09 09:58:00'), (8, 4, '2026-07-09 10:28:00'), (8, 5, '2026-07-09 10:58:00'),
(8, 6, '2026-07-09 11:28:00'), (8, 7, '2026-07-09 11:58:00'), (8, 8, '2026-07-09 12:58:00'), (8, 9, '2026-07-09 13:58:00'), (8, 10, '2026-07-09 14:58:00'),
(8, 11, '2026-07-09 15:58:00'), (8, 12, '2026-07-09 16:58:00'), (8, 13, '2026-07-09 17:58:00'), (8, 14, '2026-07-09 18:58:00'), (8, 15, '2026-07-09 19:58:00'),
(8, 16, '2026-07-09 20:58:00'),

-- Article 9
(9, 1, '2026-07-10 08:58:00'), (9, 2, '2026-07-10 09:28:00'), (9, 3, '2026-07-10 09:58:00'), (9, 4, '2026-07-10 10:28:00'), (9, 5, '2026-07-10 10:58:00'),
(9, 6, '2026-07-10 11:28:00'), (9, 7, '2026-07-10 11:58:00'), (9, 8, '2026-07-10 12:58:00'), (9, 9, '2026-07-10 13:58:00'), (9, 10, '2026-07-10 14:58:00'),
(9, 11, '2026-07-10 15:58:00'), (9, 12, '2026-07-10 16:58:00'), (9, 13, '2026-07-10 17:58:00'), (9, 14, '2026-07-10 18:58:00'),

-- Article 10
(10, 1, '2026-07-14 08:58:00'), (10, 2, '2026-07-14 09:28:00'), (10, 3, '2026-07-14 09:58:00'), (10, 4, '2026-07-14 10:28:00'), (10, 5, '2026-07-14 10:58:00'),
(10, 6, '2026-07-14 11:28:00'), (10, 7, '2026-07-14 11:58:00'), (10, 8, '2026-07-14 12:58:00'), (10, 9, '2026-07-14 13:58:00'), (10, 10, '2026-07-14 14:58:00'),
(10, 11, '2026-07-14 15:58:00'), (10, 12, '2026-07-14 16:58:00'), (10, 13, '2026-07-14 17:58:00'), (10, 14, '2026-07-14 18:58:00'), (10, 15, '2026-07-14 19:58:00'),
(10, 16, '2026-07-14 20:58:00'),

-- Article 11
(11, 1, '2026-07-15 08:58:00'), (11, 2, '2026-07-15 09:28:00'), (11, 3, '2026-07-15 09:58:00'), (11, 4, '2026-07-15 10:28:00'), (11, 5, '2026-07-15 10:58:00'),
(11, 6, '2026-07-15 11:28:00'), (11, 7, '2026-07-15 11:58:00'), (11, 8, '2026-07-15 12:58:00'), (11, 9, '2026-07-15 13:58:00'), (11, 10, '2026-07-15 14:58:00'),
(11, 11, '2026-07-15 15:58:00'), (11, 12, '2026-07-15 16:58:00'), (11, 13, '2026-07-15 17:58:00'),

-- Article 12
(12, 1, '2026-07-17 08:58:00'), (12, 2, '2026-07-17 09:28:00'), (12, 3, '2026-07-17 09:58:00'), (12, 4, '2026-07-17 10:28:00'), (12, 5, '2026-07-17 10:58:00'),
(12, 6, '2026-07-17 11:28:00'), (12, 7, '2026-07-17 11:58:00'), (12, 8, '2026-07-17 12:58:00'), (12, 9, '2026-07-17 13:58:00'), (12, 10, '2026-07-17 14:58:00'),
(12, 11, '2026-07-17 15:58:00'), (12, 12, '2026-07-17 16:58:00'), (12, 13, '2026-07-17 17:58:00'), (12, 14, '2026-07-17 18:58:00'), (12, 15, '2026-07-17 19:58:00'),

-- Article 13
(13, 1, '2026-07-18 08:58:00'), (13, 2, '2026-07-18 09:28:00'), (13, 3, '2026-07-18 09:58:00'), (13, 4, '2026-07-18 10:28:00'), (13, 5, '2026-07-18 10:58:00'),
(13, 6, '2026-07-18 11:28:00'), (13, 7, '2026-07-18 11:58:00'), (13, 8, '2026-07-18 12:58:00'), (13, 9, '2026-07-18 13:58:00'), (13, 10, '2026-07-18 14:58:00'),
(13, 11, '2026-07-18 15:58:00'), (13, 12, '2026-07-18 16:58:00'),

-- Article 14
(14, 1, '2026-07-20 08:58:00'), (14, 2, '2026-07-20 09:28:00'), (14, 3, '2026-07-20 09:58:00'), (14, 4, '2026-07-20 10:28:00'), (14, 5, '2026-07-20 10:58:00'),
(14, 6, '2026-07-20 11:28:00'), (14, 7, '2026-07-20 11:58:00'), (14, 8, '2026-07-20 12:58:00'), (14, 9, '2026-07-20 13:58:00'), (14, 10, '2026-07-20 14:58:00'),
(14, 11, '2026-07-20 15:58:00'), (14, 12, '2026-07-20 16:58:00'), (14, 13, '2026-07-20 17:58:00'), (14, 14, '2026-07-20 18:58:00'), (14, 15, '2026-07-20 19:58:00'),

-- Article 15
(15, 1, '2026-07-20 09:58:00'), (15, 2, '2026-07-20 10:28:00'), (15, 3, '2026-07-20 10:58:00'), (15, 4, '2026-07-20 11:28:00'), (15, 5, '2026-07-20 11:58:00'),
(15, 6, '2026-07-20 12:28:00'), (15, 7, '2026-07-20 12:58:00'), (15, 8, '2026-07-20 13:28:00'), (15, 9, '2026-07-20 13:58:00'), (15, 10, '2026-07-20 14:28:00'),
(15, 11, '2026-07-20 14:58:00'), (15, 12, '2026-07-20 15:28:00'), (15, 13, '2026-07-20 15:58:00');

-- ==========================================
-- 按讚文章(article_likes)
-- ==========================================
CREATE TABLE article_likes(
	article_id INT NOT NULL,
    member_id INT NOT NULL,
    liked_at DATETIME NOT NULL,
    PRIMARY KEY (article_id, member_id),
    CONSTRAINT fk_article_likes_articles
		FOREIGN KEY (article_id) REFERENCES articles(article_id),
    CONSTRAINT fk_article_likes_member
		FOREIGN KEY (member_id) REFERENCES member(member_id)
);

INSERT INTO article_likes (article_id, member_id, liked_at) VALUES 
-- Article 1: 49 Likes (Member 2~50)
(1, 2, '2026-05-05 10:00:00'), (1, 3, '2026-05-05 10:15:00'), (1, 4, '2026-05-05 10:30:00'), (1, 5, '2026-05-05 11:00:00'), (1, 6, '2026-05-05 11:20:00'),
(1, 7, '2026-05-05 12:00:00'), (1, 8, '2026-05-05 12:30:00'), (1, 9, '2026-05-05 13:00:00'), (1, 10, '2026-05-05 14:00:00'), (1, 11, '2026-05-05 14:15:00'),
(1, 12, '2026-05-05 15:00:00'), (1, 13, '2026-05-05 15:40:00'), (1, 14, '2026-05-05 16:00:00'), (1, 15, '2026-05-05 16:30:00'), (1, 16, '2026-05-05 17:00:00'),
(1, 17, '2026-05-05 17:20:00'), (1, 18, '2026-05-05 18:00:00'), (1, 19, '2026-05-05 18:30:00'), (1, 20, '2026-05-05 19:00:00'), (1, 21, '2026-05-05 19:15:00'),
(1, 22, '2026-05-05 19:45:00'), (1, 23, '2026-05-05 20:00:00'), (1, 24, '2026-05-05 20:30:00'), (1, 25, '2026-05-05 21:00:00'), (1, 26, '2026-05-05 21:15:00'),
(1, 27, '2026-05-05 21:30:00'), (1, 28, '2026-05-05 22:00:00'), (1, 29, '2026-05-05 22:15:00'), (1, 30, '2026-05-05 22:30:00'), (1, 31, '2026-05-05 22:45:00'),
(1, 32, '2026-05-05 23:00:00'), (1, 33, '2026-05-05 23:15:00'), (1, 34, '2026-05-05 23:30:00'), (1, 35, '2026-05-06 00:00:00'), (1, 36, '2026-05-06 00:30:00'),
(1, 37, '2026-05-06 01:00:00'), (1, 38, '2026-05-06 08:00:00'), (1, 39, '2026-05-06 09:00:00'), (1, 40, '2026-05-06 09:30:00'), (1, 41, '2026-05-06 10:00:00'),
(1, 42, '2026-05-06 10:30:00'), (1, 43, '2026-05-06 11:00:00'), (1, 44, '2026-05-06 11:30:00'), (1, 45, '2026-05-06 12:00:00'), (1, 46, '2026-05-06 13:00:00'),
(1, 47, '2026-05-06 14:00:00'), (1, 48, '2026-05-06 15:00:00'), (1, 49, '2026-05-06 16:00:00'), (1, 50, '2026-05-06 17:00:00'),

-- Article 2: 41 Likes (Member 1 ~ 41)
(2, 1, '2026-05-19 09:00:00'), (2, 2, '2026-05-19 09:15:00'), (2, 3, '2026-05-19 09:30:00'), (2, 4, '2026-05-19 10:00:00'), (2, 5, '2026-05-19 10:30:00'),
(2, 6, '2026-05-19 11:00:00'), (2, 7, '2026-05-19 11:30:00'), (2, 8, '2026-05-19 12:00:00'), (2, 9, '2026-05-19 13:00:00'), (2, 10, '2026-05-19 13:30:00'),
(2, 11, '2026-05-19 14:00:00'), (2, 12, '2026-05-19 14:30:00'), (2, 13, '2026-05-19 15:00:00'), (2, 14, '2026-05-19 15:30:00'), (2, 15, '2026-05-19 16:00:00'),
(2, 16, '2026-05-19 16:30:00'), (2, 17, '2026-05-19 17:00:00'), (2, 18, '2026-05-19 17:30:00'), (2, 19, '2026-05-19 18:00:00'), (2, 20, '2026-05-19 18:30:00'),
(2, 21, '2026-05-19 19:00:00'), (2, 22, '2026-05-19 19:30:00'), (2, 23, '2026-05-19 20:00:00'), (2, 24, '2026-05-19 20:30:00'), (2, 25, '2026-05-19 21:00:00'),
(2, 26, '2026-05-19 21:30:00'), (2, 27, '2026-05-19 22:00:00'), (2, 28, '2026-05-19 22:30:00'), (2, 29, '2026-05-19 23:00:00'), (2, 30, '2026-05-20 00:00:00'),
(2, 31, '2026-05-20 08:00:00'), (2, 32, '2026-05-20 09:00:00'), (2, 33, '2026-05-20 10:00:00'), (2, 34, '2026-05-20 11:00:00'), (2, 35, '2026-05-20 12:00:00'),
(2, 36, '2026-05-20 13:00:00'), (2, 37, '2026-05-20 14:00:00'), (2, 38, '2026-05-20 15:00:00'), (2, 39, '2026-05-20 16:00:00'), (2, 40, '2026-05-20 17:00:00'),
(2, 41, '2026-05-20 18:00:00'),

-- Article 3: 35 Likes (Member 1 ~ 35)
(3, 1, '2026-06-02 09:00:00'), (3, 2, '2026-06-02 09:30:00'), (3, 3, '2026-06-02 10:00:00'), (3, 4, '2026-06-02 10:30:00'), (3, 5, '2026-06-02 11:00:00'),
(3, 6, '2026-06-02 11:30:00'), (3, 7, '2026-06-02 12:00:00'), (3, 8, '2026-06-02 13:00:00'), (3, 9, '2026-06-02 14:00:00'), (3, 10, '2026-06-02 15:00:00'),
(3, 11, '2026-06-02 16:00:00'), (3, 12, '2026-06-02 17:00:00'), (3, 13, '2026-06-02 18:00:00'), (3, 14, '2026-06-02 19:00:00'), (3, 15, '2026-06-02 20:00:00'),
(3, 16, '2026-06-02 21:00:00'), (3, 17, '2026-06-02 22:00:00'), (3, 18, '2026-06-03 08:00:00'), (3, 19, '2026-06-03 09:00:00'), (3, 20, '2026-06-03 10:00:00'),
(3, 21, '2026-06-03 11:00:00'), (3, 22, '2026-06-03 12:00:00'), (3, 23, '2026-06-03 13:00:00'), (3, 24, '2026-06-03 14:00:00'), (3, 25, '2026-06-03 15:00:00'),
(3, 26, '2026-06-03 16:00:00'), (3, 27, '2026-06-03 17:00:00'), (3, 28, '2026-06-03 18:00:00'), (3, 29, '2026-06-03 19:00:00'), (3, 30, '2026-06-03 20:00:00'),
(3, 31, '2026-06-03 21:00:00'), (3, 32, '2026-06-03 22:00:00'), (3, 33, '2026-06-04 09:00:00'), (3, 34, '2026-06-04 10:00:00'), (3, 35, '2026-06-04 11:00:00'),

-- Article 4: 28 Likes (Member 1 ~ 28)
(4, 1, '2026-06-16 09:00:00'), (4, 2, '2026-06-16 10:00:00'), (4, 3, '2026-06-16 11:00:00'), (4, 4, '2026-06-16 12:00:00'), (4, 5, '2026-06-16 13:00:00'),
(4, 6, '2026-06-16 14:00:00'), (4, 7, '2026-06-16 15:00:00'), (4, 8, '2026-06-16 16:00:00'), (4, 9, '2026-06-16 17:00:00'), (4, 10, '2026-06-16 18:00:00'),
(4, 11, '2026-06-16 19:00:00'), (4, 12, '2026-06-16 20:00:00'), (4, 13, '2026-06-16 21:00:00'), (4, 14, '2026-06-17 09:00:00'), (4, 15, '2026-06-17 10:00:00'),
(4, 16, '2026-06-17 11:00:00'), (4, 17, '2026-06-17 12:00:00'), (4, 18, '2026-06-17 13:00:00'), (4, 19, '2026-06-17 14:00:00'), (4, 20, '2026-06-17 15:00:00'),
(4, 21, '2026-06-17 16:00:00'), (4, 22, '2026-06-17 17:00:00'), (4, 23, '2026-06-17 18:00:00'), (4, 24, '2026-06-17 19:00:00'), (4, 25, '2026-06-17 20:00:00'),
(4, 26, '2026-06-17 21:00:00'), (4, 27, '2026-06-18 09:00:00'), (4, 28, '2026-06-18 10:00:00'),

-- Article 5: 22 Likes (Member 1 ~ 22)
(5, 1, '2026-06-30 09:00:00'), (5, 2, '2026-06-30 10:00:00'), (5, 3, '2026-06-30 11:00:00'), (5, 4, '2026-06-30 12:00:00'), (5, 5, '2026-06-30 13:00:00'),
(5, 6, '2026-06-30 14:00:00'), (5, 7, '2026-06-30 15:00:00'), (5, 8, '2026-06-30 16:00:00'), (5, 9, '2026-06-30 17:00:00'), (5, 10, '2026-06-30 18:00:00'),
(5, 11, '2026-06-30 19:00:00'), (5, 12, '2026-06-30 20:00:00'), (5, 13, '2026-07-01 09:00:00'), (5, 14, '2026-07-01 10:00:00'), (5, 15, '2026-07-01 11:00:00'),
(5, 16, '2026-07-01 12:00:00'), (5, 17, '2026-07-01 13:00:00'), (5, 18, '2026-07-01 14:00:00'), (5, 19, '2026-07-01 15:00:00'), (5, 20, '2026-07-01 16:00:00'),
(5, 21, '2026-07-01 17:00:00'), (5, 22, '2026-07-01 18:00:00'),

-- Article 6 ~ 15: 雙位數 Likes (皆包含 Member 1)
-- Article 6: 17 Likes (Member 1 ~ 17)
(6, 1, '2026-07-03 09:00:00'), (6, 2, '2026-07-03 09:30:00'), (6, 3, '2026-07-03 10:00:00'), (6, 4, '2026-07-03 10:30:00'), (6, 5, '2026-07-03 11:00:00'),
(6, 6, '2026-07-03 11:30:00'), (6, 7, '2026-07-03 12:00:00'), (6, 8, '2026-07-03 13:00:00'), (6, 9, '2026-07-03 14:00:00'), (6, 10, '2026-07-03 15:00:00'),
(6, 11, '2026-07-03 16:00:00'), (6, 12, '2026-07-03 17:00:00'), (6, 13, '2026-07-03 18:00:00'), (6, 14, '2026-07-03 19:00:00'), (6, 15, '2026-07-03 20:00:00'),
(6, 16, '2026-07-03 21:00:00'), (6, 17, '2026-07-03 22:00:00'),

-- Article 7: 15 Likes (Member 1 ~ 15)
(7, 1, '2026-07-07 09:00:00'), (7, 2, '2026-07-07 09:30:00'), (7, 3, '2026-07-07 10:00:00'), (7, 4, '2026-07-07 10:30:00'), (7, 5, '2026-07-07 11:00:00'),
(7, 6, '2026-07-07 11:30:00'), (7, 7, '2026-07-07 12:00:00'), (7, 8, '2026-07-07 13:00:00'), (7, 9, '2026-07-07 14:00:00'), (7, 10, '2026-07-07 15:00:00'),
(7, 11, '2026-07-07 16:00:00'), (7, 12, '2026-07-07 17:00:00'), (7, 13, '2026-07-07 18:00:00'), (7, 14, '2026-07-07 19:00:00'), (7, 15, '2026-07-07 20:00:00'),

-- Article 8: 16 Likes (Member 1 ~ 16)
(8, 1, '2026-07-09 09:00:00'), (8, 2, '2026-07-09 09:30:00'), (8, 3, '2026-07-09 10:00:00'), (8, 4, '2026-07-09 10:30:00'), (8, 5, '2026-07-09 11:00:00'),
(8, 6, '2026-07-09 11:30:00'), (8, 7, '2026-07-09 12:00:00'), (8, 8, '2026-07-09 13:00:00'), (8, 9, '2026-07-09 14:00:00'), (8, 10, '2026-07-09 15:00:00'),
(8, 11, '2026-07-09 16:00:00'), (8, 12, '2026-07-09 17:00:00'), (8, 13, '2026-07-09 18:00:00'), (8, 14, '2026-07-09 19:00:00'), (8, 15, '2026-07-09 20:00:00'),
(8, 16, '2026-07-09 21:00:00'),

-- Article 9: 14 Likes (Member 1 ~ 14)
(9, 1, '2026-07-10 09:00:00'), (9, 2, '2026-07-10 09:30:00'), (9, 3, '2026-07-10 10:00:00'), (9, 4, '2026-07-10 10:30:00'), (9, 5, '2026-07-10 11:00:00'),
(9, 6, '2026-07-10 11:30:00'), (9, 7, '2026-07-10 12:00:00'), (9, 8, '2026-07-10 13:00:00'), (9, 9, '2026-07-10 14:00:00'), (9, 10, '2026-07-10 15:00:00'),
(9, 11, '2026-07-10 16:00:00'), (9, 12, '2026-07-10 17:00:00'), (9, 13, '2026-07-10 18:00:00'), (9, 14, '2026-07-10 19:00:00'),

-- Article 10: 16 Likes (Member 1 ~ 16)
(10, 1, '2026-07-14 09:00:00'), (10, 2, '2026-07-14 09:30:00'), (10, 3, '2026-07-14 10:00:00'), (10, 4, '2026-07-14 10:30:00'), (10, 5, '2026-07-14 11:00:00'),
(10, 6, '2026-07-14 11:30:00'), (10, 7, '2026-07-14 12:00:00'), (10, 8, '2026-07-14 13:00:00'), (10, 9, '2026-07-14 14:00:00'), (10, 10, '2026-07-14 15:00:00'),
(10, 11, '2026-07-14 16:00:00'), (10, 12, '2026-07-14 17:00:00'), (10, 13, '2026-07-14 18:00:00'), (10, 14, '2026-07-14 19:00:00'), (10, 15, '2026-07-14 20:00:00'),
(10, 16, '2026-07-14 21:00:00'),

-- Article 11: 13 Likes (Member 1 ~ 13)
(11, 1, '2026-07-15 09:00:00'), (11, 2, '2026-07-15 09:30:00'), (11, 3, '2026-07-15 10:00:00'), (11, 4, '2026-07-15 10:30:00'), (11, 5, '2026-07-15 11:00:00'),
(11, 6, '2026-07-15 11:30:00'), (11, 7, '2026-07-15 12:00:00'), (11, 8, '2026-07-15 13:00:00'), (11, 9, '2026-07-15 14:00:00'), (11, 10, '2026-07-15 15:00:00'),
(11, 11, '2026-07-15 16:00:00'), (11, 12, '2026-07-15 17:00:00'), (11, 13, '2026-07-15 18:00:00'),

-- Article 12: 15 Likes (Member 1 ~ 15)
(12, 1, '2026-07-17 09:00:00'), (12, 2, '2026-07-17 09:30:00'), (12, 3, '2026-07-17 10:00:00'), (12, 4, '2026-07-17 10:30:00'), (12, 5, '2026-07-17 11:00:00'),
(12, 6, '2026-07-17 11:30:00'), (12, 7, '2026-07-17 12:00:00'), (12, 8, '2026-07-17 13:00:00'), (12, 9, '2026-07-17 14:00:00'), (12, 10, '2026-07-17 15:00:00'),
(12, 11, '2026-07-17 16:00:00'), (12, 12, '2026-07-17 17:00:00'), (12, 13, '2026-07-17 18:00:00'), (12, 14, '2026-07-17 19:00:00'), (12, 15, '2026-07-17 20:00:00'),

-- Article 13: 12 Likes (Member 1 ~ 12)
(13, 1, '2026-07-18 09:00:00'), (13, 2, '2026-07-18 09:30:00'), (13, 3, '2026-07-18 10:00:00'), (13, 4, '2026-07-18 10:30:00'), (13, 5, '2026-07-18 11:00:00'),
(13, 6, '2026-07-18 11:30:00'), (13, 7, '2026-07-18 12:00:00'), (13, 8, '2026-07-18 13:00:00'), (13, 9, '2026-07-18 14:00:00'), (13, 10, '2026-07-18 15:00:00'),
(13, 11, '2026-07-18 16:00:00'), (13, 12, '2026-07-18 17:00:00'),

-- Article 14: 15 Likes (Member 1 ~ 15)
(14, 1, '2026-07-20 09:00:00'), (14, 2, '2026-07-20 09:30:00'), (14, 3, '2026-07-20 10:00:00'), (14, 4, '2026-07-20 10:30:00'), (14, 5, '2026-07-20 11:00:00'),
(14, 6, '2026-07-20 11:30:00'), (14, 7, '2026-07-20 12:00:00'), (14, 8, '2026-07-20 13:00:00'), (14, 9, '2026-07-20 14:00:00'), (14, 10, '2026-07-20 15:00:00'),
(14, 11, '2026-07-20 16:00:00'), (14, 12, '2026-07-20 17:00:00'), (14, 13, '2026-07-20 18:00:00'), (14, 14, '2026-07-20 19:00:00'), (14, 15, '2026-07-20 20:00:00'),

-- Article 15: 13 Likes (Member 1 ~ 13)
(15, 1, '2026-07-20 10:00:00'), (15, 2, '2026-07-20 10:30:00'), (15, 3, '2026-07-20 11:00:00'), (15, 4, '2026-07-20 11:30:00'), (15, 5, '2026-07-20 12:00:00'),
(15, 6, '2026-07-20 12:30:00'), (15, 7, '2026-07-20 13:00:00'), (15, 8, '2026-07-20 13:30:00'), (15, 9, '2026-07-20 14:00:00'), (15, 10, '2026-07-20 14:30:00'),
(15, 11, '2026-07-20 15:00:00'), (15, 12, '2026-07-20 15:30:00'), (15, 13, '2026-07-20 16:00:00');


-- ==========================================
-- 收藏文章(article_bookmarks)
-- ==========================================
CREATE TABLE article_bookmarks(
	article_id INT NOT NULL,
    member_id INT NOT NULL,
    saved_at DATETIME NOT NULL,
    PRIMARY KEY (article_id, member_id),
    CONSTRAINT fk_article_bookmarks_articles
		FOREIGN KEY (article_id) REFERENCES articles(article_id),
    CONSTRAINT fk_article_bookmarks_member
		FOREIGN KEY (member_id) REFERENCES member(member_id)
);

INSERT INTO article_bookmarks (article_id, member_id, saved_at) VALUES 
-- Article 1: 45 Bookmarks (Member 2~46)
(1, 2, '2026-05-05 10:05:00'), (1, 3, '2026-05-05 10:20:00'), (1, 4, '2026-05-05 10:35:00'), (1, 5, '2026-05-05 11:05:00'), (1, 6, '2026-05-05 11:25:00'),
(1, 7, '2026-05-05 12:05:00'), (1, 8, '2026-05-05 12:35:00'), (1, 9, '2026-05-05 13:05:00'), (1, 10, '2026-05-05 14:05:00'), (1, 11, '2026-05-05 14:20:00'),
(1, 12, '2026-05-05 15:05:00'), (1, 13, '2026-05-05 15:45:00'), (1, 14, '2026-05-05 16:05:00'), (1, 15, '2026-05-05 16:35:00'), (1, 16, '2026-05-05 17:05:00'),
(1, 17, '2026-05-05 17:25:00'), (1, 18, '2026-05-05 18:05:00'), (1, 19, '2026-05-05 18:35:00'), (1, 20, '2026-05-05 19:05:00'), (1, 21, '2026-05-05 19:20:00'),
(1, 22, '2026-05-05 19:50:00'), (1, 23, '2026-05-05 20:05:00'), (1, 24, '2026-05-05 20:35:00'), (1, 25, '2026-05-05 21:05:00'), (1, 26, '2026-05-05 21:20:00'),
(1, 27, '2026-05-05 21:35:00'), (1, 28, '2026-05-05 22:05:00'), (1, 29, '2026-05-05 22:20:00'), (1, 30, '2026-05-05 22:35:00'), (1, 31, '2026-05-05 22:50:00'),
(1, 32, '2026-05-05 23:05:00'), (1, 33, '2026-05-05 23:20:00'), (1, 34, '2026-05-05 23:35:00'), (1, 35, '2026-05-06 00:05:00'), (1, 36, '2026-05-06 00:35:00'),
(1, 37, '2026-05-06 01:05:00'), (1, 38, '2026-05-06 08:05:00'), (1, 39, '2026-05-06 09:05:00'), (1, 40, '2026-05-06 09:35:00'), (1, 41, '2026-05-06 10:05:00'),
(1, 42, '2026-05-06 10:35:00'), (1, 43, '2026-05-06 11:05:00'), (1, 44, '2026-05-06 11:35:00'), (1, 45, '2026-05-06 12:05:00'), (1, 46, '2026-05-06 13:05:00'),

-- Article 2: 30 Bookmarks (Member 1 ~ 30)
(2, 1, '2026-05-19 09:05:00'), (2, 2, '2026-05-19 09:20:00'), (2, 3, '2026-05-19 09:35:00'), (2, 4, '2026-05-19 10:05:00'), (2, 5, '2026-05-19 10:35:00'),
(2, 6, '2026-05-19 11:05:00'), (2, 7, '2026-05-19 11:35:00'), (2, 8, '2026-05-19 12:05:00'), (2, 9, '2026-05-19 13:05:00'), (2, 10, '2026-05-19 13:35:00'),
(2, 11, '2026-05-19 14:05:00'), (2, 12, '2026-05-19 14:35:00'), (2, 13, '2026-05-19 15:05:00'), (2, 14, '2026-05-19 15:35:00'), (2, 15, '2026-05-19 16:05:00'),
(2, 16, '2026-05-19 16:35:00'), (2, 17, '2026-05-19 17:05:00'), (2, 18, '2026-05-19 17:35:00'), (2, 19, '2026-05-19 18:05:00'), (2, 20, '2026-05-19 18:35:00'),
(2, 21, '2026-05-19 19:05:00'), (2, 22, '2026-05-19 19:35:00'), (2, 23, '2026-05-19 20:05:00'), (2, 24, '2026-05-19 20:35:00'), (2, 25, '2026-05-19 21:05:00'),
(2, 26, '2026-05-19 21:35:00'), (2, 27, '2026-05-19 22:05:00'), (2, 28, '2026-05-19 22:35:00'), (2, 29, '2026-05-19 23:05:00'), (2, 30, '2026-05-20 00:05:00'),

-- Article 3: 28 Bookmarks (Member 1 ~ 28)
(3, 1, '2026-06-02 09:05:00'), (3, 2, '2026-06-02 09:35:00'), (3, 3, '2026-06-02 10:05:00'), (3, 4, '2026-06-02 10:35:00'), (3, 5, '2026-06-02 11:05:00'),
(3, 6, '2026-06-02 11:35:00'), (3, 7, '2026-06-02 12:05:00'), (3, 8, '2026-06-02 13:05:00'), (3, 9, '2026-06-02 14:05:00'), (3, 10, '2026-06-02 15:05:00'),
(3, 11, '2026-06-02 16:05:00'), (3, 12, '2026-06-02 17:05:00'), (3, 13, '2026-06-02 18:05:00'), (3, 14, '2026-06-02 19:05:00'), (3, 15, '2026-06-02 20:05:00'),
(3, 16, '2026-06-02 21:05:00'), (3, 17, '2026-06-02 22:05:00'), (3, 18, '2026-06-03 08:05:00'), (3, 19, '2026-06-03 09:05:00'), (3, 20, '2026-06-03 10:05:00'),
(3, 21, '2026-06-03 11:05:00'), (3, 22, '2026-06-03 12:05:00'), (3, 23, '2026-06-03 13:05:00'), (3, 24, '2026-06-03 14:05:00'), (3, 25, '2026-06-03 15:05:00'),
(3, 26, '2026-06-03 16:05:00'), (3, 27, '2026-06-03 17:05:00'), (3, 28, '2026-06-03 18:05:00'),

-- Article 4: 22 Bookmarks (Member 1 ~ 22)
(4, 1, '2026-06-16 09:05:00'), (4, 2, '2026-06-16 10:05:00'), (4, 3, '2026-06-16 11:05:00'), (4, 4, '2026-06-16 12:05:00'), (4, 5, '2026-06-16 13:05:00'),
(4, 6, '2026-06-16 14:05:00'), (4, 7, '2026-06-16 15:05:00'), (4, 8, '2026-06-16 16:05:00'), (4, 9, '2026-06-16 17:05:00'), (4, 10, '2026-06-16 18:05:00'),
(4, 11, '2026-06-16 19:05:00'), (4, 12, '2026-06-16 20:05:00'), (4, 13, '2026-06-16 21:05:00'), (4, 14, '2026-06-17 09:05:00'), (4, 15, '2026-06-17 10:05:00'),
(4, 16, '2026-06-17 11:05:00'), (4, 17, '2026-06-17 12:05:00'), (4, 18, '2026-06-17 13:05:00'), (4, 19, '2026-06-17 14:05:00'), (4, 20, '2026-06-17 15:05:00'),
(4, 21, '2026-06-17 16:05:00'), (4, 22, '2026-06-17 17:05:00'),

-- Article 5: 18 Bookmarks (Member 1 ~ 18)
(5, 1, '2026-06-30 09:05:00'), (5, 2, '2026-06-30 10:05:00'), (5, 3, '2026-06-30 11:05:00'), (5, 4, '2026-06-30 12:05:00'), (5, 5, '2026-06-30 13:05:00'),
(5, 6, '2026-06-30 14:05:00'), (5, 7, '2026-06-30 15:05:00'), (5, 8, '2026-06-30 16:05:00'), (5, 9, '2026-06-30 17:05:00'), (5, 10, '2026-06-30 18:05:00'),
(5, 11, '2026-06-30 19:05:00'), (5, 12, '2026-06-30 20:05:00'), (5, 13, '2026-07-01 09:05:00'), (5, 14, '2026-07-01 10:05:00'), (5, 15, '2026-07-01 11:05:00'),
(5, 16, '2026-07-01 12:05:00'), (5, 17, '2026-07-01 13:05:00'), (5, 18, '2026-07-01 14:05:00'),

-- Article 6 ~ 15: 雙位數 Bookmarks (皆包含 Member 1)
-- Article 6: 14 Bookmarks (Member 1 ~ 14)
(6, 1, '2026-07-03 09:05:00'), (6, 2, '2026-07-03 09:35:00'), (6, 3, '2026-07-03 10:05:00'), (6, 4, '2026-07-03 10:35:00'), (6, 5, '2026-07-03 11:05:00'),
(6, 6, '2026-07-03 11:35:00'), (6, 7, '2026-07-03 12:05:00'), (6, 8, '2026-07-03 13:05:00'), (6, 9, '2026-07-03 14:05:00'), (6, 10, '2026-07-03 15:05:00'),
(6, 11, '2026-07-03 16:05:00'), (6, 12, '2026-07-03 17:05:00'), (6, 13, '2026-07-03 18:05:00'), (6, 14, '2026-07-03 19:05:00'),

-- Article 7: 13 Bookmarks (Member 1 ~ 13)
(7, 1, '2026-07-07 09:05:00'), (7, 2, '2026-07-07 09:35:00'), (7, 3, '2026-07-07 10:05:00'), (7, 4, '2026-07-07 10:35:00'), (7, 5, '2026-07-07 11:05:00'),
(7, 6, '2026-07-07 11:35:00'), (7, 7, '2026-07-07 12:05:00'), (7, 8, '2026-07-07 13:05:00'), (7, 9, '2026-07-07 14:05:00'), (7, 10, '2026-07-07 15:05:00'),
(7, 11, '2026-07-07 16:05:00'), (7, 12, '2026-07-07 17:05:00'), (7, 13, '2026-07-07 18:05:00'),

-- Article 8: 14 Bookmarks (Member 1 ~ 14)
(8, 1, '2026-07-09 09:05:00'), (8, 2, '2026-07-09 09:35:00'), (8, 3, '2026-07-09 10:05:00'), (8, 4, '2026-07-09 10:35:00'), (8, 5, '2026-07-09 11:05:00'),
(8, 6, '2026-07-09 11:35:00'), (8, 7, '2026-07-09 12:05:00'), (8, 8, '2026-07-09 13:05:00'), (8, 9, '2026-07-09 14:05:00'), (8, 10, '2026-07-09 15:05:00'),
(8, 11, '2026-07-09 16:05:00'), (8, 12, '2026-07-09 17:05:00'), (8, 13, '2026-07-09 18:05:00'), (8, 14, '2026-07-09 19:05:00'),

-- Article 9: 12 Bookmarks (Member 1 ~ 12)
(9, 1, '2026-07-10 09:05:00'), (9, 2, '2026-07-10 09:35:00'), (9, 3, '2026-07-10 10:05:00'), (9, 4, '2026-07-10 10:35:00'), (9, 5, '2026-07-10 11:05:00'),
(9, 6, '2026-07-10 11:35:00'), (9, 7, '2026-07-10 12:05:00'), (9, 8, '2026-07-10 13:05:00'), (9, 9, '2026-07-10 14:05:00'), (9, 10, '2026-07-10 15:05:00'),
(9, 11, '2026-07-10 16:05:00'), (9, 12, '2026-07-10 17:05:00'),

-- Article 10: 13 Bookmarks (Member 1 ~ 13)
(10, 1, '2026-07-14 09:05:00'), (10, 2, '2026-07-14 09:35:00'), (10, 3, '2026-07-14 10:05:00'), (10, 4, '2026-07-14 10:35:00'), (10, 5, '2026-07-14 11:05:00'),
(10, 6, '2026-07-14 11:35:00'), (10, 7, '2026-07-14 12:05:00'), (10, 8, '2026-07-14 13:05:00'), (10, 9, '2026-07-14 14:05:00'), (10, 10, '2026-07-14 15:05:00'),
(10, 11, '2026-07-14 16:05:00'), (10, 12, '2026-07-14 17:05:00'), (10, 13, '2026-07-14 18:05:00'),

-- Article 11: 11 Bookmarks (Member 1 ~ 11)
(11, 1, '2026-07-15 09:05:00'), (11, 2, '2026-07-15 09:35:00'), (11, 3, '2026-07-15 10:05:00'), (11, 4, '2026-07-15 10:35:00'), (11, 5, '2026-07-15 11:05:00'),
(11, 6, '2026-07-15 11:35:00'), (11, 7, '2026-07-15 12:05:00'), (11, 8, '2026-07-15 13:05:00'), (11, 9, '2026-07-15 14:05:00'), (11, 10, '2026-07-15 15:05:00'),
(11, 11, '2026-07-15 16:05:00'),

-- Article 12: 12 Bookmarks (Member 1 ~ 12)
(12, 1, '2026-07-17 09:05:00'), (12, 2, '2026-07-17 09:35:00'), (12, 3, '2026-07-17 10:05:00'), (12, 4, '2026-07-17 10:35:00'), (12, 5, '2026-07-17 11:05:00'),
(12, 6, '2026-07-17 11:35:00'), (12, 7, '2026-07-17 12:05:00'), (12, 8, '2026-07-17 13:05:00'), (12, 9, '2026-07-17 14:05:00'), (12, 10, '2026-07-17 15:05:00'),
(12, 11, '2026-07-17 16:05:00'), (12, 12, '2026-07-17 17:05:00'),

-- Article 13: 10 Bookmarks (Member 1 ~ 10)
(13, 1, '2026-07-18 09:05:00'), (13, 2, '2026-07-18 09:35:00'), (13, 3, '2026-07-18 10:05:00'), (13, 4, '2026-07-18 10:35:00'), (13, 5, '2026-07-18 11:05:00'),
(13, 6, '2026-07-18 11:35:00'), (13, 7, '2026-07-18 12:05:00'), (13, 8, '2026-07-18 13:05:00'), (13, 9, '2026-07-18 14:05:00'), (13, 10, '2026-07-18 15:05:00'),

-- Article 14: 13 Bookmarks (Member 1 ~ 13)
(14, 1, '2026-07-20 09:05:00'), (14, 2, '2026-07-20 09:35:00'), (14, 3, '2026-07-20 10:05:00'), (14, 4, '2026-07-20 10:35:00'), (14, 5, '2026-07-20 11:05:00'),
(14, 6, '2026-07-20 11:35:00'), (14, 7, '2026-07-20 12:05:00'), (14, 8, '2026-07-20 13:05:00'), (14, 9, '2026-07-20 14:05:00'), (14, 10, '2026-07-20 15:05:00'),
(14, 11, '2026-07-20 16:05:00'), (14, 12, '2026-07-20 17:05:00'), (14, 13, '2026-07-20 18:05:00'),

-- Article 15: 11 Bookmarks (Member 1 ~ 11)
(15, 1, '2026-07-20 10:05:00'), (15, 2, '2026-07-20 10:35:00'), (15, 3, '2026-07-20 11:05:00'), (15, 4, '2026-07-20 11:35:00'), (15, 5, '2026-07-20 12:05:00'),
(15, 6, '2026-07-20 12:35:00'), (15, 7, '2026-07-20 13:05:00'), (15, 8, '2026-07-20 13:35:00'), (15, 9, '2026-07-20 14:05:00'), (15, 10, '2026-07-20 14:35:00'),
(15, 11, '2026-07-20 15:05:00');

-- ==========================================
-- {揪團活動}
-- 活動分類(activity_categories)
-- ==========================================
CREATE TABLE activity_categories (
  activity_cat_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  activity_cat_name VARCHAR(50) NOT NULL
);

INSERT INTO activity_categories VALUES 
(1, '公益社交'),  
(2, '健行登山'),  
(3, '輕鬆旅遊'),  
(4, '藝文手作'),  
(5, '冥想療癒'),  
(6, '運動健身');

-- ==========================================
-- 活動(activities)
-- ==========================================
CREATE TABLE activities (
  activity_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  activity_name VARCHAR(50) NOT NULL,
  member_id INT NOT NULL,
  activity_cat_id INT NOT NULL,
  admin_id INT,
  activity_content VARCHAR(1000) NOT NULL,
  activity_city VARCHAR(20) NOT NULL,
  activity_dist VARCHAR(20) NOT NULL,
  activity_loc VARCHAR(50) NOT NULL,
  picture VARCHAR(255),
  regis_start DATETIME NOT NULL,
  regis_end DATETIME NOT NULL,
  activity_start DATETIME NOT NULL,
  activity_end DATETIME NOT NULL,
  capacity INT NOT NULL,
  regis_count INT UNSIGNED NOT NULL DEFAULT 0,
  waitlist_capacity INT NOT NULL,
  waitlist_count INT UNSIGNED NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL,
  reviewed_at DATETIME,
  activity_status  TINYINT NOT NULL DEFAULT 0, -- 0待審核, 1已審核, 2已發布, 3已退回, 4取消, 5延期
  reject_reason TINYINT, -- 0資訊不完整, 1內容不當, 2名額設定異常, 3其他
  reject_note VARCHAR(200),
  cancel_note VARCHAR(200),
  postpone_note VARCHAR(200),
  scheduled_publish_at DATETIME,
  published_at DATETIME,
  updated_at DATETIME,
  CONSTRAINT fk_activities_member
		FOREIGN KEY (member_id) REFERENCES member(member_id),
  CONSTRAINT fk_activities_activity_categories
		FOREIGN KEY (activity_cat_id) REFERENCES activity_categories(activity_cat_id),
  CONSTRAINT fk_activities_admin
		FOREIGN KEY (admin_id) REFERENCES admin(admin_id)
);

-- 2. INSERT INTO tbName: 插入假資料 -- 
INSERT INTO activities (activity_status, activity_name, member_id, activity_cat_id, admin_id, activity_content, activity_city, 
   activity_dist, activity_loc, picture, regis_start, regis_end, activity_start, activity_end, capacity, regis_count,
   waitlist_capacity, waitlist_count,
   created_at, reviewed_at, reject_reason, reject_note, cancel_note, postpone_note, scheduled_publish_at, published_at, updated_at
)VALUES 
-- 1. 狀態 0：待審核(發起人:會員1)
(0, '秋日駁二傳遞溫暖 FREE HUGS', 1, 1, NULL,
  '在南臺灣熱情的陽光下，給彼此一個大大的擁抱吧！不需要任何條件，只需要帶著一顆溫暖的心，一起在週末傳遞正能量。',
  '高雄市', '鹽埕區', '駁二藝術特區', '1784226224189_4b4617a4.jpg', '2026-09-01 12:00:00', '2026-09-18 23:59:59', '2026-09-26 14:00:00', '2026-09-26 19:00:00',
   15, 0, 5, 0, '2026-08-15 14:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
 ),
-- 2. 狀態 1：已審核(發起人:會員1)【已修正：created_at/reviewed_at/updated_at 原本晚於「今天」2026-07-20，已審核動作卻發生在未來，邏輯不合，改為今天之前】
(1, '陽明山輕鬆健行半日遊', 1, 2, 1,
  '享受大自然芬多精，這是一條適合新手的健行路線，沿途風景優美，路勢平緩，歡迎大家一起來放鬆～',
  '臺北市', '士林區', '陽明山擎天崗', '1784522369051_3238261f.jpg', '2026-08-01 12:00:00', '2026-08-15 23:59:59', '2026-09-01 07:30:00', '2026-09-01 09:30:00', 
  20, 0, 5, 0, '2026-07-15 09:00:00', '2026-07-17 14:00:00', NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-17 14:00:00'
),
-- 3. 狀態 2：已發布(發起人:會員2)有2筆成功報名
(2, '花東看海發呆三天兩夜小旅行', 2, 3, 5,
  '拋開工作的疲憊，一起到花東海岸線看海、發呆、放空。這是一場不需要塞滿行程的療癒之旅，坐在海景民宿前聽著浪濤聲，找回內心的平靜。', 
   '花蓮縣', '新城鄉', '七星潭', '1784226172488_3e2c7de4.jpg', '2026-05-10 10:00:00', '2026-06-01 23:59:59', '2026-06-28 09:00:00', '2026-06-30 17:00:00', 
  10, 2, 3, 0, '2026-05-01 11:31:00', '2026-05-06 12:00:00', NULL, NULL, NULL, NULL, NULL, '2026-05-08 00:00:00', '2026-05-08 00:00:00'
),
-- 4. 狀態 3：已退回(發起人:會員1)【admin_id 由5改為1，補管理員1的退回審核情境】
(3, '臺北療癒乾燥花束手作體驗',  1, 4, 1,
  '大家一起來文青小店做美麗的乾燥花束吧！(現場酌收材料費用)。', 
  '臺北市', '中山區', '合江街', '1784226246151_a4bc5df9.jpg', '2026-07-01 00:00:00', '2026-07-15 23:59:59', '2026-07-25 14:00:00', '2026-07-25 18:00:00', 
  10, 0, 3, 0, '2026-06-23 15:00:00', '2026-06-25 09:30:00', 0, '審核退回：請補充活動詳細流程、材料費金額及包含項目，以及場地確切位置。', 
   NULL, NULL, NULL, NULL, '2026-06-25 09:30:00'
),
-- 5. 狀態 4：取消(發起人:會員2)容量2、額滿2、備取上限2、備取1人 → 測「已額滿」+「備取」
(4, '臺中綠園道頌缽冥想放鬆', 2, 5, 1,
  '透過尼泊爾頌缽的頻率，引導身心進入深層放鬆狀態，清理思緒，釋放累積一整週的壓力。', 
  '臺中市', '西區', 'Soul Chill Studio 用心生活工作室', '1784226136912_fa92337c.jpg', '2026-07-03 08:00:00', '2026-07-10 21:59:59', '2026-07-16 20:30:00', '2026-07-16 21:30:00', 
  2, 2, 2, 1, '2026-06-25 10:00:00', '2026-06-26 17:00:00', NULL, NULL, '因發起人罹患流感，故取消本次活動，請見諒。', NULL, NULL, '2026-06-27 19:00:00', '2026-07-14 10:00:00'
),
-- 6. 狀態 5：延期(發起人:會員2)有2筆成功報名【admin_id 由5改為1，補管理員1的延期審核情境】
(5, '大稻埕河濱輕鬆夜跑團', 2, 6, 1,
  '從大稻埕出發，沿著河濱享受微風，配速隨意，健康第一，跑完可以一起去迪化街吃宵夜或逛逛寧夏夜市。', 
  '臺北市', '大同區', '大稻埕碼頭廣場', '1784226162161_0fe567aa.jpg', '2026-08-10 00:00:00', '2026-08-22 23:59:59', '2026-08-29 19:30:00', '2026-08-29 20:30:00', 
  20, 2, 5, 0, '2026-08-01 08:00:00', '2026-08-02 11:00:00', NULL, NULL, NULL, '因氣象預報週末豪大雨，活動將延期舉行，時間另行通知。', NULL, '2026-08-06 10:00:00', '2026-08-25 20:00:00'
),
-- 7. 狀態 2：已發布+可報名中(發起人:會員2)→ 測報名功能
(2, '淡水夕陽漫步與心靈對話', 2, 2, 1,
  '傍晚時分沿著淡水河岸慢慢走，看夕陽沉入觀音山。步行途中安排簡單的正念引導，讓身體移動、讓思緒沉澱，適合想放慢腳步的你。',
  '新北市', '淡水區', '淡水漁人碼頭', '1784226149395_f46b31c1.jpg', '2026-07-01 00:00:00', '2026-08-20 23:59:59', '2026-08-27 17:00:00', '2026-08-27 19:30:00',
  4, 3, 0, 0, '2026-06-20 10:00:00', '2026-06-22 15:00:00', NULL, NULL, NULL, NULL, NULL, '2026-06-25 08:00:00', '2026-06-25 08:00:00'
),
-- 8. 狀態2：已發布+已結束(發起人:會員1)
(2, '阿里山日出輕鬆小旅行', 1, 3, 1,
  '一起搭乘小火車上山，等待日出灑落雲海的感動時刻，全程有專業導覽員隨行解說，適合想暫時遠離城市喧囂的你。',
  '嘉義縣', '阿里山鄉', '阿里山國家森林遊樂區', '1784522382115_d67d1f5c.jpg', '2026-05-20 00:00:00', '2026-06-05 23:59:59', '2026-06-20 05:00:00', '2026-06-21 10:00:00',
  15, 4, 3, 0, '2026-05-15 10:00:00', '2026-05-16 09:00:00', NULL, NULL, NULL, NULL, NULL, '2026-05-16 09:00:00', '2026-05-16 09:00:00'
),
-- 9. 狀態2：已發布+已結束(發起人:會員3)→ 3筆成功報名(含會員7)
(2, '臺南神農街文創手作市集體驗', 3, 4, 5,
  '走進老街巷弄，跟著在地職人學做屬於自己的文創小物，體驗結束後還能自由逛逛周邊特色小店，感受府城的悠閒步調。',
  '臺南市', '中西區', '神農街', '1784226097230_ad348ec2.jpg', '2026-05-25 00:00:00', '2026-06-05 23:59:59', '2026-06-10 14:00:00', '2026-06-10 17:00:00',
  12, 3, 3, 0, '2026-05-18 09:00:00', '2026-05-19 10:00:00', NULL, NULL, NULL, NULL, NULL, '2026-05-19 10:00:00', '2026-05-19 10:00:00'
),
-- 10. 狀態2：已發布+報名進行中，正取2/2滿、備取2/2也滿(發起人:會員1)→ 測「正取+備取都額滿」擋新申請
(2, '木柵山區手作陶藝療癒工作坊', 1, 4, 1,
  '在山林環繞的工作室裡，跟著陶藝老師從捏土開始，親手做出屬於自己的一件作品，過程專注而療癒，特別適合想暫時放空的你。',
  '臺北市', '文山區', '木柵陶坊', '1784522392938_a90c14fe.jpg', '2026-07-10 00:00:00', '2026-07-31 23:59:59', '2026-08-05 14:00:00', '2026-08-05 17:00:00',
  2, 2, 2, 2, '2026-07-05 09:00:00', '2026-07-06 10:00:00', NULL, NULL, NULL, NULL, NULL, '2026-07-06 10:00:00', '2026-07-06 10:00:00'
),
-- 11. 狀態1：已審核+已設定排程發布時間(發起人:會員3)→ 測「排程發布」功能
(1, '基隆海邊漫步與呼吸練習', 3, 5, 5,
  '沿著海岸步道慢慢走，配合簡單的呼吸引導練習，聽海浪聲放鬆緊繃的身心，適合想暫離塵囂、重新充電的你。',
  '基隆市', '中正區', '潮境公園', '1784522407153_9835de32.jpg', '2026-07-22 00:00:00', '2026-08-10 23:59:59', '2026-08-16 15:00:00', '2026-08-16 17:30:00',
  10, 0, 3, 0, '2026-07-17 10:00:00', '2026-07-19 11:00:00', NULL, NULL, NULL, NULL, '2026-07-20 09:00:00', NULL, '2026-07-19 11:00:00'
);

-- 12~30. 新增19筆，每分類補到5筆，且每個分類都有1筆「已發布(status=2)+報名進行中(未額滿)」
INSERT INTO activities (activity_status, activity_name, member_id, activity_cat_id, admin_id, activity_content, activity_city, 
   activity_dist, activity_loc, picture, regis_start, regis_end, activity_start, activity_end, capacity, regis_count,
   waitlist_capacity, waitlist_count,
   created_at, reviewed_at, reject_reason, reject_note, cancel_note, postpone_note, scheduled_publish_at, published_at, updated_at
) VALUES
-- 12. cat1 公益社交／狀態2 已發布可報名中(member4)
(2, '永和二手玩具捐贈市集', 4, 1, 1,
  '把家中孩子長大後用不到的玩具整理乾淨，捐贈給有需要的家庭，讓愛心物資能循環利用，現場也會有簡單的分類整理活動。',
  '新北市', '永和區', '永和社區活動中心', '1784522415473_4ce2c22a.jpg', '2026-07-05 00:00:00', '2026-08-15 23:59:59', '2026-08-25 14:00:00', '2026-08-25 17:00:00',
  15, 3, 5, 0, '2026-07-01 09:00:00', '2026-07-03 10:00:00', NULL, NULL, NULL, NULL, NULL, '2026-07-03 10:00:00', '2026-07-03 10:00:00'
),
-- 13. cat1／狀態0 待審核(member5)
(0, '毛孩陪伴療癒摸摸日', 5, 1, NULL,
  '邀請大家帶著家中的毛小孩，或是單純想來看看可愛狗狗紓壓也沒問題！現場會有專業訓犬師講解如何與狗狗安全互動，透過撫摸與陪伴，一起感受療癒的溫暖時光。',
  '臺北市', '大安區', '大安森林公園', '1784522471164_ad244113.jpg', '2026-08-01 00:00:00', '2026-08-20 23:59:59', '2026-08-30 10:00:00', '2026-08-30 16:00:00',
  10, 0, 3, 0, '2026-07-18 15:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
),
-- 14. cat1／狀態1 已審核未發布(member6)
(1, '偏鄉部落假日陪讀志工日', 6, 1, 5,
  '前往偏鄉部落陪伴孩子們一起寫作業、閱讀故事書，用一個下午的時間，陪他們認識更寬廣的世界，也讓孩子感受到有人在乎他們的成長。',
  '桃園市', '中壢區', '中壢區公所', '1784522575080_39279726.jpg', '2026-08-05 00:00:00', '2026-08-25 23:59:59', '2026-09-05 09:00:00', '2026-09-05 12:00:00',
  12, 0, 3, 0, '2026-07-10 09:00:00', '2026-07-12 11:00:00', NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-12 11:00:00'
),
-- 15. cat1／狀態2 已結束(member2)
(2, '愛心園遊會：陪伴弱勢家庭度過美好周末', 2, 1, 1,
  '準備各式小禮物與遊戲攤位，邀請弱勢家庭的孩子們一起同樂，透過遊戲與陪伴，讓他們感受到被關心、被重視的溫暖。',
  '新北市', '板橋區', '板橋第一運動場', '1784522942657_f2f2f5fe.jpg', '2026-05-05 00:00:00', '2026-05-20 23:59:59', '2026-06-01 10:00:00', '2026-06-01 16:00:00',
  10, 3, 3, 0, '2026-05-01 09:00:00', '2026-05-03 10:00:00', NULL, NULL, NULL, NULL, NULL, '2026-05-03 10:00:00', '2026-06-02 09:00:00'
),
-- 16. cat2 健行登山／狀態2 已結束(member3)
(2, '貓空茶山步道半日行', 3, 2, 5,
  '走訪貓空茶山步道，沿途欣賞茶園風光，終點還能品嚐道地的鐵觀音，適合喜歡輕鬆健行又想放鬆的朋友。',
  '臺北市', '文山區', '貓空纜車站', '1784523030449_346037ca.jpg', '2026-05-15 00:00:00', '2026-05-30 23:59:59', '2026-06-05 09:00:00', '2026-06-05 13:00:00',
  8, 2, 3, 0, '2026-05-10 10:00:00', '2026-05-12 11:00:00', NULL, NULL, NULL, NULL, NULL, '2026-05-12 11:00:00', '2026-06-06 10:00:00'
),
-- 17. cat2／狀態4 取消(member4)
(4, '合歡山主峰輕鬆健走', 4, 2, 1,
  '挑戰百岳中最容易親近的合歡山主峰，沿途高山景致壯闊，適合想嘗試高山健行的新手。',
  '南投縣', '仁愛鄉', '合歡山遊客中心', '1784526433130_748c69de.jpg', '2026-06-05 00:00:00', '2026-06-20 23:59:59', '2026-07-01 06:00:00', '2026-07-01 12:00:00',
  6, 2, 2, 0, '2026-06-01 09:00:00', '2026-06-03 10:00:00', NULL, NULL, '因高山天候預報不佳，考量成員安全故取消本次活動，造成不便敬請見諒。', NULL, NULL, '2026-06-03 10:00:00', '2026-06-25 08:00:00'
),
-- 18. cat2／狀態0 待審核(member6)
(0, '象山親山步道夜景健行', 6, 2, NULL,
  '趁著夜晚涼爽的天氣，一起登上象山欣賞臺北101與市區夜景，路程不長很適合新手嘗試。',
  '臺北市', '信義區', '象山親山步道口', '1784526494525_67815be6.jpg', '2026-08-10 00:00:00', '2026-08-25 23:59:59', '2026-09-10 18:00:00', '2026-09-10 20:30:00',
  15, 0, 4, 0, '2026-07-19 20:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
),
-- 19. cat3 輕鬆旅遊／狀態2 已發布可報名中(member5)
(2, '九份老街懷舊半日遊', 5, 3, 5,
  '漫步在充滿懷舊氣息的九份老街，品嚐芋圓、欣賞山城與海景交織的獨特風光，感受電影場景般的氛圍。',
  '新北市', '瑞芳區', '九份老街', '1784526622032_c7de88c9.jpg', '2026-07-05 00:00:00', '2026-08-05 23:59:59', '2026-08-15 10:00:00', '2026-08-15 16:00:00',
  8, 2, 3, 0, '2026-07-02 09:00:00', '2026-07-04 10:00:00', NULL, NULL, NULL, NULL, NULL, '2026-07-04 10:00:00', '2026-07-04 10:00:00'
),
-- 20. cat3／狀態1 已審核+已設定排程發布(member1)
(1, '墾丁南灣海邊放空小旅行', 1, 3, 1,
  '到墾丁南灣感受陽光沙灘，白天可以玩水上活動，傍晚坐在沙灘上看夕陽，徹底放鬆身心。',
  '屏東縣', '恆春鎮', '南灣沙灘', '1784526630960_ea1847da.jpg', '2026-08-01 00:00:00', '2026-08-15 23:59:59', '2026-08-22 09:00:00', '2026-08-23 17:00:00',
  12, 0, 3, 0, '2026-07-14 10:00:00', '2026-07-16 11:00:00', NULL, NULL, NULL, NULL, '2026-07-21 09:00:00', NULL, '2026-07-16 11:00:00'
),
-- 21. cat3／狀態3 已退回(member4)
(3, '台東熱氣球嘉年華二日遊', 4, 3, 5,
  '一起前往台東鹿野高台，欣賞色彩繽紛的熱氣球緩緩升空，還能安排體驗熱氣球繫留活動，留下難忘回憶。',
  '臺東縣', '鹿野鄉', '鹿野高台', '1784526639327_1e4e5a9f.jpg', '2026-07-15 00:00:00', '2026-07-30 23:59:59', '2026-08-08 05:00:00', '2026-08-09 17:00:00',
  10, 0, 3, 0, '2026-07-08 09:00:00', '2026-07-09 10:00:00', 0, '審核退回：熱氣球體驗屬高風險活動，請補充保險投保證明及安全防護措施說明。', NULL, NULL, NULL, NULL, '2026-07-09 10:00:00'
),
-- 22. cat4 藝文手作／狀態2 已發布可報名中(member2)
(2, '手工皂DIY療癒體驗', 2, 4, 1,
  '從基礎油品挑選開始，跟著老師一步步製作專屬於自己的手工皂，過程療癒又能學到實用的生活小知識。',
  '新北市', '新莊區', '新莊文化藝術中心', '1784526792909_ae32f73f.jpg', '2026-07-01 00:00:00', '2026-08-01 23:59:59', '2026-08-10 14:00:00', '2026-08-10 17:00:00',
  10, 2, 3, 0, '2026-06-25 09:00:00', '2026-06-27 10:00:00', NULL, NULL, NULL, NULL, NULL, '2026-06-27 10:00:00', '2026-06-27 10:00:00'
),
-- 23. cat4／狀態0 待審核(member7)
(0, '陶笛入門手作課程', 7, 4, NULL,
  '從吹奏基礎教起，帶大家認識陶笛這項可愛的樂器，課程結束後每人都能學會演奏一首簡單的小曲子。',
  '桃園市', '桃園區', '桃園市立圖書館', '1784526801254_f3601c24.jpg', '2026-08-05 00:00:00', '2026-08-20 23:59:59', '2026-08-28 14:00:00', '2026-08-28 16:30:00',
  12, 0, 3, 0, '2026-07-17 11:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
),
-- 24. cat5 冥想療癒／狀態2 已發布可報名中(member6)
(2, '瑜珈舒壓工作坊', 6, 5, 5,
  '透過緩和的瑜珈伸展動作，搭配呼吸引導，幫助釋放身體的緊繃與壓力，適合久坐辦公的上班族。',
  '臺北市', '松山區', 'Soul Yoga 瑜珈教室', '1784526809564_0a7e00ef.jpg', '2026-07-08 00:00:00', '2026-08-10 23:59:59', '2026-08-20 19:00:00', '2026-08-20 21:00:00',
  12, 4, 4, 0, '2026-07-05 09:00:00', '2026-07-07 10:00:00', NULL, NULL, NULL, NULL, NULL, '2026-07-07 10:00:00', '2026-07-07 10:00:00'
),
-- 25. cat5／狀態2 已結束(member3)
(2, '芳療精油放鬆體驗', 3, 5, 1,
  '認識天然精油的舒緩功效，並學習簡單的手部按摩技巧，在課程中好好放鬆緊繃的身心。',
  '臺中市', '北屯區', '心靈芳香工作室', '1784526888527_feaeeb33.jpg', '2026-05-20 00:00:00', '2026-06-01 23:59:59', '2026-06-10 14:00:00', '2026-06-10 16:30:00',
  10, 2, 3, 0, '2026-05-15 09:00:00', '2026-05-17 10:00:00', NULL, NULL, NULL, NULL, NULL, '2026-05-17 10:00:00', '2026-06-11 09:00:00'
),
-- 26. cat5／狀態5 延期(member7)
(5, '森林浴身心放鬆之旅', 7, 5, 5,
  '走進芬多精滿滿的森林步道，跟著引導員練習深呼吸與正念覺察，讓身心在大自然中獲得真正的休息。',
  '新竹縣', '尖石鄉', '馬告生態公園', '1784526951938_bc77845c.jpg', '2026-06-15 00:00:00', '2026-07-01 23:59:59', '2026-07-10 08:00:00', '2026-07-10 12:00:00',
  8, 2, 3, 0, '2026-06-10 09:00:00', '2026-06-12 10:00:00', NULL, NULL, NULL, '因颱風接近，考量成員安全故延期舉行，新場次時間將另行公告。', NULL, '2026-06-12 10:00:00', '2026-07-08 15:00:00'
),
-- 27. cat6 運動健身／狀態2 已發布可報名中(member4)
(2, '河濱單車輕旅行', 4, 6, 1,
  '沿著河濱自行車道悠閒騎乘，沿途欣賞河岸風光，中途安排休息站補給，適合想輕鬆運動又能欣賞風景的朋友。',
  '新北市', '新店區', '新店碧潭風景區', '1784527033315_c0629299.png', '2026-07-05 00:00:00', '2026-08-20 23:59:59', '2026-08-30 08:00:00', '2026-08-30 12:00:00',
  20, 3, 5, 0, '2026-07-01 09:00:00', '2026-07-03 10:00:00', NULL, NULL, NULL, NULL, NULL, '2026-07-03 10:00:00', '2026-07-03 10:00:00'
),
-- 28. cat6／狀態2 已結束(member5)
(2, '週末羽球團練交流賽', 5, 6, 5,
  '不限程度都能參加的羽球團練，現場會依照程度分組對打，讓大家都能盡興運動、交流球技。',
  '桃園市', '八德區', '八德區運動中心', '1784527088300_2992b72c.jpg', '2026-05-25 00:00:00', '2026-06-05 23:59:59', '2026-06-15 14:00:00', '2026-06-15 17:00:00',
  10, 2, 3, 0, '2026-05-20 09:00:00', '2026-05-22 10:00:00', NULL, NULL, NULL, NULL, NULL, '2026-05-22 10:00:00', '2026-06-16 09:00:00'
),
-- 29. cat6／狀態0 待審核(member1)
(0, '攀岩體驗營新手場', 1, 6, NULL,
  '在專業教練指導下體驗室內攀岩的樂趣，從基礎安全知識教起，完全沒有經驗也能安心參加。',
  '臺北市', '內湖區', '岩究所攀岩館', '1784527249884_9c6668d7.jpg', '2026-08-05 00:00:00', '2026-08-25 23:59:59', '2026-09-01 14:00:00', '2026-09-01 17:00:00',
  15, 0, 4, 0, '2026-07-19 10:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
),
-- 30. cat6／狀態3 已退回(member2)
(3, '夜間路跑挑戰賽10K', 2, 6, 1,
  '夜晚涼爽的氣溫最適合路跑，全程10公里沿著河濱夜景奔跑，挑戰自我體能極限。',
  '臺北市', '中正區', '河濱公園河堤', '1784527446301_d7afdb53.jpg', '2026-07-10 00:00:00', '2026-07-25 23:59:59', '2026-08-05 19:00:00', '2026-08-05 21:30:00',
  12, 0, 3, 0, '2026-07-05 09:00:00', '2026-07-07 10:00:00', 3, '審核退回：夜間路跑活動請補充照明設備及緊急救護動線規劃，以確保參與者安全。', NULL, NULL, NULL, NULL, '2026-07-07 10:00:00'
);


-- 31~33. 新增3筆，發起人皆為會員1，分別為「取消」「延期(尚未確認新時間)」「延期已確認新時間(重新發布)」
INSERT INTO activities (activity_status, activity_name, member_id, activity_cat_id, admin_id, activity_content, activity_city, 
   activity_dist, activity_loc, picture, regis_start, regis_end, activity_start, activity_end, capacity, regis_count,
   waitlist_capacity, waitlist_count,
   created_at, reviewed_at, reject_reason, reject_note, cancel_note, postpone_note, scheduled_publish_at, published_at, updated_at
) VALUES
-- 31. cat5／狀態4 取消(member1)
(4, '華山大草原睡午覺', 1, 5, 1,
  '帶著野餐墊跟一顆放空的心，一起到華山大草原曬曬太陽、睡個舒服的午覺，遠離城市喧囂，什麼都不用做，單純享受發呆放鬆的午後時光。',
  '臺北市', '中正區', '華山大草原', '1784604680348_4633738c.jpg', '2026-06-10 00:00:00', '2026-06-25 23:59:59', '2026-07-05 14:00:00', '2026-07-05 16:30:00',
  10, 2, 3, 0, '2026-06-05 09:00:00', '2026-06-07 10:00:00', NULL, NULL, '因草地近期進行草皮養護整地，暫停對外開放，故取消本次活動，造成不便敬請見諒。', NULL, NULL, '2026-06-07 10:00:00', '2026-06-20 14:00:00'
),
-- 32. cat1／狀態5 延期(尚未確認新時間)(member1)
(5, '揪團學Java', 1, 1, 1,
  '揪一群想學Java的夥伴，一起找個下午安靜寫code、互相討論解題邏輯、分享學習資源，不論是自學或正在上課的新手都歡迎，一起把程式基礎打穩！',
  '桃園市', '中壢區', '緯育TibaMe附設中壢職訓中心', '1784604692907_2a5bada5.jpg', '2026-07-01 00:00:00', '2026-07-15 23:59:59', '2026-08-01 13:30:00', '2026-08-01 17:30:00',
  12, 2, 3, 0, '2026-06-25 09:00:00', '2026-06-27 10:00:00', NULL, NULL, NULL, '因發起人臨時有工作專案需處理，故延期舉行，新場次時間將另行公告。', NULL, '2026-06-27 10:00:00', '2026-07-18 15:00:00'
),
-- 33. cat3／狀態2 延期後已確認新時間(重新發布，含延期紀錄)(member1)
(2, '大湖公園野餐', 1, 3, 1,
  '帶著野餐墊、輕食點心，一起到大湖公園草地野餐，欣賞湖畔風景，享受悠閒的假日午後時光，適合想放鬆聊天、曬曬太陽的朋友。',
  '臺北市', '內湖區', '大湖公園', '1784622373683_ee29f1c3.jpg', '2026-06-01 00:00:00', '2026-08-10 23:59:59', '2026-08-16 14:00:00', '2026-08-16 17:00:00',
  3, 3, 4, 0, '2026-05-28 09:00:00', '2026-05-30 10:00:00', NULL, NULL, NULL, '因原定日期天氣不佳緊急延期，現已確認新場次時間為8/16，改為午後野餐時段，造成不便敬請見諒。', NULL, '2026-05-30 10:00:00', '2026-07-19 16:00:00'
);

-- ==========================================
-- 關注活動(activity_follows)
-- ==========================================
CREATE TABLE activity_follows(
  member_id INT NOT NULL,
  activity_id INT NOT NULL,
  followed_at DATETIME NOT NULL,
  PRIMARY KEY (member_id, activity_id),
  CONSTRAINT fk_activity_follows_member
		FOREIGN KEY (member_id) REFERENCES member(member_id),
  CONSTRAINT fk_activity_follows_activities
		FOREIGN KEY (activity_id) REFERENCES activities(activity_id)
);

INSERT INTO activity_follows VALUES
(1, 2, '2026-08-05 10:30:00'),-- 會員 1 關注了活動 2 
(2, 5, '2026-06-27 20:30:00'),-- 會員 2 關注了活動 5、活動 7
(2, 7, '2026-07-08 21:00:00'),
(5, 7, '2026-07-10 09:00:00'),-- 會員 5 關注了活動 7
(3, 7, '2026-07-11 10:00:00'),
(4, 7, '2026-07-13 08:00:00'),
(1, 12, '2026-07-15 09:00:00'),
(3, 12, '2026-07-16 10:30:00'),
(5, 12, '2026-07-17 11:00:00'),
(2, 15, '2026-05-03 09:30:00'),
(6, 16, '2026-05-11 14:00:00'),
(1, 19, '2026-07-18 08:30:00'),
(7, 19, '2026-07-19 09:15:00'),
(2, 22, '2026-06-26 13:00:00'),
(6, 22, '2026-07-17 15:00:00'),
(3, 24, '2026-07-14 10:00:00'),
(4, 26, '2026-06-11 09:00:00'),
(1, 27, '2026-07-18 20:00:00'),
(5, 27, '2026-07-19 07:30:00'),
(6, 28, '2026-05-21 12:00:00');

-- ==========================================
-- 報名申請(activity_registrations)
-- ==========================================
CREATE TABLE activity_registrations (
  regis_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  member_id INT NOT NULL,
  activity_id INT NOT NULL,
  regis_status TINYINT NOT NULL DEFAULT 0, -- 0待審核, 1已報名成功(正取), 2已拒絕/報名失敗, 3已取消報名, 4已報名成功(備取)
  regis_at DATETIME NOT NULL,
  motivation VARCHAR(200) NOT NULL,
  cancelled_at DATETIME,
  cancel_reason TINYINT,                   -- 0行程衝突, 1身體不適, 2其他
  cancel_note VARCHAR(200),
  reject_reason TINYINT,                   -- 0報名表單填寫不明確, 1不符合活動資格條件, 2其他
  reject_note VARCHAR(200),
  review_content VARCHAR(500),
  rating TINYINT UNSIGNED,
  reviewed_at DATETIME,
  CONSTRAINT fk_activity_registrations_member 
        FOREIGN KEY (member_id) REFERENCES member(member_id),
  CONSTRAINT fk_activity_registrations_activities 
        FOREIGN KEY (activity_id) REFERENCES activities(activity_id)
);

INSERT INTO activity_registrations (
  regis_status, member_id, activity_id, regis_at, motivation, cancelled_at, 
  cancel_reason, cancel_note, reject_reason, reject_note, review_content, rating, reviewed_at
) VALUES 
-- 1. 狀態 0：待審核(會員4 報 7號活動)→ 測審核功能【已修正：原本報名的是1號活動，但1號活動當時仍是「待審核」狀態、報名期間也還沒開始(regis_start 2026-09-01)，尚未發布就不可能收到報名，改為報名已發布且報名中的7號活動】
(1, 4, 7, '2026-07-15 10:00:00', '一直很想參加公益類的活動，希望能盡一份心力也交到新朋友。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 2. 狀態 1：已報名成功+已留評論(會員3 報 3號活動)
(1, 3, 3, '2026-06-01 09:00:00', '最近工作壓力大，想找個安靜的地方放空休息一下。', NULL, NULL, NULL, NULL, NULL, '風景很美，團主很親切，週末去走走非常放鬆！', 5, '2026-07-02 10:00:00'),
-- 3. 狀態 1：已報名成功、未評論(會員1 報 3號活動)→ 測評論功能
(1, 1, 3, '2026-07-15 14:00:00', '朋友推薦這個行程，想體驗看看花東的悠閒步調。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 4. 狀態 4：備取成功(會員4 報 5號活動,正取已滿改進備取)→ 測「備取」畫面
(4, 4, 5, '2026-07-06 14:00:00', '對頌缽冥想很有興趣，想嘗試放鬆身心的體驗。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 5. 狀態 3：已取消報名(會員4 報 6號活動後取消)
(3, 4, 6, '2026-08-11 20:00:00', '想培養運動習慣，找個輕鬆的夜跑團一起跑。', '2026-08-20 09:00:00',  0, '臨時被公司安排出差，很遺憾無法參加', NULL, NULL, NULL, NULL, NULL),
 -- 6. 狀態 3：已取消(會員1 報 6號活動後取消,之後8/11又重新報名成功=第9筆)→ 驗排序+「取消後可再報」
(3, 1, 6, '2026-08-02 15:00:00', '想趁週末運動一下，順便認識新朋友。', '2026-08-05 21:00:00', 1, '因生病取消報名', NULL, NULL, NULL, NULL, NULL),
-- 7. 狀態 1：5號活動的額滿成員之一(會員1)
(1, 1, 5, '2026-07-04 10:00:00', '最近壓力很大，想透過頌缽放鬆一下身心。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 8. 狀態 1：5號活動的額滿成員之二(會員3)
(1, 3, 5, '2026-07-05 09:00:00', '朋友說這個活動很療癒，想親自體驗看看。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 9. 狀態 1：6號活動的成功報名(會員1)
(1, 1, 6, '2026-08-11 08:00:00', '取消上次的報名後，想重新找時間再參加一次。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 10. 狀態 1：6號活動的成功報名(會員3)
(1, 3, 6, '2026-08-12 09:30:00', '喜歡河濱夜跑的感覺，想認識更多同好。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 11. 狀態 1：8號活動的成功報名(會員2)
(1, 2, 8, '2026-05-25 10:00:00', '一直想去阿里山看日出，這次終於有機會參加。', NULL, NULL, NULL, NULL, NULL, '雲海跟日出真的很壯觀，導覽員解說也很專業，小火車體驗很特別，很推薦給喜歡自然風景的朋友！', 5, '2026-06-22 10:00:00'),
-- 12. 狀態 1：8號活動的成功報名(會員3)
(1, 3, 8, '2026-05-26 11:00:00', '想暫時遠離城市喧囂，體驗山林間的日出美景。', NULL, NULL, NULL, NULL, NULL, '整體行程安排得很順暢，唯一小缺點是小火車座位有點擠，其他都很棒，會想再參加一次。', 4, '2026-06-23 09:00:00'),
(1, 5, 8, '2026-05-27 09:00:00', '朋友分享了照片後就很心動，決定跟著一起報名去看日出。', NULL, NULL, NULL, NULL, NULL, '第一次看到雲海日出，真的很感動，導覽員講的在地故事也很有趣，很值得一去！', 5, '2026-06-24 10:00:00'),
(1, 6, 8, '2026-05-28 14:00:00', '想找個機會遠離城市，放空一下順便拍些漂亮的照片。', NULL, NULL, NULL, NULL, NULL, '天氣不錯，日出畫面很美，只是清晨集合時間有點早，建議想參加的人要提前做好保暖準備。', 4, '2026-06-25 11:00:00'),
-- 13. 狀態 1：9號活動的成功報名(會員1)
(1, 1, 9, '2026-05-27 09:30:00', '對文創手作一直很有興趣，想學做屬於自己的作品。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 14. 狀態 1：9號活動的成功報名(會員2)
(1, 2, 9, '2026-05-28 14:00:00', '想趁假日到台南走走，順便體驗在地文創手作。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 15. 狀態 2：團主拒絕(會員4 報 7號活動,被拒絕)→ 測「拒絕」畫面
(2, 4, 7, '2026-07-05 10:00:00', '想去', NULL, NULL, NULL, 0, '報名表單內容填寫過於簡略，看不出參加意願', NULL, NULL, NULL),
-- 16~17. 10號活動：正取2人已滿(會員2、會員3)
(1, 2, 10, '2026-07-06 11:00:00', '對陶藝手作很有興趣，想親手做出一件作品帶回家。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 3, 10, '2026-07-06 15:00:00', '想找一個能靜下心來、專注做點什麼的活動。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 18. 10號活動：備取1人也已滿(會員4)
(4, 4, 10, '2026-07-08 10:00:00', '第一次接觸陶藝，很期待能體驗看看。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 19~20. 7號活動：待審核2筆(會員5、會員6)→ 測多筆待審核的審核情境
(1, 5, 7, '2026-07-12 14:00:00', '平常工作步調很快，想試試放慢腳步、練習正念的活動。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 6, 7, '2026-07-14 19:30:00', '喜歡淡水的夕陽，也想學習讓思緒沉澱的方法。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 21. 狀態 1：9號活動的成功報名+已留評論(會員7)→ 豐富心得總覽
(1, 7, 9, '2026-05-29 16:00:00', '很喜歡逛老街和手作小物，希望能親手做一個紀念品。', NULL, NULL, NULL, NULL, NULL, '老師教得很仔細，做出來的小物很有紀念價值，還順便逛了神農街，很充實的一天！', 4, '2026-06-12 20:00:00'),
-- activity 12(open) 共6筆：3正取+2待審核+1拒絕
(1, 2, 12, '2026-07-06 10:00:00', '看到活動想幫忙整理家裡孩子的舊玩具，也想盡一份心力。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 4, 12, '2026-07-08 14:00:00', '家裡剛好有一些堪用的玩具，想捐出來給需要的家庭。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 6, 12, '2026-07-10 09:30:00', '很認同二手物資循環利用的理念，想一起參與整理工作。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(0, 3, 12, '2026-07-15 11:00:00', '想帶小朋友一起參加，順便教他分享的觀念。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(0, 1, 12, '2026-07-17 15:00:00', '看到活動宗旨很有意義，想報名參加盡一份心力。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 7, 12, '2026-07-05 09:00:00', '想參加', NULL, NULL, NULL, 0, '報名表單填寫過於簡略，看不出參加意願，請補充完整說明後重新報名。', NULL, NULL, NULL),
-- activity 15(已結束) 共4筆：3正取(2有評論)+1取消
(1, 1, 15, '2026-05-06 10:00:00', '想趁假日為弱勢家庭盡一份心力。', NULL, NULL, NULL, NULL, NULL, '活動很溫馨，看到孩子們開心的笑容覺得很值得，希望以後還能參加類似的活動。', 5, '2026-06-03 10:00:00'),
(1, 3, 15, '2026-05-08 11:00:00', '想在中秋節前夕為弱勢家庭盡一份心力。', NULL, NULL, NULL, NULL, NULL, '園遊會攤位安排豐富，志工也很熱情，是一次很棒的體驗。', 5, '2026-06-03 10:00:00'),
(1, 5, 15, '2026-05-10 09:00:00', '看到活動介紹覺得很有意義，想一起參與。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(3, 4, 15, '2026-05-12 14:00:00', '想幫忙布置攤位，貢獻一點心力。', '2026-05-25 09:00:00', 0, '臨時工作加班無法配合活動時間，很抱歉。', NULL, NULL, NULL, NULL, NULL),
-- activity 16(已結束) 共3筆：2正取(有評論)+1取消
(1, 2, 16, '2026-05-16 10:00:00', '一直很想去貓空喝茶看風景，這次終於有機會參加。', NULL, NULL, NULL, NULL, NULL, '茶園風景很美，導覽也很仔細，品茶體驗很棒！', 5, '2026-06-07 10:00:00'),
(1, 6, 16, '2026-05-18 11:00:00', '喜歡輕鬆的步道行程，想趁假日走走放鬆一下。', NULL, NULL, NULL, NULL, NULL, '步道難度剛好適合新手，還能順便品茶，很推薦。', 4, '2026-06-07 10:00:00'),
(3, 7, 16, '2026-05-20 09:00:00', '想體驗貓空的茶山步道風景。', '2026-05-28 10:00:00', 2, '臨時有事無法配合活動時間。', NULL, NULL, NULL, NULL, NULL),
-- activity 17(取消) 共2筆：取消前已成功報名2人
(1, 3, 17, '2026-06-06 10:00:00', '一直很想挑戰合歡山主峰，這次難得有機會報名。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 5, 17, '2026-06-08 11:00:00', '想體驗高山健行的感覺，聽說合歡山很適合新手。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- activity 19(open) 共3筆：2正取+1待審核
(1, 1, 19, '2026-07-06 10:00:00', '很喜歡九份的懷舊氛圍，想找時間再去走走拍照。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 7, 19, '2026-07-08 14:00:00', '聽朋友說九份芋圓很好吃，想跟著行程一起去嚐鮮。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(0, 4, 19, '2026-07-16 09:00:00', '很喜歡老街的氛圍，想報名一起去走走。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- activity 22(open) 共4筆：2正取+1待審核+1拒絕
(1, 3, 22, '2026-06-26 10:00:00', '一直很想學做手工皂，這次終於有機會親自體驗。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 5, 22, '2026-06-28 11:00:00', '想學一個新的手作技能，順便放鬆心情。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(0, 6, 22, '2026-07-15 15:00:00', '對天然手工皂很有興趣，想報名體驗看看。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 2, 22, '2026-06-25 09:30:00', '想參加', NULL, NULL, NULL, 0, '報名表單填寫過於簡略，請補充參加動機後重新報名。', NULL, NULL, NULL),
-- activity 24(open) 共5筆：4正取+1待審核
(1, 1, 24, '2026-07-06 10:00:00', '最近工作壓力大，想透過瑜珈舒緩緊繃的身心。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 4, 24, '2026-07-08 11:00:00', '想開始培養規律運動的習慣，瑜珈感覺很適合入門。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 7, 24, '2026-07-10 14:00:00', '聽同事推薦這堂瑜珈課，想親自體驗看看。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 3, 24, '2026-07-12 09:00:00', '久坐辦公室肩頸很緊繃，希望能透過瑜珈放鬆。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(0, 2, 24, '2026-07-17 16:00:00', '想找一個舒壓的活動，瑜珈感覺很不錯。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- activity 25(已結束) 共3筆：2正取(有評論)+1取消
(1, 3, 25, '2026-05-21 10:00:00', '工作壓力大，想透過芳療課程放鬆一下。', NULL, NULL, NULL, NULL, NULL, '精油香氣很療癒，按摩手法也學到很多，回家後自己練習很有成就感。', 5, '2026-06-12 10:00:00'),
(1, 6, 25, '2026-05-23 11:00:00', '對芳療一直很有興趣，想學一些實用的按摩技巧。', NULL, NULL, NULL, NULL, NULL, '老師教學很仔細，內容豐富，很推薦給想放鬆的朋友。', 4, '2026-06-12 10:00:00'),
(3, 5, 25, '2026-05-25 09:00:00', '想嘗試精油按摩，聽說對舒眠很有幫助。', '2026-06-05 10:00:00', 1, '身體不適無法配合活動時間，很抱歉。', NULL, NULL, NULL, NULL, NULL),
-- activity 26(延期) 共2筆：延期前已成功報名2人
(1, 2, 26, '2026-06-16 10:00:00', '很想到森林裡走走，透過森林浴放鬆身心。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 4, 26, '2026-06-18 11:00:00', '平常很少接觸大自然，想趁機會放慢腳步呼吸新鮮空氣。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- activity 27(open) 共4筆：3正取+1待審核
(1, 1, 27, '2026-07-06 10:00:00', '喜歡騎單車運動，想趁假日到河濱走走。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 3, 27, '2026-07-08 11:00:00', '想培養騎單車的習慣，這條路線聽說風景很不錯。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 6, 27, '2026-07-10 14:00:00', '週末想找點輕鬆的運動，單車輕旅行感覺很適合。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(0, 4, 27, '2026-07-18 09:00:00', '喜歡河濱風景，想報名一起騎車去看看。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- activity 28(已結束) 共3筆：2正取(1有評論)+1取消
(1, 2, 28, '2026-05-26 10:00:00', '喜歡打羽球，想找機會認識更多同好一起練球。', NULL, NULL, NULL, NULL, NULL, '分組安排很用心，程度相近對打起來很盡興，交到不少新朋友。', 5, '2026-06-16 10:00:00'),
(1, 5, 28, '2026-05-28 11:00:00', '平常沒什麼機會打羽球，想趁這次活動練習一下。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(3, 6, 28, '2026-05-30 09:00:00', '想參加羽球團練活動。', '2026-06-10 10:00:00', 0, '臨時有其他行程安排，無法配合活動時間，很抱歉。', NULL, NULL, NULL, NULL, NULL),
-- 會員1報名紀錄補充：待審核/備取/被拒絕 各一筆
(4, 1, 10, '2026-07-09 09:00:00', '對陶藝手作也很有興趣，想跟朋友一起報名體驗看看。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(0, 1, 22, '2026-07-18 10:00:00', '想學做手工皂，順便體驗一下手作課程的樂趣。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 1, 25, '2026-05-19 09:00:00', '想上課', NULL, NULL, NULL, 0, '報名表單填寫過於簡略，看不出參加動機，請補充完整說明後重新報名。', NULL, NULL, NULL),
-- activity 31(取消) 共2筆：取消前已成功報名2人
(1, 3, 31, '2026-06-08 10:00:00', '最近工作壓力很大，很需要一個什麼都不用做、單純放空睡午覺的活動。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 6, 31, '2026-06-10 11:00:00', '平常很少有機會在戶外好好躺著休息，想趁這次活動徹底放鬆一下。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- activity 32(延期尚未確認新時間) 共2筆：延期前已成功報名2人
(1, 4, 32, '2026-06-28 10:00:00', '最近開始自學Java，想找一群夥伴一起讀書互相討論，比較不會半路放棄。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 7, 32, '2026-06-30 11:00:00', '工作上常常需要用到程式基礎，想趁機會加強Java，也想認識同樣在學程式的朋友。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- activity 33(延期已確認新時間) 共3筆：原本已成功報名2人+確認新時間後新加入1人
(1, 2, 33, '2026-05-31 10:00:00', '想找個悠閒的午後野餐透氣，順便認識新朋友。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 5, 33, '2026-06-02 11:00:00', '住得離大湖公園不遠，很適合帶點心去野餐放鬆一下。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 3, 33, '2026-07-20 09:00:00', '看到延期後確認的新時間剛好方便參加，決定報名一起去野餐曬太陽。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 補：會員1發起的活動中，補一筆待審核報名，供「團主審核」功能展示
(0, 6, 33, '2026-07-19 10:00:00', '看到活動內容很吸引人，想找個午後放鬆一起野餐。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 補：正取名額已滿(capacity調整為3)，這筆核准後會變成候補，供「候補」與後續「候補遞補正取」功能展示
(0, 7, 33, '2026-07-19 15:00:00', '朋友說這個活動很輕鬆愜意，想找機會一起去大湖公園走走野餐。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ==========================================
-- 活動問題回報(activity_reports)
-- ==========================================
CREATE TABLE activity_reports (
  report_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  member_id INT NOT NULL,
  activity_id INT NOT NULL,
  admin_id INT,
  report_content VARCHAR(500) NOT NULL,
  report_status TINYINT NOT NULL DEFAULT 0, -- 0: 待處理, 1: 處理中, 2: 已處理
  reply_content VARCHAR(500),
  created_at DATETIME NOT NULL,
  replied_at DATETIME,
  CONSTRAINT fk_activity_reports_member 
        FOREIGN KEY (member_id) REFERENCES member(member_id),
  CONSTRAINT fk_activity_reports_activities 
        FOREIGN KEY (activity_id) REFERENCES activities(activity_id),
  CONSTRAINT fk_activity_reports_admin 
        FOREIGN KEY (admin_id) REFERENCES admin(admin_id)
);

INSERT INTO activity_reports (
  report_status, member_id, activity_id, admin_id, report_content, 
  reply_content, created_at, replied_at
) VALUES 
-- 狀態 0：待處理
(0, 3, 3, NULL, '領隊在集合時間遲到了將近 20 分鐘，且事前完全沒有通知大家，讓大家在太陽下乾等，希望平台能對主辦方加強規範。', 
 NULL, '2026-07-01 11:30:00', NULL),
(0, 2, 8, NULL, '遊覽車比預定時間晚了近半小時才抵達集合地點，導遊也沒有主動說明延誤原因，行程被迫壓縮，體驗大打折扣。', 
 NULL, '2026-06-22 09:00:00', NULL),
-- 狀態 1：處理中
(1, 1, 3, 5, '民宿安排的房型與當初活動頁面上說明的完全不同，四人房變成很擠的通鋪，覺得權益受損，希望能協助處理部分退費。', 
 NULL, '2026-07-02 10:00:00', NULL),
(1, 3, 8, 1, '導覽人員對當地的歷史文化介紹非常簡略，跟活動頁面上寫的深度導覽內容落差很大，感覺誠意不足。', 
 NULL, '2026-06-23 15:00:00', NULL),
-- 狀態 2：已處理
(2, 1, 9, 1, '老師原訂三小時的教學時間，實際上只教了不到兩小時就提前結束，內容也沒有完整教完，覺得CP值不如預期。', 
 '您好，感謝您的回報。經與主辦方確認，當天因場地租借時間限制提前結束，我們已提醒主辦方未來需完整規劃教學時數，也會加強場地租借時間評估，造成您的不便深感抱歉。', 
 '2026-06-12 14:00:00', '2026-06-13 10:00:00'),
(2, 2, 9, 5, '現場收取的材料費比當初平台上寫的多收了 100 元，雖然現場反映後有退還，但感覺整體收費流程有瑕疵。', 
 '您好，非常抱歉造成您的不愉快。經與主辦方聯繫確認，為現場工作人員溝通疏失。我們已對該主辦方發出警告，感謝您的回報。', 
 '2026-06-12 09:00:00', '2026-06-14 14:00:00'),
(0, 3, 15, NULL, '活動當天天氣炎熱，但現場沒有足夠的遮陽棚，體感非常不舒服。', NULL, '2026-06-03 14:00:00', NULL),
(1, 5, 15, 1, '報名時填寫的過敏資訊，現場工作人員似乎沒有留意到，差點吃到不適合的食物。', NULL, '2026-06-04 09:00:00', NULL),
(0, 2, 16, NULL, '步道部分路段濕滑且缺乏警示標誌，擔心安全問題。', NULL, '2026-06-06 11:00:00', NULL),
(1, 6, 16, 5, '領隊講解時音量太小，隊伍後段的人幾乎聽不清楚導覽內容。', NULL, '2026-06-07 15:00:00', NULL),
(2, 3, 17, 1, '活動臨時取消卻沒有提前太多通知，已經請假配合行程，造成困擾。', '您好，非常抱歉造成您的不便，主辦方因天候因素緊急取消活動，我們已建議主辦方未來需提前規劃備案並及早通知，感謝您的回報。', '2026-06-26 10:00:00', '2026-06-28 14:00:00'),
(2, 5, 17, 5, '取消後退費流程等了快兩週才收到款項，希望能加快退費速度。', '您好，感謝您的耐心等候，經確認為系統作業延遲，我們已加速處理並優化退費流程，造成不便深感抱歉。', '2026-06-27 11:00:00', '2026-07-05 10:00:00'),
(0, 3, 22, NULL, '活動頁面材料清單寫得不夠清楚，不確定現場材料費用是否包含在報名費內，希望能補充說明。', NULL, '2026-07-01 09:00:00', NULL),
(1, 5, 22, 1, '報名後一直沒有收到主辦方的確認通知，不確定是否報名成功，有點擔心。', NULL, '2026-07-02 10:00:00', NULL),
(0, 1, 24, NULL, '活動頁面沒有寫明場地是否提供瑜珈墊，不確定需不需要自備。', NULL, '2026-07-08 09:00:00', NULL),
(1, 4, 24, 5, '詢問活動細節後主辦方回覆速度很慢，超過三天才收到回音。', NULL, '2026-07-11 14:00:00', NULL),
(2, 7, 24, 1, '活動地點的交通指引不夠清楚，怕到時候會找不到集合地點。', '您好，感謝您的回報，我們已請主辦方在活動頁面補充詳細的交通與集合資訊，造成您的疑慮敬請見諒。', '2026-07-13 10:00:00', '2026-07-15 11:00:00'),
(0, 3, 25, NULL, '現場使用的精油味道過於濃烈，部分學員反映頭暈不適。', NULL, '2026-06-11 09:00:00', NULL),
(1, 6, 25, 5, '體驗課程原訂內容和活動頁面描述有落差，少了手部按摩教學環節。', NULL, '2026-06-13 10:00:00', NULL),
(2, 2, 26, 1, '活動延期公告太晚發出，前一天才通知，差點白跑一趟。', '您好，非常抱歉造成您的困擾，主辦方因氣象預報臨時更新才緊急決定延期，我們已請主辦方未來提前掌握氣象資訊並及早公告，感謝您的體諒。', '2026-07-09 10:00:00', '2026-07-11 14:00:00'),
(0, 4, 26, NULL, '延期後新的活動日期一直沒有公布，希望能盡快提供確切時間。', NULL, '2026-07-15 09:00:00', NULL),
(1, 2, 28, 5, '場地借用的羽球場地板有點濕滑，擔心運動傷害風險。', NULL, '2026-06-16 11:00:00', NULL),
(0, 5, 28, NULL, '團練分組不太均衡，程度落差大導致部分場次一面倒。', NULL, '2026-06-17 14:00:00', NULL),
(2, 6, 28, 1, '活動結束後場地清潔沒有安排好，垃圾桶太快就滿出來了。', '您好，感謝您的回報，我們已與場地方確認並增加清潔頻率，造成不便深感抱歉。', '2026-06-18 09:00:00', '2026-06-20 10:00:00'),
(2, 1, 5, 1, '活動被取消後客服協助處理退費的過程算滿順利的，只是希望以後能有更完整的替代場次建議。', '您好，感謝您的回饋，我們已請主辦方未來遇到取消狀況時，一併提供類似活動的替代選項，造成不便深感抱歉。', '2026-07-15 10:00:00', '2026-07-17 11:00:00'),
(1, 3, 6, 1, '活動已經延期快兩週了，卻完全沒有新場次的消息，希望能盡快說明後續安排。', NULL, '2026-08-13 10:00:00', NULL),
(0, 2, 10, NULL, '報名時系統顯示還有名額，送出後卻通知已經額滿，體驗不太好。', NULL, '2026-07-09 09:00:00', NULL),
(1, 7, 9, 5, '手作材料包裡有一項材料缺少，需要現場另外購買才能完成作品。', NULL, '2026-06-13 09:00:00', NULL),
(0, 1, 6, NULL, '延期後的新場地離捷運站有點遠，希望能補充交通資訊。', NULL, '2026-07-16 10:00:00', NULL),
-- 新增：待處理 1筆 + 管理員1受理中 1筆
(0, 6, 31, NULL, '活動臨時取消的通知發得太晚，前一天晚上才收到，差點白跑一趟，希望以後能提早通知。', NULL, '2026-06-21 09:00:00', NULL),
(1, 4, 32, 1, '活動延期後已經超過三週，卻完全沒有新場次的消息，也聯絡不到主辦方，希望平台能協助了解狀況。', NULL, '2026-07-19 10:00:00', NULL);
-- ==========================================
-- {諮商預約}
-- 預約時段表(consultation_slots)
-- ==========================================

CREATE TABLE consultation_slots (
	timeslot_id INT NOT NULL AUTO_INCREMENT,
    psych_id INT NOT NULL,
    slot_date DATE NOT NULL,
    appt_status VARCHAR(24) NOT NULL, -- 設定一天中的營業時間 (每日00~24，0:營業時間、可預約，2:非營業時間)
    
    PRIMARY KEY (timeslot_id),
    CONSTRAINT fk_consultation_slots_psychologist
 	FOREIGN KEY (psych_id) REFERENCES psychologist(psych_id)
    
	);
    
INSERT INTO consultation_slots (timeslot_id, psych_id, slot_date, appt_status) VALUES
(1, 1, '2026-07-13', '000000001111111110000000'), 
(2, 1, '2026-07-18', '000000001111111210000000'), 
(3, 1, '2026-07-25', '000000001111111110000000'), 
(4, 1, '2026-07-28', '000000001111111110000000'), 
(5, 2, '2026-07-10', '000011000000011211000000'), 
(6, 2, '2026-07-16', '000000000000111121111000'), 
(7, 2, '2026-07-24', '000011000000012111000000'); 
 
-- ==========================================
-- 諮詢訂單(consultation_orders)
-- ==========================================
 CREATE TABLE consultation_orders (
	order_id INT NOT NULL AUTO_INCREMENT,
    time_id INT NOT NULL,
    cons_start DATETIME NOT NULL,
    cons_end DATETIME NOT NULL,
    member_id INT NOT NULL,	
    psych_id INT NOT NULL,	
    created_at DATETIME NOT NULL,
    psych_loc VARCHAR(50) NOT NULL,
    order_status TINYINT NOT NULL DEFAULT 0, -- 0：待確認、1：已確認、2：已取消、3：未出席、4：已完成
    has_gov_subsidy BOOLEAN NOT NULL DEFAULT 0, -- 0：非政府補助、1：政府補助
    psych_fee INT NOT NULL,
    visit_purpose ENUM(
        'emotion',
        'stress',
        'relationship',
        'family',
        'career',
        'academic',
        'self_growth',
        'sleep',
        'anxiety',
        'other'
	) NOT NULL,
    visit_purpose_note VARCHAR(200) NULL,
    session_type TINYINT NOT NULL, -- 0：單人諮商、1：伴侶諮商、2：家庭諮商、3：團體諮商
    psych_note VARCHAR(10000) NULL,
    rating TINYINT UNSIGNED NULL,
    review_content VARCHAR(200) NULL,
    reviewed_at DATETIME NULL,
    
    PRIMARY KEY (order_id),
    
    CONSTRAINT fk_consultation_orders_member
	FOREIGN KEY (member_id) REFERENCES member(member_id),
    
 	CONSTRAINT fk_consultation_orders_time
 	FOREIGN KEY (time_id) REFERENCES consultation_slots(timeslot_id),
   
	CONSTRAINT fk_consultation_orders_psychologist
 	FOREIGN KEY (psych_id) REFERENCES psychologist(psych_id)
);

INSERT INTO consultation_orders (
  order_id, time_id, cons_start, cons_end, member_id, psych_id, created_at, psych_loc,
  order_status, has_gov_subsidy, psych_fee, visit_purpose, visit_purpose_note,
  session_type, psych_note, rating, review_content, reviewed_at
) VALUES

(1, 3, '2026-07-25 10:00:00', '2026-07-25 11:00:00', 1, 1, '2026-07-18 09:00:00', '桃園市中壢區中山路52號',
 0, 0, 2000, 'anxiety', '最近很容易焦慮、心悸', 0, NULL, NULL, NULL, NULL),

(2, 2, '2026-07-21 15:00:00', '2026-07-21 16:00:00', 1, 1, '2026-07-12 10:00:00', '桃園市中壢區中山路52號',
 1, 0, 2000, 'stress', '工作壓力很大', 0, NULL, NULL, NULL, NULL),

(3, 7, '2026-07-24 14:00:00', '2026-07-24 15:00:00', 2, 2, '2026-07-19 15:00:00', '新北市板橋區文化路一段188號',
 1, 0, 1800, 'sleep', '長期失眠困擾', 0, NULL, NULL, NULL, NULL),

(4, 1, '2026-07-13 11:00:00', '2026-07-13 12:00:00', 2, 1, '2026-07-08 09:00:00', '桃園市中壢區中山路52號',
 2, 0, 2000, 'relationship', '人際相處常有摩擦', 0, NULL, NULL, NULL, NULL),

(5, 6, '2026-07-16 16:00:00', '2026-07-16 17:00:00', 1, 2, '2026-07-09 16:00:00', '新北市板橋區文化路一段188號',
 3, 0, 1800, 'sleep', '睡眠品質差', 0, NULL, NULL, NULL, NULL),

(6, 5, '2026-07-10 15:00:00', '2026-07-10 16:00:00', 2, 2, '2026-07-03 10:00:00', '新北市板橋區文化路一段188號',
 4, 0, 1800, 'emotion', '情緒低落想找人談談', 0,
 '個案情緒逐漸穩定，建議持續記錄情緒並保持規律作息', 5,
 '心理師很專業又溫暖，收穫很多，謝謝！', '2026-07-11 20:00:00'),

(7, 4, '2026-07-28 10:00:00', '2026-07-28 11:00:00', 2, 1, '2026-07-19 21:00:00', '桃園市中壢區中山路52號',
 0, 0, 2000, 'stress', '工作壓力大想找人聊聊', 0, NULL, NULL, NULL, NULL);


-- ==========================================
-- 諮商問題回報(consultation_reports)
-- ==========================================
CREATE TABLE consultation_reports (
report_id INT NOT NULL AUTO_INCREMENT,
member_id INT NOT NULL,
order_id INT NOT NULL,
admin_id INT NULL,
issue_desc VARCHAR(200) NOT NULL,
report_date DATETIME NOT NULL,
report_status TINYINT NOT NULL DEFAULT 0,
report_note VARCHAR(200) NULL,

PRIMARY KEY (report_id),

   CONSTRAINT fk_consultation_reports_member
	FOREIGN KEY (member_id) REFERENCES member(member_id),
    
CONSTRAINT fk_consultation_reports_order  
	FOREIGN KEY (order_id) REFERENCES consultation_orders(order_id),
    
CONSTRAINT fk_consultation_reports_admin
	FOREIGN KEY (admin_id) REFERENCES admin(admin_id)

);

INSERT INTO consultation_reports (
  member_id, order_id, admin_id, issue_desc, report_date, report_status, report_note
) VALUES
(1, 1, NULL, '預約後遲遲未收到心理師確認，想詢問處理進度', '2026-07-19 09:30:00', 0, NULL),
(2, 3, 2,    '想再次確認諮商當天的地點與報到方式',       '2026-07-19 20:00:00', 1, '已協助聯繫心理師確認，將盡快回覆會員'),
(1, 5, 2,    '被標記為未出席，但我當天有準時到場，希望協助查證', '2026-07-17 10:00:00', 2, '已調閱時段紀錄與心理師確認，將更正出席狀態'),
(2, 6, NULL, '想索取這次諮商的付款收據',                 '2026-07-11 14:00:00', 0, NULL);

-- ==========================================
-- ==========================================
-- {課程商城}
-- ==========================================

-- ============================================================
-- 第一部分：建立資料表
-- ============================================================

-- ==========================================
-- 課程分類編號(course_categories)
-- ==========================================
CREATE TABLE course_categories (
  course_cat_id INT NOT NULL AUTO_INCREMENT,
  course_cat_name VARCHAR(50) NOT NULL,
  PRIMARY KEY (course_cat_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 課程(courses)
-- ==========================================
CREATE TABLE courses (
  course_id INT NOT NULL AUTO_INCREMENT,
  course_name VARCHAR(50) NOT NULL,
  psych_id INT NOT NULL,
  admin_id INT DEFAULT NULL,
  course_cat_id INT NOT NULL,
  video_src VARCHAR(100) NOT NULL,
  video_src_pre VARCHAR(100) NOT NULL,
  outline VARCHAR(500) NOT NULL,
  listed_at DATETIME DEFAULT NULL,
  delisted_at DATETIME DEFAULT NULL,
  delist_reason ENUM('法規變更', '授權期滿', '講師要求', '突發爭議', '技術故障', '品質投訴過多') DEFAULT NULL,
  course_status TINYINT NOT NULL DEFAULT 0 COMMENT '課程狀態 (0：草稿, 1：待審核, 2：審核成功, 3：審核失敗, 4：已上架, 5：已下架)',
  save_count INT UNSIGNED NOT NULL DEFAULT 0,
  star_count INT UNSIGNED NOT NULL DEFAULT 0,
  review_count INT UNSIGNED NOT NULL DEFAULT 0,
  comment_count INT UNSIGNED NOT NULL DEFAULT 0,
  psych_discount DECIMAL(3,2) DEFAULT NULL,
  discount_start DATETIME DEFAULT NULL,
  discount_end DATETIME DEFAULT NULL,
  price INT UNSIGNED NOT NULL,
  -- 設定主鍵
  PRIMARY KEY (course_id),
  -- 設定外來鍵約束
 FOREIGN KEY (psych_id) REFERENCES psychologist (psych_id),
 FOREIGN KEY (course_cat_id) REFERENCES course_categories (course_cat_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 收藏課程(course_bookmarks)
-- ==========================================
CREATE TABLE course_bookmarks (
  course_id INT NOT NULL,
  member_id INT NOT NULL,
  saved_at DATETIME NOT NULL,
  PRIMARY KEY (course_id, member_id),
  FOREIGN KEY (course_id) REFERENCES courses (course_id) ,
  FOREIGN KEY (member_id) REFERENCES member (member_id) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 優惠券(coupons)
-- ==========================================
CREATE TABLE coupons (
  coupon_id INT NOT NULL AUTO_INCREMENT,
  coupon_name VARCHAR(50) NOT NULL,
  discount_duration INT UNSIGNED NOT NULL COMMENT '優惠效期 (天數)',
  trigger_threshold INT UNSIGNED DEFAULT NULL COMMENT '消費觸發門檻 (未填代表不限門檻)',
  discount DECIMAL(3,2) NOT NULL COMMENT '優惠券折扣 (例如：0.85 代表 85 折)',
  discount_limit INT UNSIGNED DEFAULT NULL COMMENT '折扣上限 (未填代表不設上限)',
  PRIMARY KEY (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 會員優惠券(member_coupons)
-- ==========================================
CREATE TABLE member_coupons (
  coupon_serial_no INT NOT NULL AUTO_INCREMENT,
  coupon_id INT NOT NULL,
  member_id INT NOT NULL,
  coupon_status TINYINT DEFAULT NULL COMMENT '使用狀態(0：未使用，1：已使用)',
  coupon_start_at DATETIME NOT NULL,
  coupon_end_at DATETIME NOT NULL,
  PRIMARY KEY (coupon_serial_no),
  FOREIGN KEY (coupon_id) REFERENCES coupons (coupon_id),
  FOREIGN KEY (member_id) REFERENCES member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 購物車(shopping_carts)
-- ==========================================
CREATE TABLE shopping_carts (
  member_id INT NOT NULL,
  course_id INT NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (member_id, course_id),
  FOREIGN KEY (member_id) REFERENCES member (member_id) ,
  FOREIGN KEY (course_id) REFERENCES courses (course_id) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 課程訂單(course_orders)
-- ==========================================
CREATE TABLE course_orders (
    course_order_id INT NOT NULL AUTO_INCREMENT,
    member_id INT NOT NULL,
    coupon_serial_no INT DEFAULT NULL,
    order_total INT NOT NULL,
    discount_amount INT DEFAULT NULL,
    net_amount INT DEFAULT NULL,
    payment_method TINYINT NOT NULL,
    payment_status TINYINT NOT NULL DEFAULT 0,
    ordered_at DATETIME NOT NULL,
    PRIMARY KEY (course_order_id),
    FOREIGN KEY (member_id) REFERENCES member(member_id),
    FOREIGN KEY (coupon_serial_no) REFERENCES member_coupons(coupon_serial_no)
);

-- ==========================================
-- 訂單明細(order_details)
-- ==========================================
CREATE TABLE order_details (
  course_order_id INT NOT NULL,
  course_id INT NOT NULL,
  price INT NOT NULL COMMENT '課程定價(來自課程表格)',
  discounted_price INT NOT NULL COMMENT '折扣後價格 (price * psych_discount)',
  course_permission TINYINT NOT NULL DEFAULT 0 COMMENT '課程權限 (0：可觀看預覽影片, 1：解鎖課程相關權限)',
  rating TINYINT DEFAULT NULL,
  review_content VARCHAR(200) DEFAULT NULL,
  reviewed_at DATETIME DEFAULT NULL,
  course_progress DECIMAL(5,2) DEFAULT 0.00 COMMENT '課程進度 (100.00 = 100%)',
  playback_position TIME DEFAULT '00:00:00',
  PRIMARY KEY (course_order_id, course_id),
  FOREIGN KEY (course_order_id) REFERENCES course_orders (course_order_id),
  FOREIGN KEY (course_id) REFERENCES courses (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 課程提問(course_qa_comments)
-- ==========================================
CREATE TABLE course_qa_comments (
    question_id INT NOT NULL AUTO_INCREMENT,
    course_id INT NOT NULL,
    member_id INT NOT NULL,
    asked_at DATETIME NOT NULL,
    course_question VARCHAR(500) NOT NULL,
    answered_at DATETIME DEFAULT NULL,
    course_answer VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (question_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (member_id) REFERENCES member(member_id)
);

-- ==========================================
-- 退款申請(refunds)
-- ==========================================
CREATE TABLE refunds (
    course_order_id INT NOT NULL,
    member_id INT NOT NULL,
    admin_id INT DEFAULT NULL,
    refund_reason VARCHAR(500) DEFAULT NULL,
    refund_amount INT DEFAULT NULL,
    created_at DATETIME NOT NULL,
    refunded_at DATETIME DEFAULT NULL,
    refund_status TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (course_order_id, member_id),
    FOREIGN KEY (course_order_id) REFERENCES course_orders(course_order_id),
    FOREIGN KEY (member_id) REFERENCES member(member_id),
    FOREIGN KEY (admin_id) REFERENCES admin(admin_id)
);

-- ==========================================
-- 撥款紀錄(payouts)
-- ==========================================
CREATE TABLE payouts (
    payout_id INT NOT NULL AUTO_INCREMENT,
    billing_month VARCHAR(7) NOT NULL,
    psych_id INT NOT NULL,
    admin_id INT DEFAULT NULL,
    gross_payout_amount INT NOT NULL,
    platform_commission INT NOT NULL,
    billing_offset INT NOT NULL,
    net_payout_amount INT NOT NULL,
    paid_at DATETIME DEFAULT NULL,
    payout_status TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (payout_id),
    FOREIGN KEY (psych_id) REFERENCES psychologist(psych_id),
    FOREIGN KEY (admin_id) REFERENCES admin(admin_id)
);

-- ============================================================
-- 第二部分：新增假資料
-- ============================================================

-- ==========================================
-- 課程分類編號(course_categories)
-- ==========================================
insert into course_categories(course_cat_name)
values
('自我探索與成長'),
('職場壓力與焦慮'),
('親密關係與溝通'),
('大腦科學與心理學'),
('原生家庭與愛的分際'),
('改善焦慮與自己和解');

-- 下列三張關聯表依需求僅建立表格，不建立假資料：
-- member_coupons、course_bookmarks、shopping_carts。

-- ==========================================
-- 課程(courses)
-- ==========================================
INSERT INTO courses (
  course_id,
  course_name,
  psych_id,
  admin_id,
  course_cat_id,
  video_src,
  video_src_pre,
  outline,
  listed_at,
  delisted_at,
  delist_reason,
  course_status,
  save_count,
  star_count,
  review_count,
  comment_count,
  psych_discount,
  discount_start,
  discount_end,
  price
) VALUES
(1, '認識自己：價值觀與人生方向', 1, 1, 1, '/uploads/videos/video.mp4', '/uploads/videos/video1.mp4', '透過價值觀盤點、自我敘事與行動練習，辨識真正重視的方向，建立可持續的人生選擇。', '2026-01-05 03:00:00', NULL, NULL, 4, 180, 10, 2, 3, 0.90, '2026-07-01 00:00:00', '2026-08-31 23:59:59', 1200),
(2, '建立穩定自信的日常練習', 2, 3, 1, '/uploads/videos/video.mp4', '/uploads/videos/video2.mp4', '從優勢探索、內在對話與小步行動開始，逐步建立不依賴外界評價的穩定自信。', '2026-01-12 03:00:00', NULL, NULL, 4, 160, 9, 2, 0, NULL, NULL, NULL, 980),
(3, '從完美主義走向自我接納', 3, 1, 1, '/uploads/videos/video.mp4', '/uploads/videos/video3.mp4', '理解完美主義背後的焦慮與期待，練習用更有彈性的標準回應自己。', '2026-01-19 03:00:00', NULL, NULL, 4, 145, 9, 2, 0, NULL, NULL, NULL, 1100),
(4, '職場壓力調節與情緒復原', 4, 3, 2, '/uploads/videos/video.mp4', '/uploads/videos/video1.mp4', '辨識工作壓力來源，搭配呼吸、覺察與復原計畫，降低長期累積的身心負荷。', '2026-02-02 03:00:00', NULL, NULL, 4, 210, 10, 2, 2, 0.85, '2026-07-01 00:00:00', '2026-08-31 23:59:59', 1280),
(5, '告別工作焦慮：下班後的心理界線', 5, 1, 2, '/uploads/videos/video.mp4', '/uploads/videos/video2.mp4', '練習區分工作責任與個人生活，建立下班儀式與可執行的心理界線。', '2026-02-09 03:00:00', NULL, NULL, 4, 175, 9, 2, 0, NULL, NULL, NULL, 1080),
(6, '高壓溝通與衝突管理', 6, 3, 2, '/uploads/videos/video.mp4', '/uploads/videos/video3.mp4', '在高壓職場情境中練習清楚表達、主動傾聽與衝突降溫，提升合作品質。', '2026-02-16 03:00:00', NULL, NULL, 4, 155, 8, 2, 0, NULL, NULL, NULL, 1350),
(7, '親密關係中的安全感練習', 7, 1, 3, '/uploads/videos/video.mp4', '/uploads/videos/video1.mp4', '理解依附需求與不安全感的來源，練習在關係中提出需求並建立信任。', '2026-03-02 03:00:00', NULL, NULL, 4, 230, 10, 2, 2, 0.90, '2026-07-01 00:00:00', '2026-08-31 23:59:59', 1450),
(8, '非暴力溝通：說出需要也聽見彼此', 8, 3, 3, '/uploads/videos/video.mp4', '/uploads/videos/video2.mp4', '運用觀察、感受、需要與請求四個步驟，改善指責與防衛的溝通循環。', '2026-03-09 03:00:00', NULL, NULL, 4, 195, 9, 2, 0, NULL, NULL, NULL, 1250),
(9, '關係修復與健康界線', 9, 1, 3, '/uploads/videos/video.mp4', '/uploads/videos/video3.mp4', '辨識關係中的受傷模式，學習修復對話與界線設定，讓彼此保有安全空間。', '2026-03-16 03:00:00', NULL, NULL, 4, 170, 9, 2, 0, NULL, NULL, NULL, 1380),
(10, '大腦如何形成習慣', 10, 3, 4, '/uploads/videos/video.mp4', '/uploads/videos/video1.mp4', '從神經可塑性與獎賞迴路理解習慣形成，設計容易持續的行為改變方案。', '2026-04-06 03:00:00', NULL, NULL, 4, 220, 10, 2, 2, 0.85, '2026-07-01 00:00:00', '2026-08-31 23:59:59', 1180),
(11, '睡眠、情緒與大腦修復', 11, 1, 4, '/uploads/videos/video.mp4', '/uploads/videos/video2.mp4', '認識睡眠週期對情緒與記憶的影響，建立有助大腦恢復的睡前習慣。', '2026-04-13 03:00:00', NULL, NULL, 4, 185, 9, 2, 0, NULL, NULL, NULL, 1050),
(12, '注意力與拖延的心理科學', 12, 3, 4, '/uploads/videos/video.mp4', '/uploads/videos/video3.mp4', '理解注意力限制、即時獎賞與拖延機制，運用環境設計提升行動力。', '2026-04-20 03:00:00', NULL, NULL, 4, 165, 8, 2, 0, NULL, NULL, NULL, 990),
(13, '看見原生家庭留下的關係腳本', 13, 1, 5, '/uploads/videos/video.mp4', '/uploads/videos/video1.mp4', '辨識家庭互動中形成的信念與角色，理解它們如何影響現在的人際選擇。', '2026-05-04 03:00:00', NULL, NULL, 4, 205, 10, 2, 2, 0.90, '2026-07-01 00:00:00', '2026-08-31 23:59:59', 1320),
(14, '愛與界線：停止過度承擔', 14, 3, 5, '/uploads/videos/video.mp4', '/uploads/videos/video2.mp4', '看見家庭中的過度負責模式，練習在關心他人時也保留自己的需求與界線。', '2026-05-11 03:00:00', NULL, NULL, 4, 180, 9, 2, 0, NULL, NULL, NULL, 1150),
(15, '從家庭角色找回真實自己', 15, 1, 5, '/uploads/videos/video.mp4', '/uploads/videos/video3.mp4', '探索成長過程中的家庭角色，鬆動討好與壓抑，重新建立真實的自我認同。', '2026-05-18 03:00:00', NULL, NULL, 4, 150, 9, 2, 0, NULL, NULL, NULL, 1290),
(16, '與焦慮共處：辨識身心警訊', 1, 3, 6, '/uploads/videos/video.mp4', '/uploads/videos/video1.mp4', '辨識焦慮出現時的身體與思考訊號，建立能在日常使用的安定步驟。', '2026-06-01 03:00:00', NULL, NULL, 4, 240, 10, 2, 3, 0.85, '2026-07-01 00:00:00', '2026-08-31 23:59:59', 1090),
(17, '停止災難化思考的認知練習', 2, 1, 6, '/uploads/videos/video.mp4', '/uploads/videos/video2.mp4', '找出災難化思考與過度預測，運用證據檢核建立更平衡且可行的觀點。', '2026-06-08 03:00:00', NULL, NULL, 4, 200, 9, 2, 0, NULL, NULL, NULL, 1190),
(18, '自我安定：從呼吸到內在對話', 3, 3, 6, '/uploads/videos/video.mp4', '/uploads/videos/video3.mp4', '結合呼吸調節、身體掃描與支持性內在對話，逐步降低焦慮帶來的失控感。', '2026-06-15 03:00:00', NULL, NULL, 4, 190, 8, 2, 0, NULL, NULL, NULL, 890);

-- ==========================================
-- 優惠券(coupons)
-- ==========================================
INSERT INTO coupons (
  coupon_id,
  coupon_name,
  discount_duration,
  trigger_threshold,
  discount,
  discount_limit
) VALUES
(1, '新會員體驗券', 30, NULL, 0.90, 300),
(2, '千元課程九折券', 45, 1000, 0.90, 400),
(3, '自我成長八五折券', 30, 1200, 0.85, 500),
(4, '關係課程九折券', 60, 1000, 0.90, 350),
(5, '心理學探索八折券', 14, 1500, 0.80, 600),
(6, '焦慮照顧九五折券', 90, NULL, 0.95, 200);

-- ==========================================
-- 課程訂單(course_orders)
-- ==========================================
INSERT INTO course_orders (
  course_order_id,
  member_id,
  coupon_serial_no,
  order_total,
  discount_amount,
  net_amount,
  payment_method,
  payment_status,
  ordered_at
) VALUES
(1, 1, NULL, 1200, 0, 1200, 1, 1, '2026-04-02 10:00:00'),
(2, 2, NULL, 1200, 0, 1200, 0, 1, '2026-04-03 15:30:00'),
(3, 2, NULL, 980, 0, 980, 1, 1, '2026-04-05 10:00:00'),
(4, 3, NULL, 980, 0, 980, 0, 1, '2026-04-06 15:30:00'),
(5, 3, NULL, 1100, 0, 1100, 1, 1, '2026-04-08 10:00:00'),
(6, 4, NULL, 1100, 0, 1100, 0, 1, '2026-04-09 15:30:00'),
(7, 4, NULL, 1280, 0, 1280, 1, 1, '2026-04-11 10:00:00'),
(8, 5, NULL, 1280, 0, 1280, 0, 1, '2026-04-12 15:30:00'),
(9, 5, NULL, 1080, 0, 1080, 1, 1, '2026-04-14 10:00:00'),
(10, 6, NULL, 1080, 0, 1080, 0, 1, '2026-04-15 15:30:00'),
(11, 6, NULL, 1350, 0, 1350, 1, 1, '2026-04-17 10:00:00'),
(12, 7, NULL, 1350, 0, 1350, 0, 1, '2026-04-18 15:30:00'),
(13, 7, NULL, 1450, 0, 1450, 1, 1, '2026-05-02 10:00:00'),
(14, 8, NULL, 1450, 0, 1450, 0, 1, '2026-05-03 15:30:00'),
(15, 8, NULL, 1250, 0, 1250, 1, 1, '2026-05-05 10:00:00'),
(16, 1, NULL, 1250, 0, 1250, 0, 1, '2026-05-06 15:30:00'),
(17, 1, NULL, 1380, 0, 1380, 1, 1, '2026-05-08 10:00:00'),
(18, 2, NULL, 1380, 0, 1380, 0, 1, '2026-05-09 15:30:00'),
(19, 2, NULL, 1180, 0, 1180, 1, 1, '2026-05-11 10:00:00'),
(20, 3, NULL, 1180, 0, 1180, 0, 1, '2026-05-12 15:30:00'),
(21, 3, NULL, 1050, 0, 1050, 1, 1, '2026-05-14 10:00:00'),
(22, 4, NULL, 1050, 0, 1050, 0, 1, '2026-05-15 15:30:00'),
(23, 4, NULL, 990, 0, 990, 1, 1, '2026-05-17 10:00:00'),
(24, 5, NULL, 990, 0, 990, 0, 1, '2026-05-18 15:30:00'),
(25, 5, NULL, 1320, 0, 1320, 1, 1, '2026-06-02 10:00:00'),
(26, 6, NULL, 1320, 0, 1320, 0, 1, '2026-06-03 15:30:00'),
(27, 6, NULL, 1150, 0, 1150, 1, 1, '2026-06-05 10:00:00'),
(28, 7, NULL, 1150, 0, 1150, 0, 1, '2026-06-06 15:30:00'),
(29, 7, NULL, 1290, 0, 1290, 1, 1, '2026-06-08 10:00:00'),
(30, 8, NULL, 1290, 0, 1290, 0, 1, '2026-06-09 15:30:00'),
(31, 8, NULL, 1090, 0, 1090, 1, 1, '2026-06-11 10:00:00'),
(32, 1, NULL, 1090, 0, 1090, 0, 1, '2026-06-12 15:30:00'),
(33, 1, NULL, 1190, 0, 1190, 1, 1, '2026-06-14 10:00:00'),
(34, 2, NULL, 1190, 0, 1190, 0, 1, '2026-06-15 15:30:00'),
(35, 2, NULL, 890, 0, 890, 1, 1, '2026-06-17 10:00:00'),
(36, 3, NULL, 890, 0, 890, 0, 1, '2026-06-18 15:30:00');

-- ==========================================
-- 訂單明細(order_details)
-- ==========================================
INSERT INTO order_details (
  course_order_id,
  course_id,
  price,
  discounted_price,
  course_permission,
  rating,
  review_content,
  reviewed_at,
  course_progress,
  playback_position
) VALUES
(1, 1, 1200, 1200, 1, 5, '很喜歡「認識自己：價值觀與人生方向」的練習安排，內容清楚而且能實際應用。', '2026-04-04 19:00:00', 100, '00:42:00'),
(2, 1, 1200, 1200, 1, 5, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-04-05 20:30:00', 65, '00:18:00'),
(3, 2, 980, 980, 1, 5, '很喜歡「建立穩定自信的日常練習」的練習安排，內容清楚而且能實際應用。', '2026-04-07 19:00:00', 100, '00:42:00'),
(4, 2, 980, 980, 1, 4, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-04-08 20:30:00', 65, '00:18:00'),
(5, 3, 1100, 1100, 1, 4, '很喜歡「從完美主義走向自我接納」的練習安排，內容清楚而且能實際應用。', '2026-04-10 19:00:00', 100, '00:42:00'),
(6, 3, 1100, 1100, 1, 5, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-04-11 20:30:00', 65, '00:18:00'),
(7, 4, 1280, 1280, 1, 5, '很喜歡「職場壓力調節與情緒復原」的練習安排，內容清楚而且能實際應用。', '2026-04-13 19:00:00', 100, '00:42:00'),
(8, 4, 1280, 1280, 1, 5, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-04-14 20:30:00', 65, '00:18:00'),
(9, 5, 1080, 1080, 1, 4, '很喜歡「告別工作焦慮：下班後的心理界線」的練習安排，內容清楚而且能實際應用。', '2026-04-16 19:00:00', 100, '00:42:00'),
(10, 5, 1080, 1080, 1, 5, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-04-17 20:30:00', 65, '00:18:00'),
(11, 6, 1350, 1350, 1, 4, '很喜歡「高壓溝通與衝突管理」的練習安排，內容清楚而且能實際應用。', '2026-04-19 19:00:00', 100, '00:42:00'),
(12, 6, 1350, 1350, 1, 4, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-04-20 20:30:00', 65, '00:18:00'),
(13, 7, 1450, 1450, 1, 5, '很喜歡「親密關係中的安全感練習」的練習安排，內容清楚而且能實際應用。', '2026-05-04 19:00:00', 100, '00:42:00'),
(14, 7, 1450, 1450, 1, 5, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-05-05 20:30:00', 65, '00:18:00'),
(15, 8, 1250, 1250, 1, 5, '很喜歡「非暴力溝通：說出需要也聽見彼此」的練習安排，內容清楚而且能實際應用。', '2026-05-07 19:00:00', 100, '00:42:00'),
(16, 8, 1250, 1250, 1, 4, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-05-08 20:30:00', 65, '00:18:00'),
(17, 9, 1380, 1380, 1, 4, '很喜歡「關係修復與健康界線」的練習安排，內容清楚而且能實際應用。', '2026-05-10 19:00:00', 100, '00:42:00'),
(18, 9, 1380, 1380, 1, 5, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-05-11 20:30:00', 65, '00:18:00'),
(19, 10, 1180, 1180, 1, 5, '很喜歡「大腦如何形成習慣」的練習安排，內容清楚而且能實際應用。', '2026-05-13 19:00:00', 100, '00:42:00'),
(20, 10, 1180, 1180, 1, 5, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-05-14 20:30:00', 65, '00:18:00'),
(21, 11, 1050, 1050, 1, 4, '很喜歡「睡眠、情緒與大腦修復」的練習安排，內容清楚而且能實際應用。', '2026-05-16 19:00:00', 100, '00:42:00'),
(22, 11, 1050, 1050, 1, 5, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-05-17 20:30:00', 65, '00:18:00'),
(23, 12, 990, 990, 1, 4, '很喜歡「注意力與拖延的心理科學」的練習安排，內容清楚而且能實際應用。', '2026-05-19 19:00:00', 100, '00:42:00'),
(24, 12, 990, 990, 1, 4, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-05-20 20:30:00', 65, '00:18:00'),
(25, 13, 1320, 1320, 1, 5, '很喜歡「看見原生家庭留下的關係腳本」的練習安排，內容清楚而且能實際應用。', '2026-06-04 19:00:00', 100, '00:42:00'),
(26, 13, 1320, 1320, 1, 5, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-06-05 20:30:00', 65, '00:18:00'),
(27, 14, 1150, 1150, 1, 5, '很喜歡「愛與界線：停止過度承擔」的練習安排，內容清楚而且能實際應用。', '2026-06-07 19:00:00', 100, '00:42:00'),
(28, 14, 1150, 1150, 1, 4, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-06-08 20:30:00', 65, '00:18:00'),
(29, 15, 1290, 1290, 1, 4, '很喜歡「從家庭角色找回真實自己」的練習安排，內容清楚而且能實際應用。', '2026-06-10 19:00:00', 100, '00:42:00'),
(30, 15, 1290, 1290, 1, 5, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-06-11 20:30:00', 65, '00:18:00'),
(31, 16, 1090, 1090, 1, 5, '很喜歡「與焦慮共處：辨識身心警訊」的練習安排，內容清楚而且能實際應用。', '2026-06-13 19:00:00', 100, '00:42:00'),
(32, 16, 1090, 1090, 1, 5, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-06-14 20:30:00', 65, '00:18:00'),
(33, 17, 1190, 1190, 1, 4, '很喜歡「停止災難化思考的認知練習」的練習安排，內容清楚而且能實際應用。', '2026-06-16 19:00:00', 100, '00:42:00'),
(34, 17, 1190, 1190, 1, 5, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-06-17 20:30:00', 65, '00:18:00'),
(35, 18, 890, 890, 1, 4, '很喜歡「自我安定：從呼吸到內在對話」的練習安排，內容清楚而且能實際應用。', '2026-06-19 19:00:00', 100, '00:42:00'),
(36, 18, 890, 890, 1, 4, '講解節奏穩定，讓我更能理解自己的狀態並持續練習。', '2026-06-20 20:30:00', 65, '00:18:00');

-- ==========================================
-- 課程提問(course_qa_comments)
-- ==========================================
INSERT INTO course_qa_comments (
  question_id,
  course_id,
  member_id,
  asked_at,
  course_question,
  answered_at,
  course_answer
) VALUES
(1, 1, 1, '2026-07-01 11:00:00', '完成價值觀排序後，如何把結果轉成每週可以執行的目標？', '2026-07-01 18:00:00', '可以先選一個最重要的價值，設計一個十五分鐘內能完成的小行動。'),
(2, 1, 2, '2026-07-02 11:00:00', '如果選出的價值彼此衝突，應該先從哪一個開始？', '2026-07-02 18:00:00', '先評估當下生活階段最需要被照顧的面向，再保留之後調整的空間。'),
(3, 1, 3, '2026-07-03 11:00:00', '課程中的書寫練習需要每天進行嗎？', NULL, NULL),
(4, 4, 4, '2026-07-04 11:00:00', '遇到臨時加班時，放鬆練習可以怎麼調整？', '2026-07-04 18:00:00', '可以縮短成三分鐘呼吸與肩頸覺察，重點是保留轉換狀態的動作。'),
(5, 4, 5, '2026-07-05 11:00:00', '如何判斷自己的壓力已經需要專業協助？', NULL, NULL),
(6, 7, 7, '2026-07-06 11:00:00', '伴侶不願意談感受時，我可以先做什麼？', '2026-07-06 18:00:00', '先描述可觀察到的情況與自己的感受，避免要求對方立刻給出答案。'),
(7, 7, 8, '2026-07-07 11:00:00', '安全感練習可以自己先做嗎？', NULL, NULL),
(8, 10, 2, '2026-07-08 11:00:00', '建立新習慣通常需要多久才會穩定？', '2026-07-08 18:00:00', '時間因人而異，先用固定提示與足夠小的行動提高重複成功率。'),
(9, 10, 3, '2026-07-09 11:00:00', '中斷幾天後應該重新開始計算嗎？', NULL, NULL),
(10, 13, 5, '2026-07-10 11:00:00', '如何分辨現在的反應是否來自原生家庭？', '2026-07-10 18:00:00', '可以觀察反應強度是否超過當下事件，並記錄熟悉的家庭語句與角色。'),
(11, 13, 6, '2026-07-11 11:00:00', '理解家庭腳本後一定要和家人談嗎？', NULL, NULL),
(12, 16, 8, '2026-07-12 11:00:00', '焦慮時胸悶是否適合直接做呼吸練習？', '2026-07-12 18:00:00', '先確認身體安全，再用自然且較慢的吐氣練習，不必強迫深呼吸。'),
(13, 16, 1, '2026-07-13 11:00:00', '身心警訊紀錄需要寫得多詳細？', '2026-07-13 18:00:00', '記下情境、身體感受、想法與採取的行動即可，重點是找出重複模式。'),
(14, 16, 2, '2026-07-14 11:00:00', '可以把課程練習和諮商一起使用嗎？', NULL, NULL);

-- ==========================================
-- 退款申請(refunds)
-- ==========================================
INSERT INTO refunds (
  course_order_id,
  member_id,
  admin_id,
  refund_reason,
  refund_amount,
  created_at,
  refunded_at,
  refund_status
) VALUES
(2, 2, 1, '課程內容與目前需求不符', 1200, '2026-04-06 09:30:00', '2026-04-08 14:00:00', 3),
(8, 5, 3, '近期無法安排學習時間', 1280, '2026-04-15 10:00:00', NULL, 0),
(14, 8, 1, '重複購買相同主題課程', 1450, '2026-05-07 16:00:00', NULL, 1),
(20, 3, 3, '影片播放環境不符合需求', 1180, '2026-05-15 13:00:00', '2026-05-17 11:00:00', 3),
(26, 6, 1, '購買後發現選錯課程', 1320, '2026-06-07 09:00:00', NULL, 0),
(32, 1, 3, '希望改選其他焦慮主題課程', 1090, '2026-06-13 15:00:00', NULL, 1);

-- ==========================================
-- 撥款紀錄(payouts)
-- ==========================================
INSERT INTO payouts (
  payout_id,
  billing_month,
  psych_id,
  admin_id,
  gross_payout_amount,
  platform_commission,
  billing_offset,
  net_payout_amount,
  paid_at,
  payout_status
) VALUES
(1, '2026-04', 1, 3, 11000, 1100, 0, 9900, '2026-05-05 09:00:00', 1),
(2, '2026-04', 2, 1, 12000, 1200, 0, 10800, '2026-05-05 09:00:00', 1),
(3, '2026-04', 3, 3, 13000, 1300, 0, 11700, '2026-05-05 09:00:00', 1),
(4, '2026-04', 4, 1, 14000, 1400, 0, 12600, '2026-05-05 09:00:00', 1),
(5, '2026-04', 5, 3, 15000, 1500, 0, 13500, '2026-05-05 09:00:00', 1),
(6, '2026-04', 6, 1, 16000, 1600, 0, 14400, '2026-05-05 09:00:00', 1),
(7, '2026-04', 7, 3, 17000, 1700, 0, 15300, '2026-05-05 09:00:00', 1),
(8, '2026-04', 8, 1, 18000, 1800, 0, 16200, '2026-05-05 09:00:00', 1),
(9, '2026-04', 9, 3, 19000, 1900, 0, 17100, '2026-05-05 09:00:00', 1),
(10, '2026-04', 10, 1, 20000, 2000, 0, 18000, '2026-05-05 09:00:00', 1),
(11, '2026-04', 11, 3, 21000, 2100, 0, 18900, '2026-05-05 09:00:00', 1),
(12, '2026-04', 12, 1, 22000, 2200, 0, 19800, '2026-05-05 09:00:00', 1),
(13, '2026-04', 13, 3, 23000, 2300, 0, 20700, '2026-05-05 09:00:00', 1),
(14, '2026-04', 14, 1, 24000, 2400, 0, 21600, '2026-05-05 09:00:00', 1),
(15, '2026-04', 15, 3, 25000, 2500, 0, 22500, '2026-05-05 09:00:00', 1),
(16, '2026-05', 1, 1, 13000, 1300, 0, 11700, '2026-06-05 09:00:00', 1),
(17, '2026-05', 2, 3, 14000, 1400, 0, 12600, '2026-06-05 09:00:00', 1),
(18, '2026-05', 3, 1, 15000, 1500, 0, 13500, '2026-06-05 09:00:00', 1),
(19, '2026-05', 4, 3, 16000, 1600, 0, 14400, '2026-06-05 09:00:00', 1),
(20, '2026-05', 5, 1, 17000, 1700, 0, 15300, '2026-06-05 09:00:00', 1),
(21, '2026-05', 6, 3, 18000, 1800, 0, 16200, '2026-06-05 09:00:00', 1),
(22, '2026-05', 7, 1, 19000, 1900, 0, 17100, '2026-06-05 09:00:00', 1),
(23, '2026-05', 8, 3, 20000, 2000, 0, 18000, '2026-06-05 09:00:00', 1),
(24, '2026-05', 9, 1, 21000, 2100, 0, 18900, '2026-06-05 09:00:00', 1),
(25, '2026-05', 10, 3, 22000, 2200, 0, 19800, '2026-06-05 09:00:00', 1),
(26, '2026-05', 11, 1, 23000, 2300, 0, 20700, '2026-06-05 09:00:00', 1),
(27, '2026-05', 12, 3, 24000, 2400, 0, 21600, '2026-06-05 09:00:00', 1),
(28, '2026-05', 13, 1, 25000, 2500, 0, 22500, '2026-06-05 09:00:00', 1),
(29, '2026-05', 14, 3, 26000, 2600, 0, 23400, '2026-06-05 09:00:00', 1),
(30, '2026-05', 15, 1, 27000, 2700, 0, 24300, '2026-06-05 09:00:00', 1),
(31, '2026-06', 1, 3, 15000, 1500, 0, 13500, '2026-07-05 09:00:00', 1),
(32, '2026-06', 2, 1, 16000, 1600, 0, 14400, '2026-07-05 09:00:00', 1),
(33, '2026-06', 3, 3, 17000, 1700, 0, 15300, '2026-07-05 09:00:00', 1),
(34, '2026-06', 4, 1, 18000, 1800, 0, 16200, '2026-07-05 09:00:00', 1),
(35, '2026-06', 5, 3, 19000, 1900, 0, 17100, '2026-07-05 09:00:00', 1),
(36, '2026-06', 6, 1, 20000, 2000, 0, 18000, '2026-07-05 09:00:00', 1),
(37, '2026-06', 7, 3, 21000, 2100, 0, 18900, '2026-07-05 09:00:00', 1),
(38, '2026-06', 8, 1, 22000, 2200, 0, 19800, '2026-07-05 09:00:00', 1),
(39, '2026-06', 9, 3, 23000, 2300, 0, 20700, '2026-07-05 09:00:00', 1),
(40, '2026-06', 10, 1, 24000, 2400, 0, 21600, '2026-07-05 09:00:00', 1),
(41, '2026-06', 11, 3, 25000, 2500, 0, 22500, '2026-07-05 09:00:00', 1),
(42, '2026-06', 12, 1, 26000, 2600, 0, 23400, '2026-07-05 09:00:00', 1),
(43, '2026-06', 13, 3, 27000, 2700, 0, 24300, '2026-07-05 09:00:00', 1),
(44, '2026-06', 14, 1, 28000, 2800, 0, 25200, '2026-07-05 09:00:00', 1),
(45, '2026-06', 15, 3, 29000, 2900, 0, 26100, '2026-07-05 09:00:00', 1);