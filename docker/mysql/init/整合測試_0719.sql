CREATE DATABASE IF NOT EXISTS dbtest; SET NAMES utf8mb4;
USE dbtest;

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
(5, 'admin05', '5555', 1, '活動管理員', '0955555555', '2026-05-22 08:30:00', NULL);

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
  (
    '3', '3', 0, '王美玲', '女', '0934567890', 'wang@example.com', 
    'CERT-2019-00256', 0, '台北市大安區忠孝東路四段100號', 1500, 
    '000010100000001000001010001111111100010010001111111100010011000010100011111111000100100001010001111111100010011000010100011111111000100100001010001111111100010011000100', 
    '2024-01-08 14:00:00', NULL
  ),
  (
    '4', '4', 1, '小吳', '男', '0930495890', 'wu@example.com', 
     'CERT-2019-10456', 0, '台北市大安區忠孝東路四段100號', 2000, 
    '000010100000001000001010001111111100010010001111111100010011000010100011111111000100100001010001111111100010011000010100011111111000100100001010001111111100010011000100', 
    '2024-01-08 14:00:00', NULL
  );

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
  ('親子關係');

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
  (3, 1);


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
  (3, 1, '本平台將於 2024-07-01 舉辦年度心理健康嘉年華，歡迎報名參加！',    2, '2024-06-15 08:00:00', 0);


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
  (1, 1, '您投稿的文章「如何舒緩工作壓力」審核通過，已公開發布。',0, '2024-05-30 10:00:00', 1),
  (2, 2, '會員林小華已預約您 2024-06-05 14:00 的諮詢時段，請確認行事曆。',1, '2024-06-01 13:00:00', 0),
  (3, 1, '您報名的「認知行為治療進階課程」已通過審核，開課日期為 2024-07-10。',2, '2024-06-10 09:00:00', 0);

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
  published_at DATETIME,
  updated_at DATETIME,
  CONSTRAINT fk_activities_member
		FOREIGN KEY (member_id) REFERENCES member(member_id),
  CONSTRAINT fk_activities_activity_categories
		FOREIGN KEY (activity_cat_id) REFERENCES activity_categories(activity_cat_id),
  CONSTRAINT fk_activities_admin
		FOREIGN KEY (admin_id) REFERENCES admin(admin_id)
);

-- 2. INSERT INTO tbName: 插入3~5筆假資料 -- 
INSERT INTO activities (activity_status, activity_name, member_id, activity_cat_id, admin_id, activity_content, activity_city, 
   activity_dist, activity_loc, picture, regis_start, regis_end, activity_start, activity_end, capacity, regis_count,
   waitlist_capacity, waitlist_count,
   created_at, reviewed_at, reject_reason, reject_note, cancel_note, postpone_note, published_at, updated_at
)VALUES 
-- 1. 狀態 0：待審核(發起人:會員1)
(0, '秋日駁二傳遞溫暖 FREE HUGS', 1, 1, NULL,
  '在南臺灣熱情的陽光下，給彼此一個大大的擁抱吧！不需要任何條件，只需要帶著一顆溫暖的心，一起在週末傳遞正能量。',
  '高雄市', '鹽埕區', '駁二藝術特區', '1784226224189_4b4617a4.jpg', '2026-09-01 12:00:00', '2026-09-18 23:59:59', '2026-09-26 14:00:00', '2026-09-26 19:00:00',
   15, 0, 5, 0, '2026-08-15 14:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL
 ),
-- 2. 狀態 1：已審核(發起人:會員1)
(1, '陽明山輕鬆健行半日遊', 1, 2, 1,
  '享受大自然芬多精，這是一條適合新手的健行路線，沿途風景優美，路勢平緩，歡迎大家一起來放鬆～',
  '臺北市', '士林區', '陽明山擎天崗', NULL, '2026-08-01 12:00:00', '2026-08-15 23:59:59', '2026-09-01 07:30:00', '2026-09-01 09:30:00', 
  20, 0, 5, 0, '2026-07-22 09:00:00', '2026-07-24 14:00:00', NULL, NULL, NULL, NULL, NULL, '2026-07-24 14:00:00'
),
-- 3. 狀態 2：已發布(發起人:會員2)有2筆成功報名
(2, '花東看海發呆三天兩夜小旅行', 2, 3, 2,
  '拋開工作的疲憊，一起到花東海岸線看海、發呆、放空。這是一場不需要塞滿行程的療癒之旅，坐在海景民宿前聽著浪濤聲，找回內心的平靜。', 
   '花蓮縣', '新城鄉', '七星潭', '1784226172488_3e2c7de4.jpg', '2026-05-10 10:00:00', '2026-06-01 23:59:59', '2026-06-28 09:00:00', '2026-06-30 17:00:00', 
  10, 2, 3, 0, '2026-05-01 11:31:00', '2026-05-06 12:00:00', NULL, NULL, NULL, NULL, '2026-05-08 00:00:00', '2026-05-08 00:00:00'
),
-- 4. 狀態 3：已退回(發起人:會員1)
(3, '臺北療癒乾燥花束手作體驗',  1, 4, 3,
  '大家一起來文青小店做美麗的乾燥花束吧！(現場酌收材料費用)。', 
  '臺北市', '中山區', '合江街', '1784226246151_a4bc5df9.jpg', '2026-07-01 00:00:00', '2026-07-15 23:59:59', '2026-07-25 14:00:00', '2026-07-25 18:00:00', 
  10, 0, 3, 0, '2026-06-23 15:00:00', '2026-06-25 09:30:00', 0, '審核退回：請補充活動詳細流程、材料費金額及包含項目，以及場地確切位置。', 
   NULL, NULL, NULL, '2026-06-25 09:30:00'
),
-- 5. 狀態 4：取消(發起人:會員2)容量2、額滿2、備取上限2、備取1人 → 測「已額滿」+「備取」
(4, '臺中綠園道頌缽冥想放鬆', 2, 5, 4,
  '透過尼泊爾頌缽的頻率，引導身心進入深層放鬆狀態，清理思緒，釋放累積一整週的壓力。', 
  '臺中市', '西區', 'Soul Chill Studio 用心生活工作室', '1784226136912_fa92337c.jpg', '2026-07-03 08:00:00', '2026-07-10 21:59:59', '2026-07-16 20:30:00', '2026-07-16 21:30:00', 
  2, 2, 2, 1, '2026-06-25 10:00:00', '2026-06-26 17:00:00', NULL, NULL, '因發起人罹患流感，故取消本次活動，請見諒。', NULL, '2026-06-27 19:00:00', '2026-07-14 10:00:00'
),
-- 6. 狀態 5：延期(發起人:會員2)有2筆成功報名
(5, '大稻埕河濱輕鬆夜跑團', 2, 6, 5,
  '從大稻埕出發，沿著河濱享受微風，配速隨意，健康第一，跑完可以一起去迪化街吃宵夜或逛逛寧夏夜市。', 
  '臺北市', '大同區', '大稻埕碼頭廣場', '1784226162161_0fe567aa.jpg', '2026-08-10 00:00:00', '2026-08-22 23:59:59', '2026-08-29 19:30:00', '2026-08-29 20:30:00', 
  20, 2, 5, 0, '2026-08-01 08:00:00', '2026-08-02 11:00:00', NULL, NULL, NULL, '因氣象預報週末豪大雨，活動將延期舉行，時間另行通知。', '2026-08-06 10:00:00', '2026-08-25 20:00:00'
),
-- 7. 狀態 2：已發布+可報名中(發起人:會員2)→ 測報名功能
(2, '淡水夕陽漫步與心靈對話', 2, 2, 1,
  '傍晚時分沿著淡水河岸慢慢走，看夕陽沉入觀音山。步行途中安排簡單的正念引導，讓身體移動、讓思緒沉澱，適合想放慢腳步的你。',
  '新北市', '淡水區', '淡水漁人碼頭', '1784226149395_f46b31c1.jpg', '2026-07-01 00:00:00', '2026-08-20 23:59:59', '2026-08-27 17:00:00', '2026-08-27 19:30:00',
  12, 0, 3, 0, '2026-06-20 10:00:00', '2026-06-22 15:00:00', NULL, NULL, NULL, NULL, '2026-06-25 08:00:00', '2026-06-25 08:00:00'
),
-- 8. 狀態2：已發布+已結束(發起人:會員1)
(2, '阿里山日出輕鬆小旅行', 1, 3, 1,
  '一起搭乘小火車上山，等待日出灑落雲海的感動時刻，全程有專業導覽員隨行解說，適合想暫時遠離城市喧囂的你。',
  '嘉義縣', '阿里山鄉', '阿里山國家森林遊樂區', NULL, '2026-05-20 00:00:00', '2026-06-05 23:59:59', '2026-06-20 05:00:00', '2026-06-21 10:00:00',
  15, 2, 3, 0, '2026-05-15 10:00:00', '2026-05-16 09:00:00', NULL, NULL, NULL, NULL, '2026-05-16 09:00:00', '2026-05-16 09:00:00'
),
-- 9. 狀態2：已發布+已結束(發起人:會員3)
(2, '臺南神農街文創手作市集體驗', 3, 4, 2,
  '走進老街巷弄，跟著在地職人學做屬於自己的文創小物，體驗結束後還能自由逛逛周邊特色小店，感受府城的悠閒步調。',
  '臺南市', '中西區', '神農街', '1784226097230_ad348ec2.jpg', '2026-05-25 00:00:00', '2026-06-05 23:59:59', '2026-06-10 14:00:00', '2026-06-10 17:00:00',
  12, 2, 3, 0, '2026-05-18 09:00:00', '2026-05-19 10:00:00', NULL, NULL, NULL, NULL, '2026-05-19 10:00:00', '2026-05-19 10:00:00'
),
-- 10. 狀態2：已發布+報名進行中，正取2/2滿、備取1/1也滿(發起人:會員1)→ 測「正取+備取都額滿」擋新申請
(2, '木柵山區手作陶藝療癒工作坊', 1, 4, 1,
  '在山林環繞的工作室裡，跟著陶藝老師從捏土開始，親手做出屬於自己的一件作品，過程專注而療癒，特別適合想暫時放空的你。',
  '臺北市', '文山區', '木柵陶坊', NULL, '2026-07-10 00:00:00', '2026-07-31 23:59:59', '2026-08-05 14:00:00', '2026-08-05 17:00:00',
  2, 2, 1, 1, '2026-07-05 09:00:00', '2026-07-06 10:00:00', NULL, NULL, NULL, NULL, '2026-07-06 10:00:00', '2026-07-06 10:00:00'
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
(2, 5, '2026-06-27 20:30:00'),-- 會員 2 關注了活動 5、活動 6
(2, 7, '2026-07-08 21:00:00');

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
-- 1. 狀態 0：待審核(會員4 報 1號活動)→ 測審核功能
(0, 4, 1, '2026-08-15 10:00:00', '一直很想參加公益類的活動，希望能盡一份心力也交到新朋友。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
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
(4, 4, 10, '2026-07-08 10:00:00', '第一次接觸陶藝，很期待能體驗看看。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

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
(1, 1, 3, 2, '民宿安排的房型與當初活動頁面上說明的完全不同，四人房變成很擠的通鋪，覺得權益受損，希望能協助處理部分退費。', 
 NULL, '2026-07-02 10:00:00', NULL),
(1, 3, 8, 1, '導覽人員對當地的歷史文化介紹非常簡略，跟活動頁面上寫的深度導覽內容落差很大，感覺誠意不足。', 
 NULL, '2026-06-23 15:00:00', NULL),
-- 狀態 2：已處理
(2, 1, 9, 3, '老師原訂三小時的教學時間，實際上只教了不到兩小時就提前結束，內容也沒有完整教完，覺得CP值不如預期。', 
 '您好，感謝您的回報。經與主辦方確認，當天因場地租借時間限制提前結束，我們已提醒主辦方未來需完整規劃教學時數，也會加強場地租借時間評估，造成您的不便深感抱歉。', 
 '2026-06-12 14:00:00', '2026-06-13 10:00:00'),
(2, 2, 9, 2, '現場收取的材料費比當初平台上寫的多收了 100 元，雖然現場反映後有退還，但感覺整體收費流程有瑕疵。', 
 '您好，非常抱歉造成您的不愉快。經與主辦方聯繫確認，為現場工作人員溝通疏失。我們已對該主辦方發出警告，感謝您的回報。', 
 '2026-06-12 09:00:00', '2026-06-14 14:00:00');

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
    
INSERT INTO consultation_slots (
	psych_id,
	slot_date,
	appt_status
) VALUES
(1, '2026-05-25', '000000001111111111110000'),
(2, '2026-05-26', '001111000001000001111111'),
(3, '2026-05-27', '111100000000000011111111'),
(4, '2026-05-28', '111111111100000011111000');
 
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
	order_id,
	time_id,
    cons_start,
    cons_end,
    member_id,
    psych_id,
    created_at,
    psych_loc,
    order_status,
    has_gov_subsidy,
    psych_fee,
    visit_purpose,
    visit_purpose_note,
    session_type,
    psych_note,
    rating,
    review_content,
    reviewed_at
) VALUES
(101,3, '2026-05-24 09:00:00', '2026-05-24 10:00:00', 1, 1, '2026-05-23 09:11:29', '台北市信義區', 0, 1, 1200, 'anxiety', '最近很容易焦慮', 0, NULL, NULL, '服務非常的細心專業，所有的過程都非常的暖心！', '2026-06-18 15:22:33'),
(102,4, '2026-05-28 11:00:00', '2026-05-28 12:00:00', 2, 2, '2026-05-23 15:38:22', '新北市板橋區', 1, 0, 1500, 'stress', '考試壓力大', 1, '初次諮商，建議先建立情緒紀錄', NULL, NULL, NULL),
(103,2, '2026-05-27 17:00:00', '2026-05-27 18:00:00', 3, 3, '2026-05-23 14:03:12', '台中市西區', 4, 1, 1800, 'relationship', '人際互動常常卡住', 2, '個案表達較多家庭與人際壓力', 4, '整體體驗不錯，諮商師很有耐心', '2026-05-27 23:12:27'),
(104,1, '2026-05-30 13:00:00', '2026-05-30 14:00:00', 4, 4, '2026-05-23 16:45:24', '桃園市中壢區', 2, 0, 1000, 'sleep', '最近失眠嚴重', 3, '個案有錯誤的用藥習慣，後續應持續追蹤', 5, '真的很感謝醫師的幫助，長期失眠的問題有慢慢在改善，也很期待自己的進步。', '2026-06-01 13:27:45');


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
	member_id, 
	order_id,
	admin_id,
	issue_desc,
	report_date,
	report_status,
	report_note
) VALUES
(1, 101, NULL, '心理師未準時上線', '2026-05-23 09:30:00', 0, NULL),
(2, 102, 1, '預約時間顯示錯誤', '2026-05-23 10:00:00', 1, '已通知客服'),
(3, 103, NULL, '付款後訂單未更新', '2026-05-23 11:20:00', 2, '已手動修正'),
(4, 104, 2, '無法查看會談紀錄', '2026-05-23 14:10:00', 0, NULL);

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