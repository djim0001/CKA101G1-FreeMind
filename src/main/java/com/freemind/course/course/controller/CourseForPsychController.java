package com.freemind.course.course.controller;

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
import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.service.PsychologistService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/course")
public class CourseForPsychController {

	 private final CourseService courseSvc;
	    private final CourseCategoriesService courseCategoriesSvc;
	    private final PsychologistService psychologistService;
	    
	    @Value("${course.video.upload-path}")
	    private String videoUploadPath;

	    public CourseForPsychController(
	            CourseService courseSvc,
	            CourseCategoriesService courseCategoriesSvc,
	            PsychologistService psychologistService) {

	        this.courseSvc = courseSvc;
	        this.courseCategoriesSvc = courseCategoriesSvc;
	        this.psychologistService = psychologistService;
	    }
	

	@ModelAttribute("courseCategoriesListAll")
	public List<CourseCategories> courseCategoriesListAll() {
		return courseCategoriesSvc.getAllCourseCategories();
	}

	
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

		if (psychId == null) 
			return "front-end/psych/course/selectCourse";
		Integer currentPage = (page == null) ? 1 : Integer.parseInt(page);
		model.addAttribute("currentPage", currentPage);
		Psychologist psychologist = psychologistService.getOnePsychologist(psychId);
		Page<Course> courseListAllPages = courseSvc.getCoursesByPsychId(psychId, currentPage - 1, "courseId");
		model.addAttribute("psychologist", psychologist);
		model.addAttribute("courseListAllPages", courseListAllPages);
//		if(session.getAttribute(pageQty) == null) 
//			session.setAttribute("psychCoursePageQty", 1);

		return "front-end/psych/course/selectCourse";
	}

	@GetMapping("psychAddCourse")
	public String psychAddCourse(ModelMap model, @SessionAttribute(name = "psychId", required = false) Integer psychId) {
		Course course = new Course();
		if(psychId == null) {
			model.addAttribute("pError", "請先登入心理師編號");
			return "front-end/psych/course/selectCourse";
		}
		course.setPsychologist(psychologistService.getOnePsychologist(psychId));
		model.addAttribute("course", course);
		return "front-end/psych/course/addCourse";
	}

	@PostMapping("insertOrUpdateCourse")
	public String insertOrUpdateCourse (
			@RequestParam(name="video", required = false) MultipartFile video,
			@RequestParam(name="videoPre", required = false) MultipartFile videoPre,
			@Valid Course course, BindingResult result, 
			@SessionAttribute(name = "psychId") Integer psychId,
			ModelMap model) throws IOException{
		if (result.hasErrors()) {
			return "front-end/psych/course/addCourse";
		}
			// 確認影片是否上傳
		if (course.getCourseId() == null && (video == null || video.isEmpty())) {
			model.addAttribute("videoErrorMsg", "兩個影片都需上傳");
			return "front-end/psych/course/addCourse";
		}
		if (course.getCourseId() == null && (videoPre == null || videoPre.isEmpty())) {
			model.addAttribute("videoErrorMsg", "兩個影片都需上傳");
			return "front-end/psych/course/addCourse";
		}
		// 將課程路徑存入
		if (video != null && !video.isEmpty())
			course.setVideoSrc(uploadVideo(video));
		else {
			String videoSrc = course.getVideoSrc();
			course.setVideoSrc(videoSrc);
		}
		if(videoPre != null&& !videoPre.isEmpty())
			course.setVideoSrcPre(uploadVideo(videoPre));
		else {
			String videoSrcPre = course.getVideoSrcPre();
			course.setVideoSrcPre(videoSrcPre);
		}
		// 新增課程
//		course.setPsychId(psychId);
		course.setPsychologist(psychologistService.getOnePsychologist(psychId));
		course.setCourseStatus((byte)0);
		courseSvc.updateCourse(course);
		model.addAttribute("course", course);

		return "front-end/psych/course/listOneCourse";
	}
	

	@PostMapping("psychGetOneCourse")
	public String psychGetOneCourse(@RequestParam("courseId") Integer courseId, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		model.addAttribute("course", course);
		return "front-end/psych/course/listOneCourse";
	}
	@PostMapping("psychUpdateCourse")
	public String psychUpdateCourse(@RequestParam("courseId") Integer courseId, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		model.addAttribute("course", course);
		return "front-end/psych/course/addCourse";
	}
	@PostMapping("psychSubmitCourse")
	public String psychSubmitCourse(@RequestParam("courseId") Integer courseId, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		course.setCourseStatus((byte)1);
		courseSvc.updateCourse(course);
		model.addAttribute("course", course);
		return "front-end/psych/course/listOneCourse";
	}
	
	
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
