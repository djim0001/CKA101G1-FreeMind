package com.freemind.course.course.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseCategories;
import com.freemind.course.course.model.CourseCategoriesService;
import com.freemind.course.course.model.CourseService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/course")
public class CourseController {

	@Autowired
	CourseService courseSvc;

	@Autowired
	CourseCategoriesService courseCategoriesSvc;
	
	@Value("${course.video.upload-path}")
	private String videoUploadPath;

	@ModelAttribute("courseCategoriesListAll")
	public List<CourseCategories> courseCategoriesListAll() {
		return courseCategoriesSvc.getAllCourseCategories();
	}

	// psych_function
	@PostMapping("set_psychId_session")
	public String setPsychIdSession(@RequestParam(name = "psychIdSession") Integer psychIdSession, ModelMap model,
			HttpSession session) {
		session.setAttribute("psychId", psychIdSession);
		return "redirect:/course/psychSelectCourse";
	}

	@GetMapping("psychSelectCourse")
	public String psychSelectCourse(@SessionAttribute(name = "psychId", required = false) Integer psychId,
			@RequestParam(name = "page", required = false) String page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			@SessionAttribute(name = "psychCoursePageQty", required = false) String pageQty, ModelMap model,
			HttpSession session) {

		Integer currentPage = (page == null) ? 1 : Integer.parseInt(page);
		model.addAttribute("currentPage", currentPage);
		if (psychId != null) {
			Page<Course> courseListAllPages = courseSvc.getCoursesBypsychId(psychId, currentPage - 1, "courseId");
			model.addAttribute("courseListAllPages", courseListAllPages);
		}
//		if(session.getAttribute(pageQty) == null) 
//			session.setAttribute("psychCoursePageQty", 1);

		return "front-end/course/course/psychSelectCourse";
	}

	@GetMapping("psychAddCourse")
	public String psychAddCourse(ModelMap model, @SessionAttribute(name = "psychId", required = false) Integer psychId) {
		Course course = new Course();
		if(psychId == null) {
			model.addAttribute("pError", "請先登入心理師編號");
			return "front-end/course/course/psychSelectCourse";
		}
		course.setPsychId(psychId);
		model.addAttribute("course", course);
		return "front-end/course/course/psychAddCourse";
	}

	@PostMapping("insertCourse")
	public String insertCourse (
			@RequestParam(name="video", required = false) MultipartFile video,
			@RequestParam(name="videoPre", required = false) MultipartFile videoPre,
			@Valid Course course, BindingResult result, 
			@SessionAttribute(name = "psychId") Integer psychId,
			ModelMap model) throws IOException{
		course.setPsychId(psychId);
		if (result.hasErrors()) {
			return "front-end/course/course/psychAddCourse";
		}
		// 確認影片是否上傳
		System.out.println("確認影片");
		if (video == null || video.isEmpty()) {
			model.addAttribute("videoErrorMsg", "兩個影片都需上傳");
			return "front-end/course/course/psychAddCourse";
		}
		System.out.println("確認影片2");
		if (videoPre == null || videoPre.isEmpty()) {
			model.addAttribute("videoErrorMsg", "兩個影片都需上傳");
			return "front-end/course/course/psychAddCourse";
		}
		// 將課程路徑存入
		System.out.println("新增課程路徑");
		course.setVideoSrc(uploadVideo(video));
		course.setVideoSrcPre(uploadVideo(videoPre));
		// 新增課程
		System.out.println("新增課程");
		courseSvc.addCourse(course);
		model.addAttribute("course", course);

		return "front-end/course/course/psychListOneCourse";
	}
	

	@PostMapping("psychGetOneCourse")
	public String psychGetOneCourse(@RequestParam("courseId") Integer courseId, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		model.addAttribute("course", course);
		return "front-end/course/course/psychListOneCourse";
	}

	// admin_function

	// member_function
	
	// util
	public String uploadVideo(MultipartFile video) throws IOException{
		String originalFilename = video.getOriginalFilename();

		String extension = "";
		if (originalFilename != null && originalFilename.contains(".")) {
		    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
		String newFileName = LocalDateTime.now().format(formatter) + extension;

		String uploadDir = videoUploadPath;

		Path uploadPath = Paths.get(uploadDir);

		if (!Files.exists(uploadPath)) {
		    Files.createDirectories(uploadPath);
		}

		Path savePath = uploadPath.resolve(newFileName);
		video.transferTo(savePath.toFile());
		
		return newFileName;
	}

}
