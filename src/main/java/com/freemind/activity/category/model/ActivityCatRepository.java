package com.freemind.activity.category.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ActivityCatRepository extends JpaRepository<ActivityCat, Integer> {
	// 繼承後就自動擁有基本 CRUD 功能
    
	// 活動分類查詢功能：1.下拉式選單 2.模糊搜尋
	// 1.下拉式選單
	// 直接用內建的 findAll() 取得全部分類清單去產生選項
    // 選單選定後查看單筆:直接用內建的 findById(Integer id) 即可

    // 2.模糊搜尋:需自訂的查詢方法
    @Query(value = "from ActivityCat where activityCatName like ?1 order by activityCatName")
    List<ActivityCat> findByActivityCatNameLike(String keyword);
}
