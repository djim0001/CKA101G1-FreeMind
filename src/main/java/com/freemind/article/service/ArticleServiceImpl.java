package com.freemind.article.service;

import static com.freemind.util.Constants.ART_PAGE_SIZE;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.freemind.article.dto.ArticleCreateForm;
import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.repository.ArticleCatRepository;
import com.freemind.article.repository.ArticleRepository;

import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.repository.PsychologistRepository;

import com.freemind.article.exception.ArticleValidationException;


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
		
		Article article = createArticleWithForm(form, psychId);
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
		
		Article article = createArticleWithForm(form, psychId);
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
	
	@Override
	public Page<Article> getPublishedArticles(Integer page, Integer catId) {
		Pageable pageable = PageRequest.of(page - 1, ART_PAGE_SIZE);
		
		if (catId != null ) {
			return articleRepository.findByStatusAndCatId(2, catId, pageable);
		}
		return articleRepository.findByStatus(2, pageable);
	}

	@Override
	public Article getArticle(Integer articleId, Integer psychId) {
		Article article = articleRepository.findById(articleId).orElse(null);
		
		if (article == null) return null;
		
		int status = article.getArticleStatus();
		
		if (status == 2 || psychId != null && psychId.equals(article.getPsychologist().getPsychId())) {
			return article;
		}	
		
		return null;
	}
	
	@Override
	public Article getPublishedArticle(Integer articleId) {
		return getArticle(articleId, null);
	}
	
	@Override
	public Article getEditableArticle(Integer articleId, Integer psychId) {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new IllegalArgumentException("查無此文章"));
		
		if (!article.getPsychologist().getPsychId().equals(psychId)) {
			throw new IllegalStateException("無權編輯此文章");
		}
		
		if (article.getArticleStatus() != 0 && article.getArticleStatus() != 3) {
			throw new IllegalStateException("此文章狀態無法編輯");
		}
		
		return article;
	}

	@Override
	public Article updateDraft(Integer articleId, ArticleCreateForm form, Integer psychId) {
		Article article = getEditableArticle(articleId, psychId);
		updateArticleWithForm(article, form);
		article.setArticleStatus(0);
		return articleRepository.save(article);
	}
	
	@Override
	public Article updateAndSubmit(Integer articleId, ArticleCreateForm form, Integer psychId) {
		Article article = getEditableArticle(articleId, psychId);
		
		boolean hasCoverImage = (form.getCoverImageFile() != null && !form.getCoverImageFile().isEmpty())
								|| article.getCoverImage() != null;
		validateSubmission(form.getArticleCatId(), form.getTitle(), form.getContent(), hasCoverImage);
		
		updateArticleWithForm(article, form);
		article.setArticleStatus(1);
		article.setSubmittedAt(article.getUpdatedAt());
		return articleRepository.save(article);
		
	}

	private Article createArticleWithForm(ArticleCreateForm form, Integer psychId) {
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
		Map<String, String> errors = new LinkedHashMap<>();
		
		if (articleCatId == null) {
			errors.put("catError", "請選擇文章分類");
		}
		
		if (title == null || title.isBlank()) {
			errors.put("titleError", "請輸入標題");
		}
		
		if (content == null || content.isBlank()) {
			errors.put("contentError", "請輸入內文");
		}
		
		if (!hasCoverImage) {
			errors.put("coverError", "請上傳首圖");
		}
		
		if (!errors.isEmpty()) {
	        throw new ArticleValidationException(errors);
	    }
	}

	private void updateArticleWithForm(Article article, ArticleCreateForm form) {
		article.setTitle(form.getTitle());
		article.setContent(form.getContent());
		
		if (form.getArticleCatId() != null) {
			article.setArticleCat(articleCatRepository.findById(form.getArticleCatId())
					.orElseThrow(() -> new IllegalArgumentException("查無此分類")));
		}
		
		if (form.getCoverImageFile() != null && !form.getCoverImageFile().isEmpty()) {
			try {
				article.setCoverImage(form.getCoverImageFile().getBytes());
			} catch (IOException e) {
				throw new RuntimeException("首圖讀取失敗", e);
			}
		}
		
		article.setUpdatedAt(LocalDateTime.now());
	}

}
