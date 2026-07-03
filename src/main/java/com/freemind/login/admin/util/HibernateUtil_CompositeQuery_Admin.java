/*
 *  1. 萬用複合查詢-可由客戶端隨意增減任何想查詢的欄位
 *  
 * */

package com.freemind.login.admin.util;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.freemind.login.admin.model.Admin;

//import hibernate.util.HibernateUtil;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery; //Hibernate 5.2 開始 取代原 org.hibernate.Criteria 介面
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.Query; //Hibernate 5 開始 取代原 org.hibernate.Query 介面


public class HibernateUtil_CompositeQuery_Admin {

	public static Predicate get_aPredicate_For_AnyDB(CriteriaBuilder builder, Root<Admin> root, String columnName, String value) {

		Predicate predicate = null;

		if ("adminId".equals(columnName) || "accountStatus".equals(columnName)) // 用於Integer
			predicate = builder.equal(root.get(columnName), Integer.valueOf(value));
		else if ("adminAccount".equals(columnName) || "name".equals(columnName) || "phoneNumber".equals(columnName)) // 用於varchar
			predicate = builder.like(root.get(columnName), "%" + value + "%");
		else if ("hiredate".equals(columnName)) // 用於LocalDateTime
			predicate = builder.equal(root.get(columnName), LocalDateTime.parse(value));

		return predicate;
	}

	@SuppressWarnings("unchecked")
	public static List<Admin> getAllC(Map<String, String[]> map, Session session) {

//		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		List<Admin> list = null;
		try {
			// 【●創建 CriteriaBuilder】
			CriteriaBuilder builder = session.getCriteriaBuilder();
			// 【●創建 CriteriaQuery】
			CriteriaQuery<Admin> criteriaQuery = builder.createQuery(Admin.class);
			// 【●創建 Root】
			Root<Admin> root = criteriaQuery.from(Admin.class);

			List<Predicate> predicateList = new ArrayList<Predicate>();

			Set<String> keys = map.keySet();
			int count = 0;
			for (String key : keys) {
				String value = map.get(key)[0];
				if (value != null && value.trim().length() != 0 && !"action".equals(key)) {
					count++;
					predicateList.add(get_aPredicate_For_AnyDB(builder, root, key, value.trim()));
					System.out.println("有送出查詢資料的欄位數count = " + count);
				}
			}
			System.out.println("predicateList.size()="+predicateList.size());
			criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
			criteriaQuery.orderBy(builder.asc(root.get("adminId")));
			// 【●最後完成創建 javax.persistence.Query●】
			Query query = session.createQuery(criteriaQuery); //javax.persistence.Query; //Hibernate 5 開始 取代原 org.hibernate.Query 介面
			list = query.getResultList();

			tx.commit();
		} catch (RuntimeException ex) {
			if (tx != null)
				tx.rollback();
			throw ex; // System.out.println(ex.getMessage());
		} finally {
			session.close();
			// HibernateUtil.getSessionFactory().close();
		}

		return list;
	}
}
