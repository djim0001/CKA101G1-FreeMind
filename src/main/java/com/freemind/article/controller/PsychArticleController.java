package com.freemind.article.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

	@ExceptionHandler(value = { IllegalStateException.class, IllegalArgumentException.class }) // Service 層的表單驗證錯誤
	public String handleError(Exception e, Model model) {
		model.addAttribute("errorMessage", e.getMessage());
		model.addAttribute("form", new ArticleCreateForm());
		model.addAttribute("articleCats", articleCatService.getAllCats());
		return "front-end/psych/article/createForm";
	}

	// testing
	@PostMapping("/set_psychId_session")
	public String setPsychIdSession(@RequestParam("psychIdSession") Integer psychIdSession, HttpSession session) {
		session.setAttribute("psychId", psychIdSession);
		return "redirect:/psych/article/myArticles";
	}

	@GetMapping("/create")
	public String showCreateForm(Model model, @SessionAttribute(name = "psychId", required = false) Integer psychId) {

		if (psychId == null) {
			model.addAttribute("errorMessage", "請先登入心理師帳號");
			// testing
			return "front-end/psych/article/test-login";
		}

		model.addAttribute("form", new ArticleCreateForm());
		return "front-end/psych/article/createForm";
	}

	@GetMapping("/myArticles")
	public String myArticles(Model model, @SessionAttribute(name = "psychId", required = false) Integer psychId) {

		if (psychId == null) {
			model.addAttribute("errorMessage", "請先登入心理師帳號");
			// testing
			return "front-end/psych/article/test-login";
		}

		List<Article> articles = articleService.getMyArticles(psychId);
		model.addAttribute("articles", articles);
		return "front-end/psych/article/myArticles";
	}

	@PostMapping("/create")
	public String createArticle(Model model, @ModelAttribute ArticleCreateForm form,
			@RequestParam("action") String action,
			@SessionAttribute(name = "psychId", required = false) Integer psychId) {

		if (psychId == null) {
			model.addAttribute("errorMessage", "請先登入心理師帳號");
			// testing
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

		if (psychId == null) {
			model.addAttribute("errorMessage", "請先登入心理師帳號");
			// testing
			return "front-end/psych/article/test-login";
		}

		try {
			articleService.submitExistingDraft(articleId, psychId);
		} catch (ArticleValidationException e) {
			redirectAttributes.addFlashAttribute("alertMessage", "無法送審，請先編輯文章填寫完整欄位");
			return "redirect:/psych/article/myArticles";
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
			return "redirect:/psych/article/myArticles";
		}

		return "redirect:/psych/article/myArticles";
	}

	@GetMapping("/{articleId}/edit")
	public String showEditForm(Model model, @PathVariable Integer articleId,
			@SessionAttribute(name = "psychId", required = false) Integer psychId,
			RedirectAttributes redirectAttributes) {

		if (psychId == null) {
			model.addAttribute("errorMessage", "請先登入心理師帳號");
			// testing
			return "front-end/psych/article/test-login";
		}

		// 可編輯: 0 草稿, 3 審核未通過 ; 2 已發布先不讓修改
		try {
			Article article = articleService.getEditableArticle(articleId, psychId);
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

		if (psychId == null) {
			model.addAttribute("errorMessage", "請先登入心理師帳號");
			// testing
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

}
