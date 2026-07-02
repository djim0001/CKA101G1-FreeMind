package com.freemind.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freemind.article.entity.ArticleCat;

public interface ArticleCatRepository extends JpaRepository<ArticleCat, Integer>{

}
