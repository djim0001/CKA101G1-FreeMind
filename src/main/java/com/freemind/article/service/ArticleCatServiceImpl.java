package com.freemind.article.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.article.entity.ArticleCat;
import com.freemind.article.repository.ArticleCatRepository;

@Service
public class ArticleCatServiceImpl implements ArticleCatService{
	
	@Autowired
	private ArticleCatRepository articleCatRepository;

	@Override
	public List<ArticleCat> getAllCats() {
		return articleCatRepository.findAll();
	}

}
