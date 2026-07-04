package com.freemind.course.order.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;

//@Service
public class ShoppingCartService {

	@Autowired
	ShoppingCartDAOImpl dao;
	
//	private String getCartKey(Integer memberId) {
//		return dao.getCartKey();
//	}
//
//	public void addCourseToCart(Integer memberId, ShoppingCartDTO cartDTO) {
//		String key = getCartKey(memberId);
//		String field = String.valueOf(cartDTO.getCourseId());
//
//		try {
//			String json = objectMapper.writeValueAsString(cartDTO);
//
//			stringRedisTemplate.opsForHash().put(key, field, json);
//
//		} catch (JsonProcessingException e) {
//			throw new RuntimeException("購物車資料轉換 JSON 失敗", e);
//		}
//	}
//
//	public List<ShoppingCartDTO> getCartList(Integer memberId) {
//		String key = getCartKey(memberId);
//
//		Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
//
//		List<ShoppingCartDTO> cartList = new ArrayList<>();
//
//		for (Object value : entries.values()) {
//			try {
//				ShoppingCartDTO dto = objectMapper.readValue(value.toString(), ShoppingCartDTO.class);
//
//				cartList.add(dto);
//
//			} catch (JsonProcessingException e) {
//				throw new RuntimeException("Redis JSON 轉換 ShoppingCartDTO 失敗", e);
//			}
//		}
//
//		return cartList;
//	}
//
//	public ShoppingCartDTO getCartItem(Integer memberId, Integer courseId) {
//		String key = getCartKey(memberId);
//		String field = String.valueOf(courseId);
//
//		Object value = stringRedisTemplate.opsForHash().get(key, field);
//
//		if (value == null) {
//			return null;
//		}
//
//		try {
//			return objectMapper.readValue(value.toString(), ShoppingCartDTO.class);
//
//		} catch (JsonProcessingException e) {
//			throw new RuntimeException("Redis JSON 轉換 ShoppingCartDTO 失敗", e);
//		}
//	}
//
//	public boolean isCourseInCart(Integer memberId, Integer courseId) {
//		String key = getCartKey(memberId);
//		String field = String.valueOf(courseId);
//
//		return Boolean.TRUE.equals(stringRedisTemplate.opsForHash().hasKey(key, field));
//	}
//
//	public void removeCourseFromCart(Integer memberId, Integer courseId) {
//		String key = getCartKey(memberId);
//		String field = String.valueOf(courseId);
//
//		stringRedisTemplate.opsForHash().delete(key, field);
//	}
//
//	public void clearCart(Integer memberId) {
//		String key = getCartKey(memberId);
//
//		stringRedisTemplate.delete(key);
//	}
//	public BigDecimal calculateTotal(Integer price, BigDecimal psychDiscount) {
//		
//		return BigDecimal.valueOf(price).multiply(psychDiscount);
//		
//	}
}