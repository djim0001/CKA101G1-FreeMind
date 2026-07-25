# CKA101G1 諮由自在 Free Mind 整合性諮商平台 
## 專案簡介
本專案發想旨在解決當前心理諮商領域所面臨的三大現況與挑戰：
1. 現代社會心理壓力上升，求助門檻過高：透過簡化流程，降低求助門檻，讓需要幫助的人能更輕鬆、無負擔地踏出第一步。
2. 傳統預約流程過於繁瑣，等待時間過長：整合預約流程，將預約步驟數位化與系統化，提升預約效率與使用者體驗。
3. 心理健康資源過於分散，民眾難以精準尋得：打造整合式平台，集結專業諮商預約、心理師專欄文章、線上課程及揪團活動等多元服務，以滿足使用者多樣化的心理健康需求。

## 使用技術
![Java](https://img.shields.io/badge/-Java-007396?logo=java&logoColor=white&style=flat-square)
![Spring Boot](https://img.shields.io/badge/-Spring_Boot-6DB33F?logo=spring-boot&logoColor=white&style=flat-square)
![Spring MVC](https://img.shields.io/badge/-Spring_MVC-6DB33F?logo=spring&logoColor=white&style=flat-square)
![JPA](https://img.shields.io/badge/-JPA-59666C?logo=hibernate&logoColor=white&style=flat-square)
![Hibernate](https://img.shields.io/badge/-Hibernate-59666C?logo=hibernate&logoColor=white&style=flat-square)
![MySQL](https://img.shields.io/badge/-MySQL-4479A1?logo=mysql&logoColor=white&style=flat-square)
![Redis](https://img.shields.io/badge/-Redis-DC382D?logo=redis&logoColor=white&style=flat-square)
![Maven](https://img.shields.io/badge/-Maven-C71A36?logo=apachemaven&logoColor=white&style=flat-square)
![Thymeleaf](https://img.shields.io/badge/-Thymeleaf-005F0F?logo=thymeleaf&logoColor=white&style=flat-square)
![HTML](https://img.shields.io/badge/-HTML-E34F26?logo=html&logoColor=white&style=flat-square)
![CSS](https://img.shields.io/badge/-CSS-1572B6?logo=css&logoColor=white&style=flat-square)
![JavaScript](https://img.shields.io/badge/-JavaScript-F7DF1E?logo=javascript&logoColor=black&style=flat-square)
![AJAX](https://img.shields.io/badge/-AJAX-0081CB?logo=jquery&logoColor=white&style=flat-square)
![Fetch](https://img.shields.io/badge/-Fetch-000000?logo=javascript&logoColor=white&style=flat-square)
![Bootstrap](https://img.shields.io/badge/-Bootstrap-7952B3?logo=bootstrap&logoColor=white&style=flat-square)
![Spring Security](https://img.shields.io/badge/-Spring_Security-6DB33F?logo=springsecurity&logoColor=white&style=flat-square)
![WebSocket](https://img.shields.io/badge/-WebSocket-010101?logo=socketdotio&logoColor=white&style=flat-square)
![Lombok](https://img.shields.io/badge/-Lombok-BC4521?style=flat-square)
![Google Identity Services](https://img.shields.io/badge/-Google_Identity_Services-4285F4?logo=google&logoColor=white&style=flat-square)

## 本地端啟動說明

### 事前準備
- JDK 17
- Maven（或使用專案內建的 `mvnw` / `mvnw.cmd`）
- MySQL 8（本機需先建立資料庫）
- Redis（本機需啟動 Redis 服務，預設 port 6379）

### 建置步驟
1. Clone 專案並切換到專案根目錄。
2. 於 MySQL 建立資料庫（預設資料庫名稱為 `dbtest`，可依需求調整）。
3. 設定資料庫連線環境變數（若不設定則使用預設值 `localhost:3306`、帳號 `root`、密碼 `password`）：
   - `DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USER`、`DB_PASSWORD`
4. 確認 Redis 已啟動（若非本機預設 port，可透過 `REDIS_HOST`、`REDIS_PORT` 環境變數調整）。
5. 於專案根目錄執行以下指令啟動專案：
   ```bash
   ./mvnw spring-boot:run
   ```
   或先打包再執行：
   ```bash
   ./mvnw clean package
   java -jar target/*.war
   ```
6. 啟動後預設可透過 `http://localhost:8080` 存取。

### 使用 Docker 建置，取得假資料
專案已提供 [docker-compose.yml](docker-compose.yml)，會一併啟動 app、MySQL、Redis，並自動載入假資料：
1. 安裝 Docker Desktop
2. 於專案根目錄執行：
   ```bash
   docker compose up -d --build
   ```
3. `mysql` 容器啟動時會自動執行 [docker/mysql/init](docker/mysql/init) 內的 SQL 腳本（含 [docker/mysql/covers](docker/mysql/covers) ，完成資料庫假資料建置；`redis-seed` 為一次性服務，負責執行 [docker/redis/seed-redis.sh](docker/redis/seed-redis.sh) 載入 Redis 假資料，執行完畢後容器會顯示 `Exited (0)`，屬正常現象。
   - 兩者（MySQL init + redis-seed）都要成功跑過，畫面上才會有完整假資料。
4. 確認狀態：
   ```bash
   docker compose ps
   docker compose logs -f app        # Ctrl+C 離開
   docker compose logs redis-seed    # 應看到「Redis 假資料載入完成：hot:alltime」
   ```
5. 啟動後可透過 `http://localhost:8080` 存取；MySQL 對外埠為 `3307`、Redis 對外埠為 `6380`（避免與本機既有服務衝突）。
6. 之後重開機／重啟，不需要 `--build`，資料與映像都會保留：
   ```bash
   docker compose up -d
   ```
7. 若要清空重灌（含 DB volume）：
   ```bash
   docker compose down -v
   docker compose up -d --build
   ```

### 補充說明
- 開發階段 Thymeleaf 快取已關閉（`spring.thymeleaf.cache=false`），修改頁面後可直接重新整理查看變更。
- 若需使用 Email OTP 或 Google 登入功能，請參考 [application.properties](src/main/resources/application.properties) 內的 `mail.otp.*`、`google.client-id` 設定，並依需求覆蓋對應環境變數。
- 上傳檔案（課程影片、文章內文圖片等）預設存放於專案根目錄下的 `uploads/` 資料夾；若使用 Docker 方式啟動，文章首圖 coverImage 等假資料圖檔是由 MySQL 容器的 init 腳本從 [docker/mysql/covers](docker/mysql/covers) 載入。

