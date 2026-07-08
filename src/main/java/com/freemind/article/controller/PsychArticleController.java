package com.freemind.article.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.article.dto.ArticleCreateForm;
import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.exception.ArticleValidationException;
import com.freemind.article.service.ArticleCatService;
import com.freemind.article.service.ArticleService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/psych/article")
public class PsychArticleController {

	@Autowired
	private ArticleService articleService;

	@Autowired
	private ArticleCatService articleCatService;

	@ModelAttribute("articleCats")
	public List<ArticleCat> articleCatList() {
		return articleCatService.getAllCats();
	}

	// testing
	@PostMapping("/set_psychId_session")
	public String setPsychIdSession(@RequestParam(name = "psychIdSession", required = false) Integer psychIdSession, HttpSession session) {
		session.setAttribute("psychId", psychIdSession);
		return "redirect:/psych/article/myArticles";
	}

	@GetMapping("/myArticles")
	public String myArticles(Model model, 
			@SessionAttribute(name = "psychId", required = false) Integer psychId,
			@RequestParam(name = "page", defaultValue = "1") Integer page) {

		// testing
		if (psychId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "front-end/psych/article/test-login";
		}

		Page<Article> articlePage = articleService.getMyArticles(psychId, page);
		model.addAttribute("articlePage", articlePage);
		model.addAttribute("currentPage", page);
		return "front-end/psych/article/myArticles";
	}
	
	@GetMapping("/create")
	public String showCreateForm(Model model, @SessionAttribute(name = "psychId", required = false) Integer psychId) {

		// testing
		if (psychId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "front-end/psych/article/test-login";
		}

		model.addAttribute("form", new ArticleCreateForm());
		return "front-end/psych/article/createForm";
	}

	@PostMapping("/create")
	public String createArticle(Model model, @ModelAttribute ArticleCreateForm form,
			@RequestParam("action") String action,
			@SessionAttribute(name = "psychId", required = false) Integer psychId) {

		// testing
		if (psychId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "front-end/psych/article/test-login";
		}

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
			@SessionAttribute(name = "psychId", required = false) Integer psychId,
			RedirectAttributes redirectAttributes) {

		// testing
		if (psychId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "front-end/psych/article/test-login";
		}

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
	public String showEditForm(Model model, 
			@PathVariable Integer articleId,
			@SessionAttribute(name = "psychId", required = false) Integer psychId,
			RedirectAttributes redirectAttributes) {

		// testing
		if (psychId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "front-end/psych/article/test-login";
		}

		// 可編輯: 0 草稿, 2 已發布, 3 審核未通過
		try {
			Article article = articleService.getEditableArticle(articleId, psychId);
			
			if (article.getArticleStatus() == 2) {
				Article copy = articleService.createEditCopy(articleId, psychId);
				return "redirect:/psych/article/" + copy.getArticleId() + "/edit";
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
			model.addAttribute("hasCoverImage", article.getCoverImage() != null);
			return "front-end/psych/article/editForm";
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
			return "redirect:/psych/article/myArticles";
		}

	}

	@PostMapping("/{articleId}/edit")
	public String updateArticle(Model model, @ModelAttribute ArticleCreateForm form,
			@RequestParam("action") String action, @PathVariable Integer articleId,
			@SessionAttribute(name = "psychId", required = false) Integer psychId) {

		// testing
		if (psychId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "front-end/psych/article/test-login";
		}

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
			model.addAttribute("hasCoverImage", true);
			return "front-end/psych/article/editForm";
		} catch (IllegalArgumentException | IllegalStateException e) {
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("form", form);
			model.addAttribute("articleId", articleId);
			model.addAttribute("hasCoverImage", true);
			return "front-end/psych/article/editForm";
		}

		return "redirect:/psych/article/myArticles";
	}
	
	@PostMapping("/{articleId}/unpublish")
	public String unpublishArticle(Model model,
			@PathVariable Integer articleId, 
			@SessionAttribute(name = "psychId", required = false) Integer psychId,
			RedirectAttributes redirectAttributes) {
		
		// testing
		if (psychId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "front-end/psych/article/test-login";
		}
		
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
			@SessionAttribute(name = "psychId", required = false) Integer psychId,
			RedirectAttributes redirectAttributes) {
		
		// testing
		if (psychId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "front-end/psych/article/test-login";
		}
		
		try {
			articleService.deleteDraft(articleId, psychId);
	        redirectAttributes.addFlashAttribute("alertMessage", "草稿已刪除");
		} catch (IllegalArgumentException | IllegalStateException e) {
	        redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
		}
		
        return "redirect:/psych/article/myArticles";
	}

}
