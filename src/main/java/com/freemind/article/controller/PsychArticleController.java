package com.freemind.article.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.article.dto.ArticleCreateForm;
import com.freemind.article.dto.ArticleInteractionStatsDTO;
import com.freemind.article.dto.ArticleWithStatsDTO;
import com.freemind.article.dto.StatsSummaryDTO;
import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.exception.ArticleValidationException;
import com.freemind.article.service.ArticleCatService;
import com.freemind.article.service.ArticleInteractionService;
import com.freemind.article.service.ArticleService;
import com.freemind.login.security.psychologistsecurity.PsychUserDetails;
import com.freemind.util.ImageUploadValidator;


@Controller
@RequestMapping("/psych/article")
public class PsychArticleController {
	
	@Value("${article.upload.dir}")
	private String uploadDir;

	@Value("${article.upload.url-path}")
	private String urlPath;
	
	@Value("${app.article.image.max-size}")
	private DataSize maxImageSize;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private ArticleCatService articleCatService;
	
	@Autowired
	private ArticleInteractionService articleInteractionService;

	@ModelAttribute("articleCats")
	public List<ArticleCat> articleCatList() {
		return articleCatService.getActiveCats();
	}

	@GetMapping("/myArticles")
	public String myArticles(Model model, 
			@AuthenticationPrincipal PsychUserDetails prinPsychUser,
			@RequestParam(name = "page", defaultValue = "1") Integer page) {

		Integer psychId = prinPsychUser.getPsychologist().getPsychId();
		Page<Article> articlePage = articleService.getMyArticles(psychId, page);
		
		List<ArticleWithStatsDTO> articleList = new ArrayList<>();
		for (Article article : articlePage.getContent()) {
			ArticleInteractionStatsDTO stats = null;
			
			if (article.getArticleStatus() == 2 || article.getArticleStatus() == 4) {
				stats = articleInteractionService.getArticleStatistics(article);
			}
			
			ArticleWithStatsDTO dto = new ArticleWithStatsDTO(article, stats);
			articleList.add(dto);
		}
		
		model.addAttribute("articlePage", articlePage);
		model.addAttribute("currentPage", page);
		model.addAttribute("articleList", articleList);
		return "front-end/psych/article/myArticles";
	}
	
	
	@GetMapping("/myArticles/statsDashboard")
	public String statsDashboard(Model model,
								 @AuthenticationPrincipal PsychUserDetails prinPsychUser) {
		Integer psychId = prinPsychUser.getPsychologist().getPsychId();
		List<Article> articles = articleService.getMyArticles(psychId);
		long totalPublishedCount = 0L;
		long totalUnPublishedCount = 0L;
		long totalViewCount = 0L;
		long totalLikeCount = 0L;
		long totalBookmarkCount = 0L;
		long totalShareCount = 0L;
		
		for (Article article : articles) {
			int status = article.getArticleStatus();
			
			switch (status) {
			case 2:
				totalPublishedCount += 1;
				break;
			case 4:
				totalPublishedCount += 1;
				break;
			}
			
			if (status == 2 || status == 4) {
				totalViewCount += article.getViewCount();
				totalLikeCount += article.getLikeBaseCount() + articleInteractionService.getLikeCount(article.getArticleId());
				totalBookmarkCount += article.getBookmarkBaseCount() + articleInteractionService.getBookmarkCount(article.getArticleId());
				totalShareCount += article.getShareCount();
			}
		}
		
		StatsSummaryDTO summary = StatsSummaryDTO.builder()
										 .totalPublishedCount(totalPublishedCount)
										 .totalUnPublishedCount(totalUnPublishedCount)
										 .totalLikeCount(totalLikeCount)
										 .totalBookmarkCount(totalBookmarkCount)
										 .totalViewCount(totalViewCount)
										 .totalShareCount(totalShareCount)
										 .build();
		
		model.addAttribute("summary", summary);
		return "front-end/psych/article/statsDashboard";
	}
	
	@GetMapping("/create")
	public String getCreateForm(Model model) {

		model.addAttribute("form", new ArticleCreateForm());
		return "front-end/psych/article/createForm";
	}
	
	@GetMapping("/{articleId}/preview")
	public String previewArticle(Model model,
			@PathVariable Integer articleId,
			@AuthenticationPrincipal PsychUserDetails prinPsychUser) {
		
		Integer psychId = prinPsychUser.getPsychologist().getPsychId();
		Article article = articleService.getArticle(articleId, psychId);
		
		model.addAttribute("article", article);
		return "front-end/psych/article/previewArticle";
	}
	
	@PostMapping("/preview")
	public String previewFromForm(Model model,
			@ModelAttribute ArticleCreateForm form) {
		
		Article tempArticle = new Article();
		tempArticle.setTitle(form.getTitle());
		tempArticle.setContent(form.getContent());
		
		if (form.getArticleCatId() != null) {
			try {
				ArticleCat tempCat = articleCatService.getCatById(form.getArticleCatId());
				tempArticle.setArticleCat(tempCat);
			} catch (IllegalArgumentException e) {
				System.out.println("預覽時忽略即可：" + e.getMessage());
			}
		}
		
		if (form.getCoverImageFile() != null && !form.getCoverImageFile().isEmpty()) {
			try {
				tempArticle.setCoverImage(form.getCoverImageFile().getBytes());
			} catch (IOException e) {
				System.out.println("檔案讀取失敗, 預覽時忽略即可：" + e.getMessage());
			}
		}
		
		model.addAttribute("article", tempArticle);
		return "front-end/psych/article/previewArticle";
	}

	@PostMapping("/create")
	public String createArticle(Model model, 
			@ModelAttribute ArticleCreateForm form,
			@RequestParam("action") String action,
			@AuthenticationPrincipal PsychUserDetails prinPsychUser) {

		Integer psychId = prinPsychUser.getPsychologist().getPsychId();
		
		try {
			if ("submit".equals(action)) {
				articleService.createAndSubmit(form, psychId);
			} else {
				articleService.createDraft(form, psychId);
			}
		} catch (ArticleValidationException e) {
			model.addAllAttributes(e.getFieldErrors());
			model.addAttribute("form", form);
			return "front-end/psych/article/createForm";
		} catch (IllegalArgumentException | IllegalStateException e) {
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("form", form);
			return "front-end/psych/article/createForm";
		}

		return "redirect:/psych/article/myArticles";
	}
	
	@PostMapping("/{articleId}/submit")
	public String submitDraft(Model model, @PathVariable Integer articleId,
			@AuthenticationPrincipal PsychUserDetails prinPsychUser,
			RedirectAttributes redirectAttributes) {

		Integer psychId = prinPsychUser.getPsychologist().getPsychId();

		try {
			articleService.submitExistingDraft(articleId, psychId);
		} catch (ArticleValidationException e) {
			redirectAttributes.addFlashAttribute("alertMessage", "無法送審，請先編輯文章，填寫完整欄位");
			return "redirect:/psych/article/myArticles";
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
			return "redirect:/psych/article/myArticles";
		}

		return "redirect:/psych/article/myArticles";
	}

	@GetMapping("/{articleId}/edit")
	public String getEditForm(Model model, 
			@PathVariable Integer articleId,
			@RequestParam(name = "page", defaultValue = "1") Integer page,
			@AuthenticationPrincipal PsychUserDetails prinPsychUser,
			RedirectAttributes redirectAttributes) {

		Integer psychId = prinPsychUser.getPsychologist().getPsychId();

		// 可編輯: 0 草稿, 2 已發布, 3 審核未通過
		try {
			Article article = articleService.getEditableArticle(articleId, psychId);
			
			if (article.getArticleStatus() == 2) {
				Article copy = articleService.createEditCopy(articleId, psychId);
				return "redirect:/psych/article/" + copy.getArticleId() + "/edit?page=" + page;
			}
			
			// 把資料填回 form
			ArticleCreateForm form = new ArticleCreateForm();
			form.setTitle(article.getTitle());
			form.setContent(article.getContent());

			if (article.getArticleCat() != null) {
				form.setArticleCatId(article.getArticleCat().getArticleCatId());
			}

			model.addAttribute("form", form);
			model.addAttribute("articleId", articleId);
			model.addAttribute("currentPage", page);
			model.addAttribute("hasCoverImage", article.getCoverImage() != null);
			return "front-end/psych/article/editForm";
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
			return "redirect:/psych/article/myArticles?page=" + page;
		}

	}
	
	@PostMapping("/{articleId}/edit")
	public String updateArticle(Model model, 
			@ModelAttribute ArticleCreateForm form,
			@RequestParam("action") String action, 
			@RequestParam(name = "page", defaultValue = "1") Integer page,
			@PathVariable Integer articleId,
			@AuthenticationPrincipal PsychUserDetails prinPsychUser) {

		Integer psychId = prinPsychUser.getPsychologist().getPsychId();

		try {
			if ("submit".equals(action)) {
				articleService.updateAndSubmit(articleId, form, psychId);
			} else {
				articleService.updateDraft(articleId, form, psychId);
			}
		} catch (ArticleValidationException e) {
			model.addAllAttributes(e.getFieldErrors());
			model.addAttribute("form", form);
			model.addAttribute("articleId", articleId);
			model.addAttribute("currentPage", page);
			model.addAttribute("hasCoverImage", true);
			return "front-end/psych/article/editForm";
		} catch (IllegalArgumentException | IllegalStateException e) {
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("form", form);
			model.addAttribute("articleId", articleId);
			model.addAttribute("currentPage", page);
			model.addAttribute("hasCoverImage", true);
			return "front-end/psych/article/editForm";
		}

		return "redirect:/psych/article/myArticles?page=" + page;
	}
	
	@PostMapping("/{articleId}/unpublish")
	public String unpublishArticle(Model model,
			@PathVariable Integer articleId, 
			@AuthenticationPrincipal PsychUserDetails prinPsychUser,
			RedirectAttributes redirectAttributes) {
		
		Integer psychId = prinPsychUser.getPsychologist().getPsychId();
		
		try {
			articleService.unPublishMyArticle(articleId, psychId);
			redirectAttributes.addFlashAttribute("alertMessage", "文章已下架");
		} catch (IllegalArgumentException | IllegalStateException e) {
	        redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
		}

        return "redirect:/psych/article/myArticles";
	}
	
	@PostMapping("/{articleId}/delete")
	public String deleteDraft(Model model,
			@PathVariable Integer articleId,
			@AuthenticationPrincipal PsychUserDetails prinPsychUser,
			RedirectAttributes redirectAttributes) {
		
		Integer psychId = prinPsychUser.getPsychologist().getPsychId();
		
		try {
			articleService.deleteDraft(articleId, psychId);
	        redirectAttributes.addFlashAttribute("alertMessage", "草稿已刪除");
		} catch (IllegalArgumentException | IllegalStateException e) {
	        redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
		}
		
        return "redirect:/psych/article/myArticles";
	}
	
	// saving <img src=""> to backend from TinyMCE
	@ResponseBody
	@PostMapping("/uploadImage") 
	public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
		Map<String, String> jsonResult = new HashMap<>();

		if (file == null || file.isEmpty()) {
			jsonResult.put("error", "未選擇檔案");
			return ResponseEntity.badRequest().body(jsonResult); // 400
		}
		
		try {
			ImageUploadValidator.validateImageSize(file, maxImageSize);
		}  catch (IllegalArgumentException e) {
			 jsonResult.put("error", e.getMessage());
			 return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(jsonResult);
		}

		try {
			File dir = new File(uploadDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String originFileName = file.getOriginalFilename();
			String ext = "";

			if (originFileName != null && originFileName.contains(".")) {
				ext = originFileName.substring(originFileName.lastIndexOf("."));
			}

			String newFileName = UUID.randomUUID().toString() + ext;
			File dest = new File(dir, newFileName);
			file.transferTo(dest.toPath());

			String basePath = urlPath.replace("**", "");
			jsonResult.put("location", basePath + newFileName);
			return ResponseEntity.ok(jsonResult); // 200

		} catch (Exception e) {
			jsonResult.put("error", "上傳失敗: " + e.getMessage());
			return ResponseEntity.internalServerError().body(jsonResult); // 500
		}
	}

}
