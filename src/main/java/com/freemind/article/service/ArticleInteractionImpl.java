package com.freemind.article.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleLike;
import com.freemind.article.entity.ArticleLikeId;
import com.freemind.article.repository.ArticleBookmarkRepository;
import com.freemind.article.repository.ArticleLikeRepository;
import com.freemind.article.repository.ArticleRepository;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberRepository;


@Service
public class ArticleInteractionImpl implements ArticleInteractionService{
	
	@Autowired
	private ArticleRepository articleRepository;
	
	@Autowired
    private ArticleLikeRepository articleLikeRepository;
	
	@Autowired
    private ArticleBookmarkRepository articleBookmarkRepository;
	
	@Autowired
	private MemberRepository memberRepository;

	@Override
	@Transactional
	public void toggleLike(Integer articleId, Integer memberId) {
		ArticleLikeId likeId = new ArticleLikeId(articleId, memberId);
		
		if (articleLikeRepository.existsById(likeId)) {
			articleLikeRepository.deleteById(likeId);
		} else {
			Article article = articleRepository.getReferenceById(articleId);
			Member member = memberRepository.getReferenceById(memberId);
			articleLikeRepository.save(new ArticleLike(article, member, LocalDateTime.now()));
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
	public long getLikeCount(Integer articleId) {
		return articleLikeRepository.countByArticleId(articleId);
	}

}
