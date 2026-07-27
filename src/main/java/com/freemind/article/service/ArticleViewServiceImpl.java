package com.freemind.article.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.article.repository.ArticleRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ArticleViewServiceImpl implements ArticleViewService{

	@Value("${app.hot-score.weight.view:1.0}")
	private double weightView;
	
	@Value("${app.hot-score.weight.like:3.0}")
	private double weightLike;
	
	@Value("${app.hot-score.weight.bookmark:3.0}")
	private double weightBookmark;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Autowired
	private ArticleRepository articleRepository;
	
	private static final Duration DEDUP_TTL = Duration.ofHours(24); // De-duplication Time To Live 
	private static final String HOT_KEY = "hot:alltime";
	private static final String HOT_DIRTY_KEY = "hot:dirty";

	
	@Override
	public boolean recordViewCount(Integer articleId, String visitorKey) {
		String dedupKey = "view:dedup:" + articleId + ":" + visitorKey;
		                                         
		Boolean isNew = redisTemplate.opsForValue().setIfAbsent(dedupKey, "1", DEDUP_TTL); // SET NX EX
		
		if (Boolean.TRUE.equals(isNew)) {
			redisTemplate.opsForValue().increment("view:pending:" + articleId); // INCR, the counts
			redisTemplate.opsForSet().add("view:dirty_articles", articleId.toString()); // SADD, the articles to be updated
			adjustHotScore(articleId, weightView);
			return true;
		}
		
		return false;
	}

	@Override
	@Scheduled(fixedRateString = "${view.sync.fixed-rate:120000}") // 2min
    @Transactional
	public void syncCountAndScoreToDb() {
		Set<String> viewDirtyIds = redisTemplate.opsForSet().members("view:dirty_articles"); // SMEMBERS
		Set<String> hotDirtyIds = redisTemplate.opsForSet().members(HOT_DIRTY_KEY);
		Set<String> dirtyIds = Stream.concat(viewDirtyIds.stream(), hotDirtyIds.stream())
								     .collect(Collectors.toSet());


		if (dirtyIds == null || dirtyIds.isEmpty()) return;
		
		for (String idStr : dirtyIds) {
			String viewKey = "view:pending:" + idStr;
//			String viewCount = redisTemplate.opsForValue().getAndDelete(pendingKey); // GETDEL(Redis 6.2.0)
			String viewCount = redisTemplate.opsForValue().get(viewKey);
			if (viewCount != null) {
			    redisTemplate.delete(viewKey);
			}
			
		    Double hotScore = redisTemplate.opsForZSet().score(HOT_KEY, idStr); // ZSCORE
			    
			if (hotScore != null) {
				long count = (viewCount != null) ? Long.parseLong(viewCount) : 0L;
				articleRepository.updateViewCountAndHotScore(Integer.valueOf(idStr), count, hotScore);
			}
		    
			redisTemplate.opsForSet().remove("view:dirty_articles", idStr); // SREM
			redisTemplate.opsForSet().remove(HOT_DIRTY_KEY, idStr);
		}
			
		
		
		log.info("Completed sync of {} articles.", dirtyIds.size());
	}

	@Override
	public List<Integer> getHotArticleIds(int topN) {
		Set<String> topArticleIds = redisTemplate.opsForZSet().reverseRange(HOT_KEY, 0, topN - 1); // ZREVRANGE

		List<Integer> result = new ArrayList<>();

		if (topArticleIds == null) return result;

		for (String id : topArticleIds) {
			int articleId = Integer.parseInt(id);
			result.add(articleId);
		}

		return result;
	}

	@Override
	public void adjustHotScore(Integer articleId, double delta) {
		redisTemplate.opsForZSet().incrementScore(HOT_KEY, articleId.toString(), delta); // ZINCRBY
		redisTemplate.opsForSet().add(HOT_DIRTY_KEY, articleId.toString());	// SADD, the articleIds to be updated
		
	}
	
}
