package com.freemind.article.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.article.dto.ArticleWithStatsDTO;
import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleBookmark;
import com.freemind.article.entity.ArticleBookmarkId;
import com.freemind.article.entity.ArticleLike;
import com.freemind.article.entity.ArticleLikeId;
import com.freemind.article.entity.ArticleViewHistory;
import com.freemind.article.repository.ArticleBookmarkRepository;
import com.freemind.article.repository.ArticleLikeRepository;
import com.freemind.article.repository.ArticleRepository;
import com.freemind.article.repository.ArticleViewHistoryRepository;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberRepository;
import com.freemind.login.member.model.MemberService;


@Service
public class ArticleInteractionServiceImpl implements ArticleInteractionService{
	
	@Value("${app.article.page-size}")
	private int artPageSize;
	
	@Value("${app.hot-score.weight.like:3.0}")
	private double weightLike;
	
    @Value("${app.hot-score.weight.bookmark:3.0}")
	private double weightBookmark;
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private ArticleViewService articleViewService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private ArticleRepository articleRepository;
	
	@Autowired
    private ArticleLikeRepository articleLikeRepository;
	
	@Autowired
    private ArticleBookmarkRepository articleBookmarkRepository;
	
	@Autowired
	private ArticleViewHistoryRepository articleViewHistoryRepository;
	
	@Autowired
	private MemberRepository memberRepository;

	@Override
	@Transactional
	public void toggleLike(Integer articleId, Integer memberId) {
		ArticleLikeId likeId = new ArticleLikeId(articleId, memberId);
		
		if (articleLikeRepository.existsById(likeId)) {
			articleLikeRepository.deleteById(likeId);
			articleViewService.adjustHotScore(articleId, -weightLike);
		} else {
			Article article = articleRepository.getReferenceById(articleId);
			Member member = memberRepository.getReferenceById(memberId);
			
			ArticleLike articleLike = new ArticleLike(article, member, LocalDateTime.now());
			articleLikeRepository.save(articleLike);
			articleViewService.adjustHotScore(articleId, weightLike);
		}
	}
	
	@Override
	@Transactional
	public void toggleBookmark(Integer articleId, Integer memberId) {
		ArticleBookmarkId bookmarkId = new ArticleBookmarkId(articleId, memberId);
		
		if (articleBookmarkRepository.existsById(bookmarkId)) {
			articleBookmarkRepository.deleteById(bookmarkId);
			articleViewService.adjustHotScore(articleId, -weightBookmark);
		} else {
			Article article = articleRepository.getReferenceById(articleId);
			Member member = memberRepository.getReferenceById(memberId);
			
			ArticleBookmark articleBookmark = new ArticleBookmark(article, member, LocalDateTime.now());
			articleBookmarkRepository.save(articleBookmark);
			articleViewService.adjustHotScore(articleId, weightBookmark);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean isLikedByMember(Integer articleId, Integer memberId) {
		if (memberId == null) return false;
		
		return articleLikeRepository.existsById(new ArticleLikeId(articleId, memberId));
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean isSavedByMember(Integer articleId, Integer memberId) {
		if (memberId == null) return false;
		
		return articleBookmarkRepository.existsById(new ArticleBookmarkId(articleId, memberId));
	}

	@Override
	@Transactional(readOnly = true)
	public long getLikeCount(Integer articleId) {
		return articleLikeRepository.countByArticleId(articleId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long getBookmarkCount(Integer articleId) {
		return articleBookmarkRepository.countByArticleId(articleId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public ArticleWithStatsDTO getArticleStatistics(Article article) {
		return ArticleWithStatsDTO.builder()
				.article(article)
				.viewCount(article.getViewCount())
				.likeCount(getLikeCount(article.getArticleId()))
				.bookmarkCount(getBookmarkCount(article.getArticleId()))
				.shareCount(article.getShareCount())
				.build();
	}

	@Override
	public Page<Article> getLikedArticles(Integer memberId, Integer page) {
		Pageable pageable = PageRequest.of(page - 1, artPageSize, Sort.by("likedAt").descending());
		Page<ArticleLike> likePage = articleLikeRepository.findByMemberId(memberId, pageable);
		
		return likePage.map(ArticleLike::getArticle);
	}

	@Override
	public Page<Article> getSavedArticles(Integer memberId, Integer page) {
	    Pageable pageable = PageRequest.of(page - 1, artPageSize, Sort.by("savedAt").descending());
	    Page<ArticleBookmark> bookmarkPage = articleBookmarkRepository.findByMemberId(memberId, pageable);
		
		return bookmarkPage.map(ArticleBookmark::getArticle);
	}

	@Override
	public Page<Article> getViewHistory(Integer memberId, Integer page) {
		Pageable pageable = PageRequest.of(page - 1, artPageSize, Sort.by("viewedAt").descending());
		Page<ArticleViewHistory> historyPage = articleViewHistoryRepository.findByMemberId(memberId, pageable);
		
		return historyPage.map(ArticleViewHistory::getArticle);
	}

	@Override
	@Transactional
	public void recordView(Integer articleId, Integer memberId) {
		 Optional<ArticleViewHistory> history = articleViewHistoryRepository.findByMemberIdAndArticleId(memberId, articleId);
	
		 if (history.isPresent()) {
			 history.get().setViewedAt(LocalDateTime.now());
		 } else {
			 Article article = articleService.getPublishedArticle(articleId);
			 Member member = memberService.getOneMember(memberId);
			 ArticleViewHistory newHistory = new ArticleViewHistory(article, member, LocalDateTime.now());
			 articleViewHistoryRepository.save(newHistory);
		 }
	}

}
