package com.freemind.course.course.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseCategories;
import com.freemind.course.course.model.CourseCategoriesService;
import com.freemind.course.course.model.CourseQaComment;
import com.freemind.course.course.model.CourseQaCommentService;
import com.freemind.course.course.model.CourseService;
import com.freemind.course.dto.PsychDiscountFormDTO;
import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.service.PsychologistService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

//@Validated
@Controller
@RequestMapping("/psych/course")
public class CourseForPsychController {

	private final CourseService courseSvc;
    private final CourseCategoriesService courseCategoriesSvc;
    private final PsychologistService psychologistService;
    private final CourseQaCommentService courseQaCommentSvc;
    
//    @Value("${course.video.upload-path}")
//    private String videoUploadPath;
    @Value("${course.video.upload.dir}")
    private String videoUploadDir;
    @Value("${course.video.url-path}")
	private String videoUrlPath;

    public CourseForPsychController(
            CourseService courseSvc,
            CourseCategoriesService courseCategoriesSvc,
            PsychologistService psychologistService,
            CourseQaCommentService courseQaCommentSvc) {

        this.courseSvc = courseSvc;
        this.courseCategoriesSvc = courseCategoriesSvc;
        this.psychologistService = psychologistService;
        this.courseQaCommentSvc = courseQaCommentSvc;
    }
	

	@ModelAttribute("courseCategoriesListAll")
	public List<CourseCategories> courseCategoriesListAll() {
		return courseCategoriesSvc.getAllCourseCategories();
	}

	
	@PostMapping("set_psychId_session")
	public String setPsychIdSession(@RequestParam(name = "psychIdSession") Integer psychIdSession, ModelMap model,
			HttpSession session) {
		session.setAttribute("psychId", psychIdSession);
		
		return "redirect:/psych/course/select_course";
	}

	@GetMapping("/select_course")
	public String psychSelectCourse(
			@SessionAttribute(name = "psychId", required = false) Integer psychId,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model) {

		if(psychId == null)
			return "front-end/psych/course/selectCourse";
		if (page < 1)  page = 1;
		Integer currentPage = page;
		
		String sortField = (orderBy == null || orderBy.isBlank()) ? "courseId" : orderBy;
		
		Psychologist psychologist = psychologistService.getOnePsychologist(psychId);
		
		Page<Course> courseListAllPages = 
				courseSvc.getCoursesByPsychId(psychId, currentPage - 1, sortField);
		
		model.addAttribute("psychologist", psychologist);
		model.addAttribute("courseListAllPages", courseListAllPages);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", courseListAllPages.getTotalPages());
		if(orderBy != null)
			model.addAttribute("orderBy", orderBy);

		return "front-end/psych/course/selectCourse";
	}

	@GetMapping("/add_course")
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
	
	@PostMapping("/answer_course")
	public String psychAnswerdCourse(
			ModelMap model,
			@RequestParam("questionId") Integer questionId,
			@RequestParam("courseId") Integer courseId,
			@RequestParam("courseAnswer") String courseAnswer,
			@SessionAttribute(name = "psychId", required = false) Integer psychId,
			RedirectAttributes redirectAttributes) {
		if(psychId == null) {
			model.addAttribute("pError", "請先登入心理師編號");
			return "front-end/psych/course/selectCourse";
		}
		try {
			courseQaCommentSvc.answerQuestion(questionId, psychId, courseAnswer);
			model.addAttribute(
	                "answerMsg",
	                "回復成功"
	        );
	    } catch (IllegalArgumentException e) {
	    		model.addAttribute(
	                "answerMsg",
	                e.getMessage()
	        );
	    }
		
		Course course = courseSvc.getOneCourse(courseId);
		
		List<CourseQaComment> courseQuestions = 
				courseQaCommentSvc.getAllCourseQaByCourseId(courseId);
		model.addAttribute("courseQuestions", courseQuestions);
		model.addAttribute("course", course);
		
		return "front-end/psych/course/listOneCourse";
	}
	
	@PostMapping("/insert_or_update_course")
	public String insertOrUpdateCourse (
			@RequestParam(name="video") MultipartFile video,
			@RequestParam(name="videoPre") MultipartFile videoPre,
			@Valid Course course, BindingResult result, 
			@SessionAttribute(name = "psychId") Integer psychId,
			ModelMap model) throws IOException{
		result = removeFieldError(course, result, "video");
		result = removeFieldError(course, result, "videoPre");
		boolean videoExist = false;
		// 確認影片是否上傳
		if (course.getCourseId() == null && (video == null || video.isEmpty())) {
			model.addAttribute("videoErrorMsg", "兩個影片都需上傳");
		}else if (course.getCourseId() == null && (videoPre == null || videoPre.isEmpty())) {
			model.addAttribute("videoErrorMsg", "兩個影片都需上傳");
		}else 
			videoExist = true;
		if (result.hasErrors() || !videoExist) {
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
		course.setPsychologist(psychologistService.getOnePsychologist(psychId));
		courseSvc.updateCourse(course);
		model.addAttribute("course", course);

		return "front-end/psych/course/listOneCourse";
	}
	
	@PostMapping("/update_psych_discount")
	public String updatePsychDiscount(
	        @Valid @ModelAttribute("psychDiscountForm") PsychDiscountFormDTO form,
	        BindingResult result, ModelMap model,
	        @RequestParam(name = "courseId") Integer courseId) {
		
		if (result.hasErrors()) {
			Course course = courseSvc.getOneCourse(form.getCourseId());

	        model.addAttribute("course", course);
	        model.addAttribute("psychDiscountMsg", "show");
			return "front-end/psych/course/listOneCourse";
		}
		Course course = courseSvc.getOneCourse(courseId);
		course.setPsychDiscount(form.getPsychDiscount());
		course.setDiscountStart(form.getDiscountStart().atStartOfDay());
		course.setDiscountEnd(
				form.getDiscountStart()
				.plusMonths(form.getDiscountMonth())
				.atStartOfDay());
		courseSvc.updateCourse(course);
		
		model.addAttribute("course", course);
		return "front-end/psych/course/listOneCourse";
	}

	@GetMapping("/get_one_course/{courseId}")
	public String psychGetOneCourse(
//			@RequestParam("courseId") Integer courseId, 
			@PathVariable("courseId") Integer courseId, 
			ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		
		List<CourseQaComment> courseQuestions = 
				courseQaCommentSvc.getAllCourseQaByCourseId(courseId);
		model.addAttribute("courseQuestions", courseQuestions);
		model.addAttribute("course", course);
		return "front-end/psych/course/listOneCourse";
	}
	@PostMapping("/update_course")
	public String psychUpdateCourse(@RequestParam("courseId") Integer courseId, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		model.addAttribute("course", course);
		return "front-end/psych/course/addCourse";
	}
	@PostMapping("/submit_course")
	public String psychSubmitCourse(@RequestParam("courseId") Integer courseId, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		course.setCourseStatus((byte)1);
		courseSvc.updateCourse(course);
		model.addAttribute("course", course);
		return "front-end/psych/course/listOneCourse";
	}
	@PostMapping("/discount_model_box")
	public String discountModelBox(ModelMap model, 
			@RequestParam("courseId") Integer courseId,
			@SessionAttribute(name = "psychId", required = false) Integer psychId) {
		if(psychId == null) {
			model.addAttribute("pError", "請先登入心理師編號");
			return "front-end/psych/course/selectCourse";
		}
		Course course = courseSvc.getOneCourse(courseId);
		PsychDiscountFormDTO form = new PsychDiscountFormDTO();
	    form.setCourseId(courseId);
	    
		model.addAttribute("course", course);
		model.addAttribute("psychDiscountForm", form);
		model.addAttribute("psychDiscountMsg", "show");
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

//		String uploadDir = videoUploadPath;
		String uploadDir = videoUploadDir;
		String urlPath = videoUrlPath;
		
		try {
			File dir = new File(uploadDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File dest = new File(dir, newFileName);
			video.transferTo(dest.toPath());
			String basePath = urlPath.replace("**", "");
			return basePath + newFileName;
		}catch (Exception e) {
				return newFileName; // 500
			}
//		Path uploadPath = Paths.get(uploadDir);
//
//		if (!Files.exists(uploadPath)) {
//		    Files.createDirectories(uploadPath);
//		}
//
//		Path savePath = uploadPath.resolve(newFileName);
//		video.transferTo(savePath.toFile());
		
	}
	
	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(
			Course course, BindingResult result, 
			String removedFieldname) {
		// 從原BindingResult中去除removedFieldname這個欄位的紀錄之後，再將其它所保留下來的欄位的FieldError紀錄轉換成errorsListToKeep這個List物件
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldError -> !fieldError.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		// 對驗證的目標對象建立一個新(空)的BindingResult的物件
		// 參數一：目標對象
		// 參數二：對象的名稱(通常是類別名首字母小寫)
		result = new BeanPropertyBindingResult(course, "course");
		// 將新(空)的BindingResult的物件加入保留下來的其它欄位的FieldError紀錄
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		// 更新後的BindingResult
		return result;
	}
	

}
