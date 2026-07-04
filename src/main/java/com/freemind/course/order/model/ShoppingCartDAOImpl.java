package com.freemind.course.order.model;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;

public class ShoppingCartDAOImpl implements ShoppingCartDAO{

	private final StringRedisTemplate stringRedisTemplate;
	
	public ShoppingCartDAOImpl(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}
	
	
	@Override
	public String getCartKey(Integer memberId) {
		return "shoppingCart:member:" + memberId;
	}

	@Override
	public void insert(Integer memberId, ShoppingCartDTO cartDTO) {
		
	}

	@Override
	public List<ShoppingCartDTO> getAllByOneMember(Integer memberId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShoppingCartDTO getOneByOneMember(Integer memberId, Integer courseId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteOne(Integer memberId, Integer courseId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearAll(Integer memberId) {
		// TODO Auto-generated method stub
		
	}

}
