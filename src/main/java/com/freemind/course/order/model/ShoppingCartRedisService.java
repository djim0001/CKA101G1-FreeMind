package com.freemind.course.order.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseRepository;
import com.freemind.course.dto.CartItemDTO;

@Service
public class ShoppingCartRedisService {

	private final StringRedisTemplate stringRedisTemplate;
	private final CourseRepository courseRepository;

	public ShoppingCartRedisService(StringRedisTemplate stringRedisTemplate, CourseRepository courseRepository) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.courseRepository = courseRepository;
	}

	public void addCourse(Integer memberId, Integer courseId) {
		String key = "shoppingCart:member:" + memberId;
		stringRedisTemplate.opsForSet().add(key, String.valueOf(courseId));
	}

	public void removeCourse(Integer memberId, Integer courseId) {
		String key = "shoppingCart:member:" + memberId;
		stringRedisTemplate.opsForSet().remove(key, String.valueOf(courseId));
	}

	public boolean isCourseInCart(Integer memberId, Integer courseId) {
		String key = "shoppingCart:member:" + memberId;
		Boolean result = stringRedisTemplate.opsForSet().isMember(key, String.valueOf(courseId));
		return Boolean.TRUE.equals(result);
	}

	public List<CartItemDTO> getCartCartItemDTOs(Integer memberId) {
        String key = "shoppingCart:member:" + memberId;

        Set<String> courseIdSet = stringRedisTemplate.opsForSet().members(key);

        if (courseIdSet == null || courseIdSet.isEmpty()) {
            return List.of();
        }

        List<Integer> courseIds = courseIdSet.stream()
                .map(Integer::valueOf)
                .toList();

        List<Course> courses = courseRepository.findAllById(courseIds);

        List<CartItemDTO> cartItemList = new ArrayList<>();
        BigDecimal cartTotal = BigDecimal.ZERO;

        for (Course course : courses) {
            CartItemDTO item = new CartItemDTO();

            item.setCourseId(course.getCourseId());
            item.setCourseName(course.getCourseName());
            item.setPsychologistName(course.getPsychologist().getName());
            item.setPrice(course.getPrice());
            
            BigDecimal price = course.getPrice() != null
                    ? BigDecimal.valueOf(course.getPrice())
                    : BigDecimal.ZERO;

            BigDecimal discount = course.getPsychDiscount() != null
                    ? course.getPsychDiscount()
                    : BigDecimal.ONE;
            
            item.setPsychDiscount(discount);

            BigDecimal subtotal = price.multiply(discount)
                    .setScale(0, RoundingMode.HALF_UP);

            item.setSubtotal(subtotal);

            cartItemList.add(item);
        }
        return cartItemList;
    }
	
	public int calculateCartTotal(List<CartItemDTO> list) {
	    BigDecimal total = BigDecimal.ZERO;

	    for (CartItemDTO item : list) {
	        total = total.add(item.getSubtotal());
	    }

	    return total.setScale(0, RoundingMode.HALF_UP).intValue();
	}

	public void clearCart(Integer memberId) {
		String key = "shoppingCart:member:" + memberId;
		stringRedisTemplate.delete(key);
	}
	
	public Long getCourseCount(Integer memberId) {
	    if (memberId == null) {
	        throw new IllegalArgumentException("會員編號不能為空");
	    }

	    String key = "shoppingCart:member:" + memberId;

	    Long count = stringRedisTemplate.opsForSet().size(key);

	    return count != null ? count : 0L;
	}
	

}