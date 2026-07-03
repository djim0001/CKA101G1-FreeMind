package com.freemind.article.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.article.dto.ArticleCreateForm;
import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.repository.ArticleCatRepository;
import com.freemind.article.repository.ArticleRepository;
import com.freemind.login.psychologist.model.Psychologist;
import com.freemind.login.psychologist.model.PsychologistRepository;

@Service
public class ArticleServiceImpl implements ArticleService{
	
	@Autowired
	private ArticleRepository articleRepository;
	@Autowired
	private ArticleCatRepository articleCatRepository;
	@Autowired
	private PsychologistRepository psychologistRepository;

	@Override
	public Article createDraft(ArticleCreateForm form, Integer psychId) {
		if (psychId == null) {
			throw new IllegalArgumentException("心理師資訊未帶入，請確認登入狀態");
		}
		
		Article article = buildBaseArticle(form, psychId);
		article.setArticleStatus(0); // 建立草稿, 不檢查欄位
		return articleRepository.save(article);
	}

	@Override
	public Article createAndSubmit(ArticleCreateForm form, Integer psychId) {
		if (psychId == null) {
			throw new IllegalArgumentException("心理師資訊未帶入，請確認登入狀態");
		}
		// 送審, 先檢查欄位再建立
		boolean hasCoverImage = form.getCoverImageFile() != null && !form.getCoverImageFile().isEmpty();
		validateSubmission(form.getArticleCatId(), form.getTitle(), form.getContent(), hasCoverImage);
		
		Article article = buildBaseArticle(form, psychId);
		article.setArticleStatus(1);
		article.setSubmittedAt(article.getCreatedAt());
		return articleRepository.save(article);
	}

	@Override
	public Article submitExistingDraft(Integer articleId, Integer psychId) {
		if (articleId == null) {
			throw new IllegalArgumentException("文章ID不可為空");
		}
		
		if (psychId == null) {
			throw new IllegalArgumentException("心理師資訊未帶入，請確認登入狀態");
		}
		
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new IllegalArgumentException("文章不存在: " + articleId));
		if (article.getPsychologist() == null || !psychId.equals(article.getPsychologist().getPsychId()) ) {
			throw new IllegalArgumentException("無權限操作此文章");
		}
		
		if (article.getArticleStatus() != 0) {
			throw new IllegalArgumentException("此文章狀態不可送審");
		}
		
		Integer articleCatId = article.getArticleCat() != null ? article.getArticleCat().getArticleCatId() : null;
		boolean hasCoverImage = article.getCoverImage() != null;
		validateSubmission(articleCatId, article.getTitle(), article.getContent(), hasCoverImage);
		
		article.setArticleStatus(1);
		article.setSubmittedAt(LocalDateTime.now());
		return articleRepository.save(article);
	}

	@Override
	public List<Article> getMyArticles(Integer psychId) {
		if (psychId == null) {
			throw new IllegalArgumentException("心理師資訊未帶入，請確認登入狀態");
		}
		
		return articleRepository.findArticlesByPsychId(psychId);
	}
	
	private Article buildBaseArticle(ArticleCreateForm form, Integer psychId) {
		Article article = new Article();
		article.setTitle(form.getTitle());
		article.setContent(form.getContent());
		
		if (form.getCoverImageFile() != null && !form.getCoverImageFile().isEmpty()) {
			try {
				article.setCoverImage(form.getCoverImageFile().getBytes());
			} catch (IOException e) {
				throw new RuntimeException("首圖讀取失敗", e);
			}
		}
		
		article.setCreatedAt(LocalDateTime.now());
		
		if (form.getArticleCatId() != null) {
			ArticleCat articleCat = articleCatRepository.findById(form.getArticleCatId())
					.orElseThrow(() -> new IllegalArgumentException("文章分類不存在: " + form.getArticleCatId()));
			article.setArticleCat(articleCat);
		}
		
		Psychologist psych = psychologistRepository.findById(psychId)
				.orElseThrow(() -> new IllegalArgumentException("心理師不存在: " + psychId));
		article.setPsychologist(psych);
		
		return article;
	}
	
	private void validateSubmission(Integer articleCatId, String title, String content, boolean hasCoverImage) {
		if (articleCatId == null) {
			throw new IllegalArgumentException("請選擇文章分類");
		}
		
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("請輸入標題");
		}
		
		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("請輸入內文");
		}
		
		if (!hasCoverImage) {
			throw new IllegalArgumentException("請上傳首圖");
		}
	}

}
