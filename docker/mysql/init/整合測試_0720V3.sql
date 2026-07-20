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
(8, 'user08', '8888', 2, '上上上上上億', '女', '0945678901', '2000-02-29', '新北市', '板橋區', '縣民大道二段7號', '2025-12-01 09:00:00', NULL, NULL, NULL, '1000000million@example.com', NULL);



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
  profile_pic           LONGBLOB    DEFAULT NULL,
  bank_account          VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (psych_id)
);


INSERT INTO psychologist 
  (psych_account, psych_password, account_status, name, gender, phone_number, email, 
   psych_certificate, has_practice_license, psych_loc, psych_fee, 
   weekly_availability, regis_at, bank_account)
VALUES 
  (
    '1', '1', 1, '陳雅婷', '女', '0912345678', 'chen@example.com', 
    'CERT-2021-00123', 1, '桃園市中壢區中山路52號', 2000, 
    '000000001111111110000000000000001111111110000000000000001111111110000000000000001111111110000000000000001111111110000000000000001111111110000000000000001111111110000000', 
    '2023-03-15 10:00:00', '12345678901234'
  ),
  (
    '2', '2', 1, '林志遠', '男', '0923456789', 'lin@example.com', 
    'CERT-2019-00456', 1, '新北市板橋區文化路一段188號', 1800, 
    '000000000000000000000000000000111111100000000111100000000111100000000111111100000000111111111000000011000000011111000000011111000000011000000011100000001111110000000', 
    '2022-11-20 09:30:00', '98765432109876'
  ),


('psych007','Pw007Aa',1,'林詩涵','女','0900000007','psych007@mindcare.com.tw','諮商心理師證書第2007號',1,'高雄市苓雅區',2000,
'000000000000011111111000000000000011111111000000000000011111111000000000000011111111000000000000011111111000000000000000000',
'2025-03-03 09:00:00','00007100001007'),

('psych008','Pw008Aa',1,'張家豪','男','0900000008','psych008@mindcare.com.tw','諮商心理師證書第2008號',1,'新北市板橋區',1600,
'111111110000111100001111111100001111000011110000111111110000111100001111000011110000111111110000111100001111000011110000',
'2025-04-04 09:00:00','00008100001008'),

('psych009','Pw009Aa',1,'李思穎','女','0900000009','psych009@mindcare.com.tw','諮商心理師證書第2009號',1,'台南市東區',2200,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111',
'2025-05-05 09:00:00','00009100001009'),

('psych010','Pw010Aa',1,'黃彥誠','男','0900000010','psych010@mindcare.com.tw','諮商心理師證書第2010號',1,'桃園市中壢區',1700,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111',
'2025-06-06 09:00:00','00010100001010'),

('psych011','Pw011Aa',1,'吳佳玲','女','0900000011','psych011@mindcare.com.tw','諮商心理師證書第2011號',1,'新竹市東區',1900,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111',
'2025-07-07 09:00:00','00011100001011'),

('psych012','Pw012Aa',1,'劉冠廷','男','0900000012','psych012@mindcare.com.tw','諮商心理師證書第2012號',1,'台北市信義區',2100,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111',
'2025-08-08 09:00:00','00012100001012'),
('psych013','Pw013Aa',1,'周美芳','女','0900000013','psych013@mindcare.com.tw','諮商心理師證書第2013號',1,'台中市北屯區',1500,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111',
'2025-09-09 09:00:00','00013100001013'),

('psych014','Pw014Aa',1,'蔡博文','男','0900000014','psych014@mindcare.com.tw','諮商心理師證書第2014號',1,'高雄市左營區',2300,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111',
'2025-10-10 09:00:00','00014100001014'),

('psych015','Pw015Aa',0,'許雅筑','女','0900000015','psych015@mindcare.com.tw','諮商心理師證書第2015號',1,'新北市新莊區',1650,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111',
'2025-11-11 09:00:00','00015100001015'),

('psych016','Pw016Aa',2,'鄭子軒','男','0900000016','psych016@mindcare.com.tw','諮商心理師證書第2016號',1,'台南市永康區',1750,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111',
'2025-12-12 09:00:00','00016100001016'),

('psych017','Pw017Aa',1,'謝欣妤','女','0900000017','psych017@mindcare.com.tw','諮商心理師證書第2017號',0,'桃園市桃園區',2000,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111',
'2026-01-13 09:00:00','00017100001017'),

('psych018','Pw018Aa',1,'楊智凱','男','0900000018','psych018@mindcare.com.tw','',1,'台北市中山區',1850,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111',
'2026-02-14 09:00:00','00018100001018'),

('psych019','Pw019Aa',1,'賴怡君','女','0900000019','psych019@mindcare.com.tw','',0,'高雄市三民區',1950,
'000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111000000000111111111',
'2026-03-15 09:00:00','00019100001019');

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
  (1, 1, '投稿的文章「如何舒緩工作壓力」審核通過，已公開發布。',0, '2024-05-30 10:00:00', 0),
  (1, 1, '您稿的文章「如何舒緩工作壓力」審核通過，已公開發布。',0, '2024-05-30 10:00:00', 0),
  (2, 2, '會員林小華已預約您 2024-06-05 14:00 的諮詢時段，請確認行事曆。',1, '2024-06-01 13:00:00', 0),
  (3, 1, '您報名的「認知行為治療進階課程」已通過審核，開課日期為 2024-07-10。',2, '2024-06-10 09:00:00', 0),
  (5, 4, '您投稿的文章「認識焦慮症」審核通過，已公開發布。',0, '2026-06-02 09:30:00', 1),
  (8, 2, '您投稿的文章「情緒壓力管理技巧」目前正在等待管理員審核。',0, '2026-07-01 13:20:00', 0),

  (6, 2, '會員已成功預約您於2026-07-20 14:00的諮詢時段，請確認行事曆。',1, '2026-06-18 10:00:00', 1),
  (9, 3, '您有新的諮商預約申請，請至心理師後台確認。',1, '2026-07-13 15:40:00', 0),

  (7, 3, '您報名的「心理師專業成長課程」已通過審核，開課日期為2026-07-25。',2, '2026-06-25 09:00:00', 1),
  (10, 5, '新的心理專業進修課程已開放報名，歡迎參加。',2, '2026-07-16 11:00:00', 0);
-- ==========================================
-- {專欄文章}
-- 文章分類(article_categories)
-- ==========================================
CREATE TABLE article_categories(
	article_cat_id INT PRIMARY KEY AUTO_INCREMENT,
    article_cat_name VARCHAR(50) NOT NULL,
    article_cat_status BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO article_categories (article_cat_name) VALUES 
('自我探索與成長'), ('職場壓力與焦慮'), ('親密關係與溝通'), ('大腦科學與心理學'); 

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
    title VARCHAR(255),
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
	like_base_count INT UNSIGNED NOT NULL DEFAULT 0,
    bookmark_base_count INT UNSIGNED NOT NULL DEFAULT 0,
    share_count INT UNSIGNED NOT NULL DEFAULT 0,
   --  INDEX idx_view_count (view_count),
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
  article_status, reject_reason, reject_note, view_count, like_base_count, bookmark_base_count, share_count
) VALUES  
(
NULL, 1, 1, 2, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/cover1.png'), '找不到原因的身體不適？解密現代人的隱形危機「自律神經失調」',
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
'2026-07-08 07:03:11', '2026-07-08 07:38:15', '2026-07-08 07:38:15', '2026-07-08 07:50:38', '2026-07-08 07:50:38', 
NULL, 2, NULL, NULL, 525, 143, 120, 70
),
(
NULL, 1, 1, 3, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/01.jpg'), '解讀夢境：頻繁夢到「從高處墜落」的潛意識訊息', 
'<p>昨晚又夢到從高處墜落嗎？這可能代表你最近的內心正承受著超乎預期的壓力與焦慮。</p><p>在心理學與解夢的領域中，「墜落」往往是最常見的夢境意象之一。當我們在夢中失去重心、不斷向下掉落時，那種強烈的無助與失控感，往往是潛意識在向你發出警訊。這可能意味著，你在現實生活中正經歷著以下幾種狀態：</p><h2>1. 面對生活變動的「失控感」</h2><p>無論是工作職位變更、人際關係的轉變，或是正處於人生重大的十字路口，當現實發展超出了你的預期，你的大腦就會在深夜透過「墜落」來反映你對未來的焦慮與不安全感。</p><h2>2. 過度緊繃的「完美主義」</h2><p>你是不是給了自己太高的期望？總是害怕自己犯錯、害怕從苦心經營的「高處」跌落？這種長期處於緊繃狀態的精神壓力，很容易在睡眠時轉化為具體的墜落夢境，提醒你該放手讓自己喘口氣了。</p><h2>3. 生理上的過度疲憊</h2><p>有時候，這也是身體發出的過勞信號。當大腦比身體更快進入深層睡眠，或者肌肉因極度疲勞而突然放鬆時，神經系統可能會產生錯覺，進而引發「入睡抽動」（Hypnic jerk），在潛意識中編織出墜落的畫面。</p><hr /><h3 style="color:#e8b4b4;">親愛的，這不是你的錯，這只是心累了。</h3><p>夢境從來不是為了嚇唬我們，而是潛意識最溫柔的提醒。如果這個夢最近頻繁出現，不妨試著在睡前撥出十分鐘，放下手機，做幾次深呼吸，對自己說一句：「今天辛苦了，現在我可以安全地放鬆了。」</p><p>如果這份無助感已經讓你感到有些難以招架，請記得，你不需要一個人硬撐。平台的專業諮商心理師隨時在這裡，願意陪伴你一起梳理那些卡住的思緒，找回內心的平穩與安全感。</p>', 
'2026-05-21 09:15:00', '2026-07-03 14:15:45', '2026-05-22 14:00:00', '2026-05-23 10:00:00', '2026-05-23 10:30:00', NULL, 
2, NULL, NULL, 252, 43, 12, 7
),
(
NULL, 2, 4, 2, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/7002.png'), '從腦科學看現代人的專注力危機：如何奪回大腦主導權', 
'近期有研究指出，長時間滑短影音會改變大腦的多巴胺分泌機制...', 
'2026-01-14 10:00:00', '2026-03-10 11:15:00', '2026-01-15 09:00:00', '2026-01-16 09:00:00', '2026-01-16 09:40:00', '2026-05-23 22:40:00', 
4, NULL, NULL, 4250, 288, 105, 185
),
(
NULL, 1, 1, 3, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/03.jpg'), '解碼你的社交面具：你在群體中扮演哪種「隱形角色」？', 
'<h1>為什麼我們總在人群中感到疲憊？</h1>
<p data-path-to-node="7">你是否也有過這種經驗：在朋友聚會中笑得最開心，回到家關上門的瞬間，卻只感受到排山倒海的空虛與疲憊？</p>
<p data-path-to-node="8">心理學研究指出，為了適應社會，我們每個人都會發展出不同的「人格面具」（Persona）。這並不是虛偽，而是一種心理防禦機制。然而，當面具戴得太久，我們往往會忘記自己原本的模樣。</p>
<p><img src="/article-images/4a0d3a18-aa96-49f9-8fee-bda0efa057ea.jpg" alt="" width="520" height="376"></p>
<h3 data-path-to-node="10">快速檢視：你是哪一種社交人格？</h3>
<p data-path-to-node="11">根據日常行為，我們大致可以將常見的社交面具分為以下三類：</p>
<ul data-path-to-node="12">
<li>
<p data-path-to-node="12,0,0"><strong data-path-to-node="12,0,0" data-index-in-node="0">「傾聽型」面具</strong>：在群體中總是扮演垃圾桶，習慣隱藏自己的情緒來迎合他人。</p>
</li>
<li>
<p data-path-to-node="12,1,0"><strong data-path-to-node="12,1,0" data-index-in-node="0">「幽默型」面具</strong>：用搞笑來掩飾內心的不安，深怕氣氛一冷下來就會被看穿寂寞。</p>
</li>
<li>
<p data-path-to-node="12,2,0"><strong data-path-to-node="12,2,0" data-index-in-node="0">「邊緣型」面具</strong>：刻意與人保持距離，用冷漠來保護自己免受人際關係的傷害。</p>
</li>
</ul>
<blockquote data-path-to-node="13">
<p data-path-to-node="13,0"><strong data-path-to-node="13,0" data-index-in-node="0">心理學小建議：</strong> 試著在每天睡前留出 10 分鐘的「無面具時間」，不滑手機、不迎合任何人，單純與自己的呼吸待在一起。允許自己不完美，才是真正自我成長的開始。</p>
</blockquote>', 
'2026-06-29 15:16:34', '2026-07-03 18:48:36', '2026-07-03 18:48:00', NULL, '2026-07-03 19:30:00', NULL, 
2, NULL, NULL, 50, 31, 15, 2
),
(
NULL, 2, 1, 3, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/04.jpg'), '為什麼毛孩懂你的眼淚？看寵物如何用「無條件的愛」療癒我們的靈魂', 
'<h2 data-path-to-node="6">那些不說話的家人，最懂你的心</h2>
<p data-path-to-node="7">你有沒有過這種經驗？當你結束疲憊的一天回到家，或是正躲在房間低頭哭泣時，家裡的貓咪或狗狗總會悄悄走過來，靠在你的腳邊，或者用濕漉漉的鼻子蹭蹭你。</p>
<p data-path-to-node="8">牠們雖然聽不懂人類的語言，打心理學研究發現，寵物對人類的情緒共振（Emotional Contagion）能力超乎想像。牠們能透過敏銳的嗅覺與觀察，察覺到我們壓力荷爾蒙（皮質醇）的變化。</p>
<p data-path-to-node="8"><img src="/article-images/f20c3bd9-81c9-4a02-9b0e-96929d2e1849.jpg" alt="" width="263" height="235"><img src="/article-images/f20c3bd9-81c9-4a02-9b0e-96929d2e1849.jpg" alt="" width="263" height="235"><img src="/article-images/f20c3bd9-81c9-4a02-9b0e-96929d2e1849.jpg" alt="" width="263" height="235"><img src="/article-images/f20c3bd9-81c9-4a02-9b0e-96929d2e1849.jpg" alt="" width="263" height="235"></p>
<h3 data-path-to-node="10">🐾 寵物帶給我們的 3 大心理療癒力</h3>
<ul data-path-to-node="11">
<li>
<p data-path-to-node="11,0,0"><strong data-path-to-node="11,0,0" data-index-in-node="0">無條件的積極關注</strong>：在社會上我們總被要求表現完美，但對毛孩來說，無論你今天多狼狽、賺多少錢，你就是牠的全世界。這種「不帶審判的愛」是現代人最核心的心理慰藉。</p>
</li>
<li>
<p data-path-to-node="11,1,0"><strong data-path-to-node="11,1,0" data-index-in-node="0">降低焦慮的「觸覺療癒」</strong>：科學證實，當我們撫摸寵物的毛髮時，大腦會分泌<strong data-path-to-node="11,1,0" data-index-in-node="34">催產素（Oxytocin）</strong>，這能有效降低心率、舒緩緊繃的神經。</p>
</li>
<li>
<p data-path-to-node="11,2,0"><strong data-path-to-node="11,2,0" data-index-in-node="0">重拾生活的主控權</strong>：憂鬱 or 焦慮常讓人失去動力。而每天固定的餵食、散步與陪伴，能幫我們建立規律的「生活儀式感」，把我們從負面情緒的黑洞中拉回當下。</p>
</li>
</ul>
<blockquote data-path-to-node="12">
<p data-path-to-node="12,0"><strong data-path-to-node="12,0" data-index-in-node="0">心靈小語：</strong> 寵物是用一生在實踐「活在當下」的哲學。當你感到焦慮時，不妨學學身邊的毛孩，伸個懶腰、好好吃一頓飯、曬曬太陽，讓受傷的心跟著牠們一起慢慢被療癒。</p>
</blockquote>',
'2026-07-02 21:43:31', '2026-07-03 14:18:51', '2026-07-05 03:21:37', NULL, '2026-07-05 04:00:00', NULL, 
2, NULL, NULL, 680, 192, 145, 54 
),
(
NULL, 1, 1, NULL, NULL, '草稿1', 
'<h2>草稿1</h2>\r\n<p>&ensp;ᖤ&ensp;&bull; ᴥ &bull;&ensp;ᖢ &emsp;&emsp;&emsp;&emsp;&emsp;&ensp; </p>\r\n<p>&nbsp; ५ &emsp; &emsp;&ensp;𐦾 &ensp;&emsp;&emsp;&emsp; &emsp; &ensp;</p>\r\n<p>&nbsp; &nbsp; &nbsp;ᓑ&oline;&oline;&oline;&oline;&oline;ᓑ&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</p>\r\n<p>&nbsp;</p>\r\n<p>&emsp;&emsp;&emsp;&emsp;&emsp; &ensp;</p>', 
'2026-07-03 15:16:11', '2026-07-05 07:47:58', NULL, NULL, NULL, NULL, 
0, NULL, NULL, 0, 0, 0, 0  
),
(
NULL, 2, 4, 1, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/05.png'), '布丁狗狗', 
'<h1>⠀⠀⠀⠀⠀ 🍮 ⠀⠀⠀⠀⠀⠀</h1>
<p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;⢀⡴⣿⣿⣿⣿⣿⣿⣿⣿⡷⣄⠀⠀</p>
<p>&nbsp; ⢀⣠⡴⠖⠋⠀⠈⠉⠛⠛⠛⠛⠛⠉⠀⠈⠛⠶⣄⡀</p>
<p>⢀⡴⠋⠀⠀⣰⠅⠀⣀⠀⠀⠀⠀⠀⠀⢀⠀⢰⡄⠀⠈⠻⣦⠀</p>
<p>⣾⠁⠀⠀⢰⠇⠀⠀⠉⠀⢀⠘⡗⣀⠀⠉⠀⠀⢷⡀⠀⠀⢸⡆</p>
<p>⠳⣄⣀⣠⡟⠀⠀⠀⠀⠀⠈⠊⠙⠋⠀⠀⠀⠀⠈⢳⡶⠴⠞⠁</p>
<p>⠀⠀⢩⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢻⡄⠀⠀</p>
<p>⠀⠀⣾⠀⣼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⣧⠀⣿⠀⠀</p>
<p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ⠘⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡾⠁⠀⠀</p>
<p>⠀&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;⢳⣀⣴⠋⠉⠉⠉⠉⠉⠉⠳⢤⡼⠃⠀⠀</p>
<h2>&nbsp;</h2>', 
'2026-07-03 20:16:31', '2026-07-05 02:51:25', '2026-07-05 02:48:54', '2026-07-05 03:22:08', '2026-07-05 03:30:00', NULL, 
2, NULL, NULL, 312, 88, 42, 19   
),
(
NULL, 1, 3, 1, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/7009.png'), '草稿2',
'<h2>草稿2</h2>
<p>ᖤ&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;ᖢ &emsp;&emsp;&emsp;&emsp;&emsp;&ensp;</p>
<p>५ &emsp; *&emsp;&ensp;𐦾 &ensp;&emsp;&emsp;&emsp;&emsp;&emsp; &ensp;</p>
<p>&nbsp; &nbsp;ᓑ&oline;&oline;&oline;&oline;&oline;ᓑ</p>',
'2026-07-04 17:08:27', '2026-07-05 07:46:54', NULL, NULL, NULL, NULL, 
0, NULL, NULL, 0, 0, 0, 0       
),
(
NULL, 1, 2, 1, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/07.png'), '不是因為懶！心理學揭密：為什麼我們突然會「完全不想工作」？', 
'<h2 data-path-to-node="6">當「熱情」被消耗殆盡：認識職場燃盡症候群</h2>
<p data-path-to-node="7">每天早上鬧鐘響起，你是否總覺得雙腿像灌了鉛一樣沉重，心裡充斥著抗拒？在過去，這種現象常被簡單歸咎於「偷懶」或「抗壓性不足」；但在現代心理學中，這很可能是「職場燃盡（Burnout）」的紅色警訊。</p>
<p data-path-to-node="8">當一個人長期處於高壓、缺乏成就感，或是付出與回報不成正比的環境中，大腦的保護機制就會啟動，用「不想工作」的消極抗拒，來阻止精神面臨更嚴重的崩潰。</p>
<p data-path-to-node="8"><img src="/article-images/5a66c730-b9f0-4d0f-b1b1-95d2c8742f92.png" alt="" width="703" height="404"></p>
<h3 data-path-to-node="10">奪走工作動力的 3 大心理隱形殺手</h3>
<ul data-path-to-node="11">
<li>
<p data-path-to-node="11,0,0"><strong data-path-to-node="11,0,0" data-index-in-node="0">習得性無助（Learned Helplessness）</strong>：當你在職場上無論怎麼努力、怎麼加班，最後的功勞總是別人的，或者政策天天都在變，大腦就會學會「努力也沒有用」，進而徹底失去動力。</p>
</li>
<li>
<p data-path-to-node="11,1,0"><strong data-path-to-node="11,1,0" data-index-in-node="0">價值觀與角色的衝突</strong>：你可能渴望做出有溫度的產品，公司卻只看重冰冷的數據與流水線；這種內在價值觀的拉扯，會以極快的速度消耗你的心理資本。</p>
</li>
<li>
<p data-path-to-node="11,2,0"><strong data-path-to-node="11,2,0" data-index-in-node="0">情緒勞動（Emotional Labor）過載</strong>：職場上不只要應付公事，更要花精力管理表情、應酬人際關係。當每天戴著「專業面具」的時間過長，內向特質的人特別容易陷入電力耗盡的狀態。</p>
</li>
</ul>
<blockquote data-path-to-node="12">
<p data-path-to-node="12,0"><strong data-path-to-node="12,0" data-index-in-node="0">心理學小建議：</strong> 不想工作時，強迫自己「加油」往往適得其反。試著把工作與個人價值切換成「微抽離」狀態&mdash;&mdash;上班只是履行合約，下班才是探索自我。給自己安排一個沒有任何工作訊息的週末，是重建心理韌性的第一步。</p>
</blockquote>',
'2026-07-05 03:16:50', '2026-07-05 03:17:23', '2026-07-05 03:16:50', '2026-07-05 03:20:32', '2026-07-05 03:25:00', NULL, 
2, NULL, NULL, 940, 245, 180, 88  
),
(
NULL, 1, 2, 2, LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/08.png'), '擺退內耗惡性循環：為什麼「沒工作焦慮，工作了又想離職」？', 
'<h2 data-path-to-node="7">左右為難的「職場鐘擺效應」</h2>
<p data-path-to-node="8">你是否也陷入了這種痛苦的輪迴：待業在家時，看著存折與空白的履歷，內心充滿了被社會拋棄的集體焦慮，恨不得隨便抓一根浮木就業；然而，一旦真的開工、進了辦公室，面對開不完的會和窒息的職場人際，內心卻又瘋狂湧現想遞辭呈的衝動？</p>
<p data-path-to-node="9">這種「在焦慮與厭世之間反覆橫跳」的狀態，在心理學上常被稱為職場的雙向內耗。這並不是因為你抗壓性低，而是大腦在「生存安全感」與「自我價值感」之間失去了平衡。</p>
<h3 data-path-to-node="11">解構「盲目就業、痛苦離職」的背後動機</h3>
<ul data-path-to-node="12">
<li>
<p data-path-to-node="12,0,0"><strong data-path-to-node="12,0,0" data-index-in-node="0">沒工作時的「存在焦慮」</strong>：現代社會往往將「職業」與「個人價值」死死綁定。當失去工作標籤時，我們感到的不只是經濟壓力，更多的是找不到自身定位的恐慌，進而為了逃避焦慮而「盲目就業」。</p>
</li>
<li>
<p data-path-to-node="12,1,0"><strong data-path-to-node="12,1,0" data-index-in-node="0">工作後的「心理資本透支」</strong>：為了逃避恐慌而草率選擇的工作，往往與內在需求不符。當每天的付出只是在滿足別人的期待，你的主體性會不斷被消磨，導致上班沒幾天，心理能量就直接見底。</p>
</li>
<li>
<p data-path-to-node="12,2,0"><strong data-path-to-node="12,2,0" data-index-in-node="0">缺乏「中間緩衝地帶」</strong>：從極度焦慮的待業狀態，直接切換到高壓的工作節奏，靈魂根本還沒有準備好。這就像沒有暖身就直接挑戰馬拉松，受傷只是時間問題。</p>
</li>
</ul>
<hr>
<p data-path-to-node="15"><h3 style="color: #e8b4b4">親愛的，你只是需要時間，重新找回生活的配速。</h3></p>
<p data-path-to-node="16">無論是前進還是停下，這份反覆拉扯的疲憊，都在提醒你該好好傾聽內心的聲音了。如果你正卡在進退兩難的泥淖中感到窒息，請記得，你不需要一個人硬撐。平台的專業諮商心理師隨時在這裡，願意陪伴你一起釐清職業規劃與心理盲點，陪你找回內心真正渴望的平穩與安全感。</p>', 
'2026-07-05 08:02:03', '2026-07-05 08:02:03', '2026-07-05 08:02:03', '2026-07-05 08:10:00', '2026-07-05 08:15:00', NULL, 
2, NULL, NULL, 1250, 312, 220, 115 
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
(1, 1, '2026-05-24 14:50:00'), -- member1 viewed article1
(2, 1, '2026-05-22 13:00:00'), -- member1 viewed article2
(1, 2, '2026-05-23 07:50:00'), -- member2 viewed article1
(1, 3, '2026-05-23 10:45:00'); -- member1 viewed article3

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
(1, 1, '2026-05-24 15:00:00'), -- member1 saved article1
(2, 1, '2026-05-24 16:30:00'), -- member2 saved article1
(1, 2, '2026-05-23 08:00:00'), -- member1 saved article2
(1, 3, '2026-05-23 11:00:00'); -- member1 saved article3

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
(1, 1, '2026-05-24 14:55:00'), -- member1 liked article1
(2, 1, '2026-05-22 13:10:00'), -- member2 liked article1
(1, 2, '2026-05-23 09:15:00'), -- member1 liked article2
(1, 3, '2026-05-23 11:45:00'); -- member1 liked article3


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
-- 4. 狀態 3：已退回(發起人:會員1)
(3, '臺北療癒乾燥花束手作體驗',  1, 4, 5,
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
-- 6. 狀態 5：延期(發起人:會員2)有2筆成功報名
(5, '大稻埕河濱輕鬆夜跑團', 2, 6, 5,
  '從大稻埕出發，沿著河濱享受微風，配速隨意，健康第一，跑完可以一起去迪化街吃宵夜或逛逛寧夏夜市。', 
  '臺北市', '大同區', '大稻埕碼頭廣場', '1784226162161_0fe567aa.jpg', '2026-08-10 00:00:00', '2026-08-22 23:59:59', '2026-08-29 19:30:00', '2026-08-29 20:30:00', 
  20, 2, 5, 0, '2026-08-01 08:00:00', '2026-08-02 11:00:00', NULL, NULL, NULL, '因氣象預報週末豪大雨，活動將延期舉行，時間另行通知。', NULL, '2026-08-06 10:00:00', '2026-08-25 20:00:00'
),
-- 7. 狀態 2：已發布+可報名中(發起人:會員2)→ 測報名功能
(2, '淡水夕陽漫步與心靈對話', 2, 2, 1,
  '傍晚時分沿著淡水河岸慢慢走，看夕陽沉入觀音山。步行途中安排簡單的正念引導，讓身體移動、讓思緒沉澱，適合想放慢腳步的你。',
  '新北市', '淡水區', '淡水漁人碼頭', '1784226149395_f46b31c1.jpg', '2026-07-01 00:00:00', '2026-08-20 23:59:59', '2026-08-27 17:00:00', '2026-08-27 19:30:00',
  12, 0, 3, 0, '2026-06-20 10:00:00', '2026-06-22 15:00:00', NULL, NULL, NULL, NULL, NULL, '2026-06-25 08:00:00', '2026-06-25 08:00:00'
),
-- 8. 狀態2：已發布+已結束(發起人:會員1)
(2, '阿里山日出輕鬆小旅行', 1, 3, 1,
  '一起搭乘小火車上山，等待日出灑落雲海的感動時刻，全程有專業導覽員隨行解說，適合想暫時遠離城市喧囂的你。',
  '嘉義縣', '阿里山鄉', '阿里山國家森林遊樂區', '1784522382115_d67d1f5c.jpg', '2026-05-20 00:00:00', '2026-06-05 23:59:59', '2026-06-20 05:00:00', '2026-06-21 10:00:00',
  15, 2, 3, 0, '2026-05-15 10:00:00', '2026-05-16 09:00:00', NULL, NULL, NULL, NULL, NULL, '2026-05-16 09:00:00', '2026-05-16 09:00:00'
),
-- 9. 狀態2：已發布+已結束(發起人:會員3)→ 3筆成功報名(含會員7)
(2, '臺南神農街文創手作市集體驗', 3, 4, 5,
  '走進老街巷弄，跟著在地職人學做屬於自己的文創小物，體驗結束後還能自由逛逛周邊特色小店，感受府城的悠閒步調。',
  '臺南市', '中西區', '神農街', '1784226097230_ad348ec2.jpg', '2026-05-25 00:00:00', '2026-06-05 23:59:59', '2026-06-10 14:00:00', '2026-06-10 17:00:00',
  12, 3, 3, 0, '2026-05-18 09:00:00', '2026-05-19 10:00:00', NULL, NULL, NULL, NULL, NULL, '2026-05-19 10:00:00', '2026-05-19 10:00:00'
),
-- 10. 狀態2：已發布+報名進行中，正取2/2滿、備取1/1也滿(發起人:會員1)→ 測「正取+備取都額滿」擋新申請
(2, '木柵山區手作陶藝療癒工作坊', 1, 4, 1,
  '在山林環繞的工作室裡，跟著陶藝老師從捏土開始，親手做出屬於自己的一件作品，過程專注而療癒，特別適合想暫時放空的你。',
  '臺北市', '文山區', '木柵陶坊', '1784522392938_a90c14fe.jpg', '2026-07-10 00:00:00', '2026-07-31 23:59:59', '2026-08-05 14:00:00', '2026-08-05 17:00:00',
  2, 2, 1, 1, '2026-07-05 09:00:00', '2026-07-06 10:00:00', NULL, NULL, NULL, NULL, NULL, '2026-07-06 10:00:00', '2026-07-06 10:00:00'
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
(0, 4, 7, '2026-07-15 10:00:00', '一直很想參加公益類的活動，希望能盡一份心力也交到新朋友。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
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
(1, 2, 8, '2026-05-25 10:00:00', '一直想去阿里山看日出，這次終於有機會參加。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- 12. 狀態 1：8號活動的成功報名(會員3)
(1, 3, 8, '2026-05-26 11:00:00', '想暫時遠離城市喧囂，體驗山林間的日出美景。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),  
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
(0, 5, 7, '2026-07-12 14:00:00', '平常工作步調很快，想試試放慢腳步、練習正念的活動。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(0, 6, 7, '2026-07-14 19:30:00', '喜歡淡水的夕陽，也想學習讓思緒沉澱的方法。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
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
(3, 6, 28, '2026-05-30 09:00:00', '想參加羽球團練活動。', '2026-06-10 10:00:00', 0, '臨時有其他行程安排，無法配合活動時間，很抱歉。', NULL, NULL, NULL, NULL, NULL);

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
(0, 1, 15, NULL, '現場物資分配動線規劃不佳，導致排隊時間過長，希望之後能改善動線設計。', NULL, '2026-06-02 10:00:00', NULL),
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
(2, 1, 3, 5, '民宿早餐選擇太少，連續兩天都是一樣的菜色，稍嫌單調。', '您好，感謝您的建議，我們已將意見反映給民宿業者，未來會增加早餐菜色的變化，謝謝您的回報。', '2026-07-03 09:00:00', '2026-07-05 10:00:00'),
(1, 3, 8, 1, '小火車座位安排太擠，隨身行李幾乎沒有地方可以放。', NULL, '2026-06-22 10:00:00', NULL),
(0, 2, 8, NULL, '當天氣候不佳雲層太厚，最後其實沒有看到日出，覺得有點可惜。', NULL, '2026-06-21 14:00:00', NULL),
(1, 7, 9, 5, '手作材料包裡有一項材料缺少，需要現場另外購買才能完成作品。', NULL, '2026-06-13 09:00:00', NULL),
(0, 1, 6, NULL, '延期後的新場地離捷運站有點遠，希望能補充交通資訊。', NULL, '2026-07-16 10:00:00', NULL);

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
(1, 1, '2026-07-13', '000000000111111111000000'), 
(2, 1, '2026-07-18', '000000000111111211000000'), 
(3, 1, '2026-07-25', '000000000111111111000000'), 
(4, 1, '2026-07-28', '000000000111111111000000'), 
(5, 2, '2026-07-10', '000000000111111211000000'), 
(6, 2, '2026-07-15', '000000000111111121000000'), 
(7, 2, '2026-07-24', '000000000111112111000000'),
(8, 2, '2026-07-28', '000000000111111111000000'); 
 
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

(2, 2, '2026-07-18 15:00:00', '2026-07-18 16:00:00', 1, 1, '2026-07-12 10:00:00', '桃園市中壢區中山路52號',
 1, 0, 2000, 'stress', '工作壓力很大', 0, NULL, NULL, NULL, NULL),

(3, 7, '2026-07-24 14:00:00', '2026-07-24 15:00:00', 2, 2, '2026-07-19 15:00:00', '新北市板橋區文化路一段188號',
 1, 0, 1800, 'sleep', '長期失眠困擾', 0, NULL, NULL, NULL, NULL),

(4, 1, '2026-07-13 11:00:00', '2026-07-13 12:00:00', 2, 1, '2026-07-08 09:00:00', '桃園市中壢區中山路52號',
 2, 0, 2000, 'relationship', '人際相處常有摩擦', 0, NULL, NULL, NULL, NULL),

(5, 6, '2026-07-15 16:00:00', '2026-07-15 17:00:00', 1, 2, '2026-07-08 16:00:00', '新北市板橋區文化路一段188號',
 3, 0, 1800, 'sleep', '睡眠品質差', 0, NULL, NULL, NULL, NULL),

(6, 5, '2026-07-10 15:00:00', '2026-07-10 16:00:00', 2, 2, '2026-07-03 10:00:00', '新北市板橋區文化路一段188號',
 4, 0, 1800, 'emotion', '情緒低落想找人談談', 0,
 '個案情緒逐漸穩定，建議持續記錄情緒並保持規律作息', 5,
 '心理師很專業又溫暖，收穫很多，謝謝！', '2026-07-11 20:00:00');


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
(1, 5, 2,    '被標記為未出席，但我當天有準時到場，希望協助查證', '2026-07-16 10:00:00', 2, '已調閱時段紀錄與心理師確認，將更正出席狀態'),
(2, 6, NULL, '想索取這次諮商的付款收據',                 '2026-07-11 14:00:00', 0, NULL);

-- ==========================================
-- {課程商城}
-- 課程分類編號(course_categories)
-- ==========================================

CREATE TABLE course_categories (
  course_cat_id INT NOT NULL AUTO_INCREMENT,
  course_cat_name VARCHAR(50) NOT NULL,
  PRIMARY KEY (course_cat_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
insert into course_categories(course_cat_name)
values('課程分類一'),('課程分類二'),('課程分類三'),('課程分類四');


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
-- 2. INSERT INTO tbName: 插入3-5筆假資料 -- 
INSERT INTO courses (
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
(
    '壓力管理與情緒調適',
    1,
    1,
    1,
    '/uploads/videos/courseNo1.mp4',
    '/uploads/videos/coursePre.mp4',
    '介紹壓力形成的原因，並透過實際練習學習情緒調適與放鬆技巧。',
    '2026-05-01 09:00:00',
    NULL,
    NULL,
    4,
    120,
    100,
    450,
    32,
    0.85,
    '2026-07-01 00:00:00',
    '2026-07-31 23:59:59',
    1200
),
(
    '改善失眠的心理練習',
    2,
    1,
    2,
    '/uploads/videos/courseNo2.mp4',
    '/uploads/videos/coursePre.mp4',
    '透過睡眠衛生、認知調整與放鬆訓練，建立更穩定且健康的睡眠習慣。',
    '2026-05-10 10:30:00',
    NULL,
    NULL,
    4,
    88,
    300,
    1100,
    18,
    0.90,
    '2026-07-10 00:00:00',
    '2026-08-10 23:59:59',
    1000
),
(
    '建立健康的親密關係',
    3,
    2,
    3,
    '/uploads/videos/courseNo3.mp4',
    '/uploads/videos/coursePre.mp4',
    '認識依附關係、溝通模式與衝突處理方式，建立更成熟的親密關係。',
    '2026-05-20 14:00:00',
    NULL,
    NULL,
    4,
    156,
    510,
    1610,
    46,
    NULL,
    NULL,
    NULL,
    1500
),
(
    '職場人際溝通技巧',
    1,
    2,
    4,
    '/videos/workplace-communication.mp4',
    '/videos/preview/workplace-communication.mp4',
    '協助學員提升職場表達、傾聽與衝突協調能力，改善團隊合作關係。',
    '2026-06-01 08:30:00',
    NULL,
    NULL,
    4,
    75,
    280,
    660,
    21,
    0.80,
    '2026-07-15 00:00:00',
    '2026-08-15 23:59:59',
    900
),
(
    '認識焦慮與自我照顧',
    2,
    NULL,
    1,
    '/videos/anxiety-care.mp4',
    '/videos/preview/anxiety-care.mp4',
    '認識焦慮的身心反應，學習辨識觸發因素並建立適合自己的自我照顧方式。',
    NULL,
    NULL,
    NULL,
    1,
    0,
    0,
    0,
    0,
    NULL,
    NULL,
    NULL,
    1100
),
(
    '兒童情緒陪伴入門',
    3,
    NULL,
    2,
    '/videos/child-emotion.mp4',
    '/videos/preview/child-emotion.mp4',
    '提供家長與照顧者實用的兒童情緒辨識、同理回應與陪伴技巧。',
    NULL,
    NULL,
    NULL,
    0,
    0,
    0,
    0,
    0,
    NULL,
    NULL,
    NULL,
    1300
),
(
    '正念冥想基礎課程',
    1,
    1,
    1,
    '/videos/mindfulness.mp4',
    '/videos/preview/mindfulness.mp4',
    '從呼吸覺察開始練習正念，逐步提升專注力與對身心狀態的覺察能力。',
    NULL,
    NULL,
    NULL,
    2,
    15,
    0,
    0,
    3,
    0.75,
    '2026-08-01 00:00:00',
    '2026-08-31 23:59:59',
    800
),
(
    '走出低潮與負面思考',
    2,
    2,
    3,
    '/videos/negative-thinking.mp4',
    '/videos/preview/negative-thinking.mp4',
    '學習辨識常見的負面思考模式，並透過認知調整建立更有彈性的觀點。',
    NULL,
    NULL,
    NULL,
    3,
    5,
    0,
    0,
    1,
    NULL,
    NULL,
    NULL,
    1400
),
(
    '提升自信與自我價值',
    3,
    1,
    4,
    '/videos/self-confidence.mp4',
    '/videos/preview/self-confidence.mp4',
    '從自我認識、內在對話與行動練習出發，逐步建立穩定的自我價值感。',
    '2026-04-15 11:00:00',
    '2026-07-01 18:00:00',
    '授權期滿',
    5,
    210,
    620,
    2140,
    55,
    NULL,
    NULL,
    NULL,
    1600
),
(
    '高敏感族群的生活調適',
    1,
    2,
    2,
    '/videos/high-sensitivity.mp4',
    '/videos/preview/high-sensitivity.mp4',
    '認識高敏感特質，學習管理刺激、建立界線並找到適合自己的生活節奏。',
    '2026-03-20 13:00:00',
    '2026-06-30 17:00:00',
    '講師要求',
    5,
    135,
    390,
    1185,
    27,
    0.90,
    '2026-05-01 00:00:00',
    '2026-05-31 23:59:59',
    1250
);
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
INSERT INTO course_bookmarks
(course_id,member_id,saved_at)
VALUES 
(1,1,'2026:05:22:00:00:00'),
(2,1,'2026:05:22:00:00:00'),
(2,2,'2026:05:20:00:00:00');

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
INSERT INTO coupons
(coupon_name,discount_duration,discount)
VALUES 
('生日優惠券',30,0.85),
('九折優惠券',30,0.90),
('八折優惠券',30,0.80);

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
INSERT INTO member_coupons
(coupon_id,member_id,coupon_start_at,coupon_end_at)
VALUES 
(1,1,'2026:05:22:00:00:00','2026:06:22:00:00:00'),
(2,1,'2026:05:22:00:00:00','2026:06:22:00:00:00'),
(2,2,'2026:05:22:00:00:00','2026:06:22:00:00:00'),
(2,3,'2026:05:22:00:00:00','2026:06:22:00:00:00');

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
INSERT INTO shopping_carts
(member_id,course_id,created_at)
VALUES
(1,1,'2026:05:20:00:00:00'),
(1,2,'2026:05:20:00:00:00'),
(2,1,'2026:05:20:00:00:00');

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
INSERT INTO course_orders
(member_id, coupon_serial_no, order_total, discount_amount,
 net_amount, payment_method, payment_status, ordered_at)
VALUES
(1, 1, 3000, 450, 2550, 0, 1, '2026:05:22:00:00:00'),
(2, NULL, 1000, 0,1000, 1, 0, '2026-05-23 14:20:00'),
(3, 4, 2000, 200,1800, 0, 1, '2026-05-24 09:15:00');

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
INSERT INTO order_details
VALUES 
(1,1,1000,1000,1,4,'訂單一評價課程一','2026:05:23:00:00:00',50.0,'00:30:00'),
(1,2,2000,2000,1,4,'訂單一評價課程二','2026:05:23:00:00:00',50.0,'00:30:00'),
(2,1,1000,1000,0,NULL,NULL,NULL,NULL,NULL),
(3,2,2000,2000,1,4,NULL,NULL,50.0,'00:30:00');

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
INSERT INTO course_qa_comments
(course_id, member_id, asked_at, course_question, answered_at, course_answer)
VALUES
(1, 1, '2026-05-20 10:30:00','諮商到我腦袋壞掉 差評!!','2026-05-20 12:00:00',
'謝謝您 歡迎再次回購'),
(1, 2, '2026-05-21 14:20:00','諮商師很專業','2026-05-21 15:10:00','謝謝您 歡迎再次回購'),
(2, 3, '2026-05-22 09:15:00','購買後可以重複觀看嗎？',NULL,NULL);

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
INSERT INTO refunds
(course_order_id, member_id, admin_id,refund_reason, refund_amount,
 created_at, refunded_at, refund_status)
VALUES
(1, 1, 1,'課程內容不符合預期',2550,'2026-05-20 10:30:00','2026-05-22 14:00:00',3),
(2, 2, NULL,'老師風評不好',1000,'2026-05-21 09:15:00',NULL,0),
(3, 3, 2,'沒錢退錢',1800,'2026-05-23 16:40:00',NULL,1);

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
INSERT INTO payouts
(billing_month, psych_id, admin_id,gross_payout_amount, platform_commission,
 billing_offset, net_payout_amount,paid_at, payout_status)
VALUES
('2026-04', 1, 1,50000, 5000,0, 45000,'2026-05-05 14:30:00', 1),
('2026-04', 2, 2,42000, 4200,2000, 35800,'2026-05-05 15:00:00', 1),
('2026-05', 3, NULL,38000, 3800,0, 34200,NULL, 0);