/*
 *  1. 萬用複合查詢-可由客戶端隨意增減任何想查詢的欄位
 *  
 * */

package com.freemind.login.member.util;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.freemind.login.member.model.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.Query;


public class HibernateUtil_CompositeQuery_Member {

	public static Predicate get_aPredicate_For_AnyDB(CriteriaBuilder builder, Root<Member> root, String columnName, String value) {

		Predicate predicate = null;

		if ("memberId".equals(columnName) || "accountStatus".equals(columnName)) // 用於Integer
			predicate = builder.equal(root.get(columnName), Integer.valueOf(value));
		else if ("memberAccount".equals(columnName) || "name".equals(columnName)
				|| "phoneNumber".equals(columnName) || "nickname".equals(columnName)
				|| "email".equals(columnName) || "city".equals(columnName)
				|| "dist".equals(columnName)) // 用於varchar，LIKE模糊查詢
			predicate = builder.like(root.get(columnName), "%" + value + "%");
		else if ("gender".equals(columnName)) // 用於varchar，完全比對
			predicate = builder.equal(root.get(columnName), value);
		else if ("birthday".equals(columnName)) // 用於LocalDate
			predicate = builder.equal(root.get(columnName), LocalDate.parse(value));
		else if ("regisAt".equals(columnName)) // 用於LocalDateTime
			predicate = builder.equal(root.get(columnName), LocalDateTime.parse(value));

		return predicate;
	}

	@SuppressWarnings("unchecked")
	public static List<Member> getAllC(Map<String, String[]> map, Session session) {

		Transaction tx = session.beginTransaction();
		List<Member> list = null;
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Member> criteriaQuery = builder.createQuery(Member.class);
			Root<Member> root = criteriaQuery.from(Member.class);

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
			System.out.println("predicateList.size()=" + predicateList.size());
			criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
			criteriaQuery.orderBy(builder.asc(root.get("memberId")));

			Query query = session.createQuery(criteriaQuery);
			list = query.getResultList();

			tx.commit();
		} catch (RuntimeException ex) {
			if (tx != null)
				tx.rollback();
			throw ex;
		} finally {
			session.close();
		}

		return list;
	}
}
