package com.freemind;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexTempController {

	@GetMapping("/index_temp")
	public String frontHome() {
		return "index_temp"; // 本來的首頁
	}

	@GetMapping("/course_index")
	public String selectCourse() {
		return "back-end/course/courseIndex";
	}

	@GetMapping("/consultation")
	public String consultationIndex() {
		return "back-end/consultation/consultationIndex";
	}

}