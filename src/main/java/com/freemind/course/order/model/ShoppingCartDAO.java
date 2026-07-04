package com.freemind.course.order.model;

import java.util.List;
import java.util.Map;


public interface ShoppingCartDAO {

	String getCartKey(Integer memberId);
	void insert(Integer memberId, ShoppingCartDTO cartDTO);
    public List<ShoppingCartDTO> getAllByOneMember(Integer memberId);
    ShoppingCartDTO getOneByOneMember(Integer memberId, Integer courseId);
    void deleteOne(Integer memberId, Integer courseId);
    public void clearAll(Integer memberId);
}
