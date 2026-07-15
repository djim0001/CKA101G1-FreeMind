package com.freemind.activity.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component  // 用@Component才能被掃描到，不要用@SpringBootApplication，避免跟專案裡原本的Application.java衝突
public class ActivityCatTestRunner implements CommandLineRunner {

//    	@Autowired
//    	private ActivityCatRepository repository;
	
//		@Autowired
//		private ActivityCatService service;
	
	    @Override
	    public void run(String... args) throws Exception {
	    
    		// 新增測試
//	    ActivityCat newCat = new ActivityCat();
//	    newCat.setActivityCatName("測試分類");
//	    repository.save(newCat);
//	    System.out.println("新增完成");
	
    		// 修改測試
//	    ActivityCat cat = repository.findById(7).get();
//	    cat.setActivityCatName("運動測試");
//	    repository.save(cat);
//	    System.out.println("修改完成");
	
    		// 刪除測試
//	    repository.deleteById(7);  
//	    System.out.println("刪除完成");
    	
    	
    		// 單筆查詢 測試
//	    	Optional<ActivityCat> optional = repository.findById(6);
//	    	ActivityCat cat = optional.orElse(null);
//	    	if (cat != null) {
//	    	    System.out.println(cat.getActivityCatId() + " , " + cat.getActivityCatName());
//	    	} else {
//	    	    System.out.println("查無資料");
//	    	}
    	
    	
    		// 查詢全部 測試
//      List<ActivityCat> list = repository.findAll();
//      System.out.println("=== 全部分類 ===");
//      for (ActivityCat cat : list) {
//          System.out.println(cat.getActivityCatId() + " , " + cat.getActivityCatName());
//      }

        // 測試自訂的模糊搜尋
//      List<ActivityCat> list2 = repository.findByActivityCatNameLike("%運動%");
//      System.out.println("=== 模糊搜尋「運動」===");
//      if (!list2.isEmpty()) {
//          for (ActivityCat cat : list2) {
//              System.out.println(cat.getActivityCatId() + " , " + cat.getActivityCatName());
//          }
//      } else {
//          System.out.println("查無資料");
//      }
    	
    	
    	
	    	
	    	
	    	// ============測試service============
    	
        // 測試模糊搜尋：確認Service有自動加上 % ,不用自己加
//        List<ActivityCat> list = service.getByNameLike("運動");
//        System.out.println("=== Service模糊搜尋「運動」===");
//        for (ActivityCat cat : list) {
//            System.out.println(cat.getActivityCatId() + " , " + cat.getActivityCatName());
//        }

        // 測試刪除一個不存在的ID，確認existsById的保護有沒有生效
//        service.deleteActivityCat(9999);
//        System.out.println("測試刪除不存在的ID,程式應該正常結束,不報錯");

        // 測試單筆查詢
//        ActivityCat cat = service.getOneActivityCat(7);
//        System.out.println(cat != null ? cat.getActivityCatName() : "查無資料");
    
    }
}