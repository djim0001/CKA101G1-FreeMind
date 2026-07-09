package com.freemind.activity.activity;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.freemind.activity.activity.model.Activity;
import com.freemind.activity.activity.util.HibernateUtil_CompositeQuery_Activity;

@Component
public class ActivityTestRunner implements CommandLineRunner {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void run(String... args) throws Exception {

        // 測試複合查詢：不填任何條件，應該撈出全部活動
//        Map<String, String[]> map = new TreeMap<String, String[]>();
//
//        List<Activity> list = HibernateUtil_CompositeQuery_Activity.getAllC_ForMember(map, sessionFactory.openSession());
//        System.out.println("=== 沒有設定條件，撈出全部資料 ===");
//        for (Activity a : list) {
//            System.out.println(a.getActivityId() + " , " + a.getActivityName());
//        }

        // 測試單一條件查詢：查詢縣市
//        Map<String, String[]> map2 = new TreeMap<String, String[]>();
//        map2.put("activityCity", new String[] { "台北市" });
//        List<Activity> list2 = HibernateUtil_CompositeQuery_Activity.getAllC_ForMember(map2, sessionFactory.openSession());
//        System.out.println("=== 只查縣市=台北市 ===");
//        for (Activity a : list2) {
//            System.out.println(a.getActivityId() + " , " + a.getActivityName() + " , " + a.getActivityCity());
//        }

        // 測試多條件同時查詢
//        Map<String, String[]> map3 = new TreeMap<String, String[]>();
//        map3.put("activityCity", new String[] { "台北市" });
//        map3.put("activityName", new String[] { "夜跑" });
//        List<Activity> list3 = HibernateUtil_CompositeQuery_Activity.getAllC_ForMember(map3, sessionFactory.openSession());
//        System.out.println("=== 縣市+名稱關鍵字 ===");
//        for (Activity a : list3) {
//            System.out.println(a.getActivityId() + " , " + a.getActivityName() + " , " + a.getActivityCity());
//        }

        // 測試活動分類(關聯物件查詢)
//        Map<String, String[]> map4 = new TreeMap<String, String[]>();
//        map4.put("activityCatId", new String[] { "6" });  
//        List<Activity> list4 = HibernateUtil_CompositeQuery_Activity.getAllC_ForMember(map4, sessionFactory.openSession());
//        System.out.println("=== 查詢活動分類 ===");
//        for (Activity a : list4) {
//            System.out.println(a.getActivityId() + " , " + a.getActivityName() + " , " + a.getActivityCat().getActivityCatName());
//        }
    }
}