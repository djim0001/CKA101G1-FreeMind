package com.freemind.article.service;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;

import com.freemind.article.entity.ArticleCat;

public interface ArticleCatService {

	List<ArticleCat> getAllCats();
	
	List<ArticleCat> getActiveCats();
	
//	Page<ArticleCat> getAllCats(Integer catId, Integer page);

	ArticleCat getCatById(Integer catId);

	ArticleCat createCat(String catName);
	
	void updateCat(Integer catId, String catName);

	void deactivateCat(Integer catId);

}
