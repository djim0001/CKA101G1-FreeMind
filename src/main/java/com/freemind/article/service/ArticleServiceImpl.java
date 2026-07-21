package com.freemind.article.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.freemind.article.dto.ArticleCreateForm;
import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.repository.ArticleCatRepository;
import com.freemind.article.repository.ArticleRepository;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminRepository;
import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.repository.PsychologistRepository;
import com.freemind.util.ImageUploadValidator;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import com.freemind.article.exception.ArticleValidationException;


@Service
public class ArticleServiceImpl implements ArticleService{
	
	@Value("${app.article.page-size}")
	private int artPageSize;
	
	@Value("${app.article.image.max-size}")
	private DataSize maxImageSize;
	
	@Value("${app.hot-score.weight.share:5.0}")
    private double weightShare;
	
	@Autowired
	private ArticleViewService articleViewService;
	
	@Autowired
	private ArticleRepository articleRepository;
	
	@Autowired
	private ArticleCatRepository articleCatRepository;
	
	@Autowired
	private PsychologistRepository psychologistRepository;
	
	@Autowired
	private AdminRepository adminRepository;

	
	@Override
	public Article createDraft(ArticleCreateForm form, Integer psychId) {
		if (psychId == null) {
			throw new IllegalArgumentException("心理師資訊未帶入，請確認登入狀態");
		}
		
		validateTitleLength(form.getTitle());
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
		return articleRepository.findArticlesByPsychId(psychId);
	}

	@Override
	public Page<Article> getMyArticles(Integer psychId, Integer catId, Integer status, String sort, Integer page) {
		if (psychId == null) {
			throw new IllegalArgumentException("心理師資訊未帶入，請確認登入狀態");
		}
		
		Sort sortBy = "oldest".equals(sort) ? Sort.by("createdAt").ascending() : Sort.by("createdAt").descending();
		Pageable pageable = PageRequest.of(page - 1, artPageSize, sortBy);
		
		return articleRepository.findMyArticlesWithFilters(psychId, catId, status, pageable);
	}
	
	@Override
	public Page<Article> getPublishedArticles(Integer catId, String keyword, 
			   								  LocalDate dateFrom, LocalDate dateTo, 
											  Integer page, String sort, Integer excludeId) {
		Sort orderBy = "oldest".equals(sort) ? Sort.by("publishedAt").ascending() : Sort.by("publishedAt").descending();
		Pageable pageable = PageRequest.of(page - 1, artPageSize, orderBy);

		String kw = (keyword != null && !keyword.isBlank()) ? keyword : null;
	    LocalDateTime from = (dateFrom != null) ? dateFrom.atStartOfDay() : null;
	    LocalDateTime to   = (dateTo != null)   ? dateTo.plusDays(1).atStartOfDay() : null;

	    return articleRepository.searchArticles(2, excludeId, catId, kw, from, to, pageable);
	}
	
	@Override
	public Article getArticle(Integer articleId, Integer psychId) {
		Article article = articleRepository.findById(articleId).orElse(null);
		
		if (article == null) return null;
		
		int status = article.getArticleStatus();
		// 已發布文章 || 心理師本人查看所有狀態的文章
		if (status == 2 || psychId != null && psychId.equals(article.getPsychologist().getPsychId())) {
			return article;
		}	
		
		return null;
	}
	
	@Override
	public Article getArticleForAdmin(Integer articleId) {
		Article article = articleRepository.findById(articleId).orElse(null);
		
		if (article == null) return null;
		// 後台不查看草稿
		if (article.getArticleStatus() == 0) return null;
		
		return article;
	}
	
	@Override
	public Article getPublishedArticle(Integer articleId) {
		return getArticle(articleId, null);
	}
	
	@Override
	public List<Article> getPublishedArticlesByIds(List<Integer> articleIds) {
		List<Article> result = new ArrayList<>();
		
		if (articleIds == null || articleIds.isEmpty()) return result;
		
		List<Article> articles = articleRepository.findAllById(articleIds);
		
		for (Integer id : articleIds) {
			for (Article article : articles) {
				if (article.getArticleId().equals(id) && article.getArticleStatus() == 2) {
					result.add(article);
					break;
				}
			}
		}
		
		return result;
	}
	
	@Override
	public Article getEditableArticle(Integer articleId, Integer psychId) {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new IllegalArgumentException("查無此文章"));
		
		if (!article.getPsychologist().getPsychId().equals(psychId)) {
			throw new IllegalStateException("無權編輯此文章");
		}
		
		if (article.getArticleStatus() != 0 && article.getArticleStatus() != 2 && article.getArticleStatus() != 3) {
			throw new IllegalStateException("此文章狀態無法編輯");
		}
		
		return article;
	}
	
	@Override
	public Article createEditCopy(Integer articleId, Integer psychId) {
		Article article = getEditableArticle(articleId, psychId);
		
		if (article.getArticleStatus() != 2) {
			throw new IllegalStateException("此文章狀態無法編輯");
		}
		
		// 已發布文章, 建立可編輯的副本
		
		List<Integer> statuses = new ArrayList<>();
		statuses.add(0); // 副本狀態: 編輯中
		statuses.add(1); // 副本狀態: 送審中
		
		Article existingCopy = articleRepository.findEditCopy(articleId, statuses);
		if (existingCopy != null) {
			return existingCopy;
		}
		
		Article copy = new Article();
		copy.setParentArticleId(article.getArticleId());
		copy.setArticleCat(article.getArticleCat());
		copy.setPsychologist(article.getPsychologist());
		copy.setCoverImage(article.getCoverImage());
		copy.setTitle(article.getTitle());
		copy.setContent(article.getContent());
		copy.setArticleStatus(0);
		copy.setCreatedAt(LocalDateTime.now());
		
		return articleRepository.save(copy);
	}

	@Override
	public Article updateDraft(Integer articleId, ArticleCreateForm form, Integer psychId) {
		Article article = getEditableArticle(articleId, psychId);
		validateTitleLength(form.getTitle());
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
			validateCoverImageSize(form.getCoverImageFile());
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
	
	private void updateArticleWithForm(Article article, ArticleCreateForm form) {
		article.setTitle(form.getTitle());
		article.setContent(form.getContent());
		
		if (form.getArticleCatId() != null) {
			article.setArticleCat(articleCatRepository.findById(form.getArticleCatId())
					.orElseThrow(() -> new IllegalArgumentException("查無此分類")));
		}
		
		if (form.getCoverImageFile() != null && !form.getCoverImageFile().isEmpty()) {
			validateCoverImageSize(form.getCoverImageFile());
			try {
				article.setCoverImage(form.getCoverImageFile().getBytes());
			} catch (IOException e) {
				throw new RuntimeException("首圖讀取失敗", e);
			}
		}
		
		article.setUpdatedAt(LocalDateTime.now());
	}
	
	private void validateTitleLength(String title) {
	    if (title != null && title.length() > 50) {
	    	Map<String, String> errors = new LinkedHashMap<>();
	        errors.put("titleError", "標題不可超過 50 字");
	        throw new ArticleValidationException(errors);
	    }
	}
	
	private void validateCoverImageSize(MultipartFile file) {
		try {
			ImageUploadValidator.validateImageSize(file, maxImageSize);
		} catch (IllegalArgumentException e) {
			Map<String, String> errors = new LinkedHashMap<>();
			errors.put("coverError", e.getMessage());
			throw new ArticleValidationException(errors);
		}
	}
	
	private void validateSubmission(Integer articleCatId, String title, String content, boolean hasCoverImage) {
		Map<String, String> errors = new LinkedHashMap<>();
		
		if (articleCatId == null) {
			errors.put("catError", "請選擇文章分類");
		}
		
		if (title == null || title.isBlank()) {
			errors.put("titleError", "請輸入標題");
		} else if (title.length() > 50) {
	        errors.put("titleError", "標題不可超過 50 字");
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

	@Override
	public void deleteDraft(Integer articleId, Integer psychId) {
		Article article = getEditableArticle(articleId, psychId);
		
		int status = article.getArticleStatus();
		if (status != 0 && status != 3) {
			throw new IllegalStateException("此文章狀態無法刪除");
		}
		
		articleRepository.delete(article);
	}
	
	@Override
	@Transactional
	public void batchDeleteDrafts(List<Integer> articleIds, Integer psychId) {
	    for (Integer articleId : articleIds) {
	        Article article = articleRepository.findById(articleId)
	                .orElseThrow(() -> new IllegalArgumentException("文章不存在: " + articleId));

	        if (!article.getPsychologist().getPsychId().equals(psychId)) {
	            throw new IllegalArgumentException("無權限刪除文章: " + articleId);
	        }

	        int status = article.getArticleStatus();
	        if (status != 0 && status != 3) {
	            throw new IllegalStateException("此文章狀態無法刪除: " + article.getTitle());
	        }

	        articleRepository.delete(article);
	    }
	}

	@Override
	public void unPublishMyArticle(Integer articleId, Integer psychId) {
		Article article = getPublishedArticle(articleId);
		
		if (!article.getPsychologist().getPsychId().equals(psychId)) {
	        throw new IllegalArgumentException("無權操作此文章");
	    }
		
		article.setArticleStatus(4);
		articleRepository.save(article);
	}
	
	@Override
	public Page<Article> getSubmittedArticles(String keyword, Integer catId, Integer status, String sort, Integer page) {
		Sort sortBy = "oldest".equals(sort) ? Sort.by("submittedAt").ascending() : Sort.by("submittedAt").descending();
		Pageable pageable = PageRequest.of(page - 1, artPageSize, sortBy);
		
		List<Integer> articleStatuses = List.of(1, 2, 3, 4);
		String kw = (keyword != null && !keyword.isBlank()) ? keyword : null;
		
		return articleRepository.findSubmittedArticlesWithFilters(articleStatuses,  catId, kw, status, pageable);
	}
	
	@Override
	public Page<Article> getPendingArticles(Integer page) {
		Pageable pageable = PageRequest.of(page - 1, artPageSize, Sort.by("submittedAt").ascending());
		return articleRepository.findByStatus(1, pageable);
	}
	
	@Override
	public Article getArticleForReview(Integer articleId) {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new IllegalArgumentException("查無此文章"));
		
		if (article.getArticleStatus() != 1) {
			throw new IllegalStateException("此文章不在審核中");
		}
		
		return article;
	}
	
	@Transactional
	@Override
	public void approveArticle(Integer articleId, Integer adminId) {
		Article article = getArticleForReview(articleId);
		
		Admin admin = adminRepository.findById(adminId)
        		.orElseThrow(() -> new IllegalArgumentException("查無此員工"));
		
		// 副本覆寫原文, 刪除副本
		if (article.getParentArticleId() != null) {
			Article original = articleRepository.findById(article.getParentArticleId())
					.orElseThrow(() -> new IllegalArgumentException("查無此文章"));
		
			article.setAdmin(admin);
			original.setArticleCat(article.getArticleCat());
			original.setCoverImage(article.getCoverImage());
			original.setTitle(article.getTitle());
			original.setContent(article.getContent());
			original.setReviewedAt(LocalDateTime.now());
	        original.setPublishedAt(LocalDateTime.now());
	        articleRepository.save(original);
	        articleRepository.delete(article);
		} else {
			article.setAdmin(admin);
			article.setArticleStatus(2);
			article.setReviewedAt(LocalDateTime.now());
			article.setPublishedAt(LocalDateTime.now());
			articleRepository.save(article);
		}
	}

	@Override
	public void rejectArticle(Integer articleId, Integer adminId, Integer rejectReason, String rejectNote) {
		Article article = getArticleForReview(articleId);
		
		Admin admin = adminRepository.findById(adminId)
				.orElseThrow(() -> new IllegalArgumentException("查無此員工"));
		
		if (rejectReason == null) {
	        throw new IllegalArgumentException("請選擇退回原因");
	    }
		
		if (rejectReason == 4 && (rejectNote == null || rejectNote.isBlank())) {
			throw new IllegalArgumentException("退回說明，選擇「其他」時必填");
		}
		
		article.setAdmin(admin);
		article.setArticleStatus(3);
		article.setReviewedAt(LocalDateTime.now());
		article.setRejectReason(rejectReason);
		
		if (rejectNote != null && rejectNote.length() >200) {
		    throw new IllegalArgumentException("退回說明不可超過200字");
		}
		
		article.setRejectNote(rejectNote);
		articleRepository.save(article);
	}

	@Override
	public void unPublishArticle(Integer articleId, Integer adminId) {
		Article article = getPublishedArticle(articleId);
		
		Admin admin = adminRepository.findById(adminId)
				.orElseThrow(() -> new IllegalArgumentException("查無此員工"));
		
		article.setAdmin(admin);
	    article.setArticleStatus(4);
		articleRepository.save(article);
	}

	@Override
	public Page<Article> getReviewedArticles(Integer status, Integer page) {
		Pageable pageable = PageRequest.of(page - 1, artPageSize, Sort.by("reviewedAt").descending());
		List<Integer> statuses;
		
		 if (status != null) {
			 statuses = new ArrayList<>();
			 statuses.add(status);
		 } else {
			 statuses = new ArrayList<>();
			 statuses.add(2);
			 statuses.add(3);
			 statuses.add(4);
		 }
		
		return articleRepository.findByStatuses(statuses, pageable);
	}

	@Override
	@Transactional
	public long incrementAndGetShareCount(Integer articleId) {
		articleRepository.incrementShareCount(articleId);
		
		Article article = articleRepository.findById(articleId).orElse(null);

		if (article == null) {
		    return 0;
		} else {
			articleViewService.adjustHotScore(articleId, weightShare);
			return (long)article.getShareCount();
		}

	}

}
