package com.freemind.login.psychologist.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.consultation.orders.model.OrdersService;
import com.freemind.login.psychologist.dto.PsychologistProfileRes;
import com.freemind.login.psychologist.service.ExpertiseService;
import com.freemind.login.psychologist.service.PsychologistService;
import com.freemind.login.security.membersecurity.MemberUserDetails;


@Controller
@RequestMapping("/search")
public class PsychologistMemberController {

	private final PsychologistService psychologistService;
	private final ExpertiseService expertiseService;
	private final OrdersService ordersSvc;
	public PsychologistMemberController(PsychologistService psychologistService,
									ExpertiseService expertiseService ,OrdersService ordersSvc) {
		this.psychologistService = psychologistService;
		this.expertiseService = expertiseService;
		this.ordersSvc = ordersSvc;
	}
	
	private String blankToNull(String s) {
		return (s == null || s.isBlank()) ? null : s;
	}
	
	@GetMapping
	public String search(
	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) String gender,
	        @RequestParam(required = false) String psychLoc,
	        @RequestParam(required = false) Integer minFee,
	        @RequestParam(required = false) Integer maxFee,
	        @RequestParam(required = false) String expertiseName,
	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate slotDate,
	        @RequestParam(required = false) String orderBy,
	        @AuthenticationPrincipal MemberUserDetails memberDetails,
	        Model model) {
		
		name = blankToNull(name);
		gender = blankToNull(gender);
	    psychLoc = blankToNull(psychLoc);
	    expertiseName = blankToNull(expertiseName);
	    orderBy = blankToNull(orderBy);
		
		
//	    try {
//	        return ResponseEntity.ok(
//	            psychologistService.search(name, gender, psychLoc, minFee, maxFee,
//	                    expertiseName, slotDate, sortBy, sortDir));
//	    } catch (IllegalArgumentException e) {
//	        return ResponseEntity.badRequest().body(e.getMessage());
//	        model.addAttribute("psychList", List.of()); // 給空清單，避免頁面 th:each 對 null 迭代出錯
//	    }
	    try {
	        List<PsychologistProfileRes> psychList = psychologistService.search(
	        		 name, gender, psychLoc, minFee, maxFee, expertiseName, slotDate, orderBy);
	        model.addAttribute("psychList", psychList);
	    } catch (IllegalArgumentException e) {
	        model.addAttribute("errorMessage", e.getMessage());
	        model.addAttribute("psychList", List.of()); // 給空清單，避免頁面 th:each 對 null 迭代出錯
	    }
	    
	    model.addAttribute("name", name);
	    model.addAttribute("gender", gender);
	    model.addAttribute("psychLoc", psychLoc);
	    model.addAttribute("minFee", minFee);
	    model.addAttribute("maxFee", maxFee);
	    model.addAttribute("expertiseName", expertiseName);
	    model.addAttribute("slotDate", slotDate);
	    model.addAttribute("orderBy", orderBy);
	    model.addAttribute("expertiseOptions", expertiseService.getAllExpertiseNames());
	    if (memberDetails != null) {
	        model.addAttribute("member", memberDetails.getMember());
	    }
	    

	    return "front-end/member/psych/search";
	    
	}

	
	@GetMapping("/detail/{id}")
	public String detailPage(@PathVariable Integer id, Model model) {
	    try {
	        PsychologistProfileRes psych = psychologistService.getProfile(id);
	        System.out.println("1111111"+psych.getAvailableDates());
	        model.addAttribute("psych", psych);
	        model.addAttribute("avgRating", ordersSvc.getAvgRating(psych.getPsychId()));
	        model.addAttribute("ratingCount", ordersSvc.getRatingCount(psych.getPsychId()));
	    } catch (IllegalArgumentException e) {
	        model.addAttribute("errorMessage", e.getMessage());
	    }
	    return "front-end/member/psych/detail";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
