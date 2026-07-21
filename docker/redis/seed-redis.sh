#!/bin/sh
# ==========================================================
# Redis 假資料：文章熱門度排行 (Sorted Set: hot:alltime)
#
# 用途：讓「熱門文章」在全新環境（VM / docker compose down -v 之後）
#      也有資料可顯示。ZADD 為冪等操作，重複執行只會覆寫分數，無副作用。
#
# 兩種執行方式：
#   1) 由 docker-compose 的 redis-seed 服務自動執行（REDIS_HOST=redis）
#   2) 手動：docker compose exec redis sh /seed.sh   （走預設 127.0.0.1）
# ==========================================================
set -e

REDIS_HOST="${REDIS_HOST:-127.0.0.1}"
REDIS_PORT="${REDIS_PORT:-6379}"

# 等 Redis 就緒（最多 30 秒）：depends_on 只保證容器啟動，不保證能接受連線
i=0
until redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" ping > /dev/null 2>&1; do
  i=$((i + 1))
  if [ "$i" -ge 30 ]; then
    echo "Redis ($REDIS_HOST:$REDIS_PORT) 在 30 秒內未就緒，放棄灌入假資料"
    exit 1
  fi
  sleep 1
done

# 一次寫入 15 筆（ZADD 支援多組 score member）
redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" ZADD hot:alltime \
  1875 1 \
  1580 2 \
  1950 3 \
  1120 4 \
  2240 5 \
  980 6 \
  1430 7 \
  760 8 \
  1690 9 \
  1240 10 \
  890 11 \
  1520 12 \
  1080 13 \
  640 14 \
  1350 15

echo "Redis 假資料灌入完成：hot:alltime（15 筆）"
