package com.freemind.activity.activity.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.freemind.activity.activity.model.Activity;
import com.freemind.activity.category.model.ActivityCat;
import com.freemind.login.member.model.Member;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class HibernateUtil_CompositeQuery_Activity {
	
	// get_aPredicate_For_AnyDB方法：把一個欄位條件，加工成一條查詢規則
													// 建立查詢條件             代表查詢哪一個表格        查詢哪個欄位          使用者實際填入的值     
    public static Predicate get_aPredicate_For_AnyDB(CriteriaBuilder builder, Root<Activity> root, String columnName, String value) {
        Predicate predicate = null;

        if ("activityCatId".equals(columnName)) {  // 如果查詢的欄位等於活動分類編號
            ActivityCat activityCat = new ActivityCat();  // 不查資料庫，自己在記憶體裡「捏造」一個物件
            activityCat.setActivityCatId(Integer.valueOf(value)); // 只填ID，其他欄位全部是null
            predicate = builder.equal(root.get("activityCat"), activityCat);
            // 幫我組一條條件：「Activity的活動分類」，要等於剛剛捏造的『只有ID是XX』假分類物件」——效果就是篩選出WHERE activity_cat_id = XX 的活動。
        }
        else if ("activityCity".equals(columnName))
            predicate = builder.equal(root.get(columnName), value);
        else if ("activityDist".equals(columnName))
            predicate = builder.equal(root.get(columnName), value);
        else if ("activityName".equals(columnName))
            predicate = builder.like(root.get(columnName), "%" + value + "%");
        else if ("activityStatus".equals(columnName))
            predicate = builder.equal(root.get(columnName), Integer.valueOf(value));
        else if ("registrationStatus".equals(columnName)) {  // 用四個時間欄位計算出報名狀態
            LocalDateTime now = LocalDateTime.now();
            int status = Integer.valueOf(value);

            if (status == 0) {  // 尚未開放報名
                predicate = builder.greaterThan(root.get("regisStart"), now);
            } else if (status == 1) {  // 可報名中
                predicate = builder.and(
                    builder.lessThanOrEqualTo(root.get("regisStart"), now),
                    builder.greaterThanOrEqualTo(root.get("regisEnd"), now)
                );
            } else if (status == 2) {  // 已截止報名
                predicate = builder.and(
                    builder.lessThan(root.get("regisEnd"), now),
                    builder.greaterThan(root.get("activityEnd"), now)
                );
            } else if (status == 3) {  // 已結束
                predicate = builder.lessThan(root.get("activityEnd"), now);
            }
        }
        else if ("isFull".equals(columnName)) {
            boolean isFull = Boolean.parseBoolean(value);
            if (isFull) {
                predicate = builder.greaterThanOrEqualTo(root.get("regisCount"), root.get("capacity"));
            } else {
                predicate = builder.lessThan(root.get("regisCount"), root.get("capacity"));
            }
        }
        else if ("activityStartFrom".equals(columnName)) {
            LocalDate date = LocalDate.parse(value);
            predicate = builder.greaterThanOrEqualTo(root.get("activityStart"), date.atStartOfDay());
        }
        else if ("activityStartTo".equals(columnName)) {
            LocalDate date = LocalDate.parse(value);
            predicate = builder.lessThanOrEqualTo(root.get("activityStart"), date.atTime(23, 59, 59));
        }

        return predicate;
    }
    
    // 會員查看活動列表
    // getAllC方法：收集使用者送出的所有欄位、一個個拿去加工、把所有做好的條件組合起來、真正執行查詢
    // Map<String, String[]>模擬的是req.getParameterMap()的回傳型態——也就是「使用者從表單送出的所有欄位資料」。(servlet p.105)
    public static List<Activity> getAllC_ForMember(Map<String, String[]> map, Integer currentPage, Session session) {
        Transaction tx = session.beginTransaction();
        List<Activity> list = null;
        try {
            // 條件產生器
        		CriteriaBuilder builder = session.getCriteriaBuilder();
            // 查詢架構(結果型態是activity)
        		CriteriaQuery<Activity> criteriaQuery = builder.createQuery(Activity.class);
            // 來源表（FROM Activity）
        		Root<Activity> root = criteriaQuery.from(Activity.class);
            // 空籃子，裝查詢條件用
        		List<Predicate> predicateList = new ArrayList<Predicate>();

        		
        		// 固定條件：只顯示已發布的活動
            predicateList.add(builder.equal(root.get("activityStatus"), 2));
           
        		// 把使用者送出的所有欄位名稱都抓出來 → 一個一個檢查：這個欄位『有沒有填值』、『不是空白』、『不是action這個特殊欄位』
        		// → 通過檢查的，才呼叫get_aPredicate_For_AnyDB組成一條查詢條件，丟進籃子裡收集起來。
        		Set<String> keys = map.keySet();
            int count = 0;  // 除錯用訊息，後續可拿掉
            for (String key : keys) {
                String value = map.get(key)[0];
                if (value != null && value.trim().length() != 0 && !"action".equals(key) && !"currentPage".equals(key)) {
                	 Predicate p = get_aPredicate_For_AnyDB(builder, root, key, value.trim());
                     if (p != null) {
                         predicateList.add(p);
                     }
                 }
            }
            
            // 把籃子裡的查詢條件轉成陣列，套進查詢當作 WHERE 條件
            criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
            // 依活動編號，由小到大排序
            criteriaQuery.orderBy(builder.asc(root.get("activityId")));

            int pageSize = 3; // 每頁顯示3筆資料
            int first = (currentPage - 1) * pageSize;  //從資料庫的第幾筆資料開始撈
            
            // 把查詢架構，變成一個真正可以執行的查詢物件
            Query query = session.createQuery(criteriaQuery);
            query.setFirstResult(first); // 從第幾筆開始撈 
            query.setMaxResults(pageSize); // 撈幾筆
            // 真正執行查詢，把結果撈回來
            list = query.getResultList();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) tx.rollback();
            throw ex;
        } finally {
            session.close();
        }
        return list;
    }
    
    //活動列表分頁功能
    public static long getTotalCount_ForMember(Map<String, String[]> map, Session session) {
        Transaction tx = session.beginTransaction();
        long total; //最終算出的總筆數
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
            Root<Activity> root = criteriaQuery.from(Activity.class);
            List<Predicate> predicateList = new ArrayList<Predicate>();

            predicateList.add(builder.equal(root.get("activityStatus"), 2));

            Set<String> keys = map.keySet();
            for (String key : keys) {
                String value = map.get(key)[0];

                if (value != null && value.trim().length() != 0 && !"action".equals(key) && !"currentPage".equals(key)) {
                	Predicate p = get_aPredicate_For_AnyDB(builder, root, key, value.trim());
                	if (p != null) {
                	    predicateList.add(p);
                		}
                }
            }
            criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
            criteriaQuery.select(builder.count(root)); // 指定這次查詢的目的：計算符合前面where條件的資料，總共有幾筆

            total = session.createQuery(criteriaQuery).getSingleResult();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) tx.rollback();
            throw ex;
        } finally {
            session.close();
        }
        return total;
    }
    
    // 發起者查看發起的活動
    public static List<Activity> getAllC_ForOwner(Map<String, String[]> map, Integer memberId, Integer currentPage, Session session) {
        Transaction tx = session.beginTransaction();
        List<Activity> list = null;
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Activity> criteriaQuery = builder.createQuery(Activity.class);
            Root<Activity> root = criteriaQuery.from(Activity.class);
            List<Predicate> predicateList = new ArrayList<Predicate>();

            // 固定條件：只查詢這個會員發起的活動
            Member member = new Member();
            member.setMemberId(memberId);
            predicateList.add(builder.equal(root.get("member"), member));

            Set<String> keys = map.keySet();
            for (String key : keys) {
                String value = map.get(key)[0];
                if (value != null && value.trim().length() != 0 && !"action".equals(key)&& !"currentPage".equals(key)) {
                	 Predicate p = get_aPredicate_For_AnyDB(builder, root, key, value.trim());
                     if (p != null) {
                         predicateList.add(p);
                     }
                 }
            }
            criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
            criteriaQuery.orderBy(builder.asc(root.get("activityId")));
            
            int pageSize = 3; 
            int first = (currentPage - 1) * pageSize; 
            
            Query query = session.createQuery(criteriaQuery);
            query.setFirstResult(first);
            query.setMaxResults(pageSize); 
            list = query.getResultList();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) tx.rollback();
            throw ex;
        } finally {
            session.close();
        }
        return list;
    }
    
  
    public static long getTotalCount_ForOwner(Map<String, String[]> map, Integer memberId, Session session) {
        Transaction tx = session.beginTransaction();
        long total; 
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
            Root<Activity> root = criteriaQuery.from(Activity.class);
            List<Predicate> predicateList = new ArrayList<Predicate>();

            Member member = new Member();
            member.setMemberId(memberId);
            predicateList.add(builder.equal(root.get("member"), member));

            Set<String> keys = map.keySet();
            for (String key : keys) {
                String value = map.get(key)[0];

                if (value != null && value.trim().length() != 0 && !"action".equals(key) && !"currentPage".equals(key)) {
                	Predicate p = get_aPredicate_For_AnyDB(builder, root, key, value.trim());
                	if (p != null) {
                	    predicateList.add(p);
                		}
                }
            }
            criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
            criteriaQuery.select(builder.count(root)); // 指定這次查詢的目的：計算符合前面where條件的資料，總共有幾筆

            total = session.createQuery(criteriaQuery).getSingleResult();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) tx.rollback();
            throw ex;
        } finally {
            session.close();
        }
        return total;
    }
    
    // 後台員工查詢活動列表
    public static List<Activity> getAllC_ForAdmin(Map<String, String[]> map, Integer currentPage, Session session) {
        Transaction tx = session.beginTransaction();
        List<Activity> list = null;
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Activity> criteriaQuery = builder.createQuery(Activity.class);
            Root<Activity> root = criteriaQuery.from(Activity.class);
            List<Predicate> predicateList = new ArrayList<Predicate>();

            // 沒有固定條件，後台可以看到所有狀態

            Set<String> keys = map.keySet();
            for (String key : keys) {
                String value = map.get(key)[0];
                if (value != null && value.trim().length() != 0 && !"action".equals(key) && !"currentPage".equals(key)) {
                 	Predicate p = get_aPredicate_For_AnyDB(builder, root, key, value.trim());
                	if (p != null) {
                	    predicateList.add(p);
                		}
                }
            }
            criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
            criteriaQuery.orderBy(builder.asc(root.get("activityId")));

            int pageSize = 3;
            int first = (currentPage - 1) * pageSize;

            Query query = session.createQuery(criteriaQuery);
            query.setFirstResult(first);
            query.setMaxResults(pageSize);
            list = query.getResultList();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) tx.rollback();
            throw ex;
        } finally {
            session.close();
        }
        return list;
    }
    
    public static long getTotalCount_ForAdmin(Map<String, String[]> map, Session session) {
        Transaction tx = session.beginTransaction();
        long total;
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
            Root<Activity> root = criteriaQuery.from(Activity.class);
            List<Predicate> predicateList = new ArrayList<Predicate>();


            Set<String> keys = map.keySet();
            for (String key : keys) {
                String value = map.get(key)[0];
                if (value != null && value.trim().length() != 0 && !"action".equals(key) && !"currentPage".equals(key)) {
                	Predicate p = get_aPredicate_For_AnyDB(builder, root, key, value.trim());
                	if (p != null) {
                	    predicateList.add(p);
                	}
                }
            }
            criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
            criteriaQuery.select(builder.count(root));

            total = session.createQuery(criteriaQuery).getSingleResult();
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) tx.rollback();
            throw ex;
        } finally {
            session.close();
        }
        return total;
    }
}