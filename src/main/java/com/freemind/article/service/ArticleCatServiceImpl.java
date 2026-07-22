package com.freemind.article.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.freemind.article.entity.ArticleCat;
import com.freemind.article.repository.ArticleCatRepository;
import com.freemind.article.repository.ArticleRepository;

@Service
public class ArticleCatServiceImpl implements ArticleCatService{
	
	@Autowired
	private ArticleCatRepository articleCatRepository;
	
	@Autowired
	private ArticleRepository articleRepository;

	@Override
	public List<ArticleCat> getAllCats() {
		return articleCatRepository.findAll(Sort.by("articleCatId").ascending());
	}
	
	@Override
	public List<ArticleCat> getActiveCats() {
		return articleCatRepository.findByArticleCatStatusTrue();
	}

	@Override
	public ArticleCat getCatById(Integer catId) {
		ArticleCat articleCat = articleCatRepository.findById(catId)
				.orElseThrow(() -> new IllegalArgumentException("查無此文章分類"));
		
		return articleCat;
	}
	
	@Override
	public List<ArticleCat> getCatsByName(String keyword) {
		return articleCatRepository.findCatsByName(keyword);
	}
	
	@Override
	public ArticleCat createCat(String catName) {
		if (catName == null || catName.isBlank()) {
			throw new IllegalArgumentException("請輸入分類名稱");
		}
		
		if (articleCatRepository.existsCatName(catName.trim())) {
			throw new IllegalArgumentException("此分類已存在");
		}
		
		ArticleCat articleCat = new ArticleCat();
		articleCat.setArticleCatName(catName.trim());
		articleCat.setArticleCatStatus(true);
		articleCatRepository.save(articleCat);
		
		return articleCat;
	}

	@Override
	public void updateCat(Integer catId, String catName) {
		ArticleCat articleCat = articleCatRepository.findById(catId)
				.orElseThrow(() -> new IllegalArgumentException("查無此分類"));
		
		if (catName == null || catName.isBlank()) {
			throw new IllegalArgumentException("請填寫分類名稱");
		}

		if (articleCatRepository.existsCatNameExcludingId(catName.trim(), catId)) {
			throw new IllegalArgumentException("此分類已存在");
		}

		articleCat.setArticleCatName(catName.trim());
		articleCatRepository.save(articleCat);
	}

	@Override
	public void deactivateCat(Integer catId) {
		ArticleCat articleCat = articleCatRepository.findById(catId)
				.orElseThrow(() -> new IllegalArgumentException("查無此分類"));
		
		if (articleRepository.existsByArticleCat(articleCat)) {
			throw new IllegalStateException("此分類下仍有文章，無法停用");
		}
		
		if (!articleCat.getArticleCatStatus()) {
	        throw new IllegalStateException("此分類已停用");
	    }
		
		articleCat.setArticleCatStatus(false);
		articleCatRepository.save(articleCat);
	}

}
