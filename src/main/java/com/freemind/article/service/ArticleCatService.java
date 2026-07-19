package com.freemind.article.service;

import java.util.List;


import com.freemind.article.entity.ArticleCat;

public interface ArticleCatService {

	List<ArticleCat> getAllCats();
	
	List<ArticleCat> getActiveCats();
	
	ArticleCat getCatById(Integer catId);
	
	List<ArticleCat> getCatsByName(String keyword);

	ArticleCat createCat(String catName);
	
	void updateCat(Integer catId, String catName);

	void deactivateCat(Integer catId);

}
