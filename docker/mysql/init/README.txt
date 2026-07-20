# 1. 進專案資料夾
cd CKA101G1-FreeMind

# 2. 抓最新程式碼
git pull

# 3. 停掉舊容器（避免名字衝突），資料保留
docker compose down

# 4. 重建映像 + 重新啟動
docker compose up -d --build

# 5. 確認狀態 / 看 log
docker compose ps
docker compose logs -f app        # Ctrl+C 離開


VM 重新開機後（手動）

cd CKA101G1-FreeMind
docker compose up -d        # 注意：不用 --build，映像和資料都還在

docker compose ps
docker compose logs -f app