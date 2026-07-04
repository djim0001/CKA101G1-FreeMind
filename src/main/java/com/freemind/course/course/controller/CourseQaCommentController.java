package com.freemind.course.course.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseQaComment;
import com.freemind.course.course.model.CourseQaCommentService;
import com.freemind.course.course.model.CourseService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

@Controller
@RequestMapping("/course/qa")
public class CourseQaCommentController {

    @Autowired
    private CourseQaCommentService commentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private MemberService memberService;

    // 會員查看某門課程的提問頁面
    @GetMapping("/member")
    public String showMemberQuestions(
            @RequestParam("courseId") Integer courseId,
            // ⬆ (小)表示從瀏覽器送來的 Request 中取得名為 courseId 的參數。
            @SessionAttribute("memberId") Integer memberId,
            // ⬆ (中)表示從瀏覽器送來的 Request 中取得名為 memberId 的參數。
            ModelMap model) {

    	 Course course = courseService.getOneCourse(courseId);

    	    if (course == null) {
    	        throw new IllegalArgumentException("找不到此課程");
    	    }

    	    List<CourseQaComment> questions =
    	            commentService.getQuestionsByCourseAndMember(
    	                    courseId,
    	                    memberId
    	            );

    	    model.addAttribute("course", course);
    	    model.addAttribute("questions", questions);

    	    return "front-end/course/course/memberCourseQa";
    	}
    // 會員提問頁面
    @PostMapping("/addQa")
    public String memberQa(
    	@RequestParam(name="courseId",required = false) Integer courseId,
    	@SessionAttribute(name="memberId",required = false) Integer memberId,
    	ModelMap model
    		) {
    	Course course = courseService.getOneCourse(courseId);
    	Member member = memberService.getOneMember(memberId);
    	CourseQaComment comment = new CourseQaComment();
    	model.addAttribute("course", course);
    	model.addAttribute("member", member);
    	model.addAttribute("courseQa", comment);
    	return "front-end/member/course/memberCourseQa";
    }
    
 // 會員送出課程提問
    @PostMapping("/ask")
    public String addQuestion(
            @RequestParam("courseId") Integer courseId,
            @RequestParam("courseQuestion") String courseQuestion,
            @SessionAttribute(name="memberId",required = false) Integer memberId) {

    	//用課程宣告課程VO,用courseId取得一個課程 放入course這個變數名稱
        Course course = courseService.getOneCourse(courseId);
        Member member = memberService.getOneMember(memberId);
        CourseQaComment courseQaComment = commentService.addQuestion(course, member,courseQuestion );
//        if (course == null) {
//            throw new IllegalArgumentException("找不到此課程");
//        }

//        if (member == null) {
//            throw new IllegalArgumentException("找不到此會員");
//        }

//        commentService.addQuestion(
//        		questionId,
//                member,
//                courseQuestion
//        );
        return "front-end/member/course/memberCourseQa";
       }
       
        //查詢所有會員有購買的課程
        public String showMemberQuestions(
                @SessionAttribute(name="memberId",required = false) Integer memberId,
                ModelMap model) {

            List<Course> memberQuestions =
            		courseService.getAllCourse();

            model.addAttribute("memberQuestions", memberQuestions);//前面的是對外面的(HTML)

            return "front-end/member/course/memberCourseQa";
        }
        
   
 // 心理師查看自己課程收到的提問
    @GetMapping("/psych")
    public String showPsychQuestions(
            @SessionAttribute(name="psychId",required = false) Integer psychId,
            ModelMap model) {

        List<CourseQaComment> questions =
                commentService.getQuestionsByPsychId(psychId);

        model.addAttribute("questions", questions);//前面的是對外面的(HTML)

        return "front-end/psych/course/psychCourseQa";
    }
    //心理師送出回復
    @PostMapping("answerQa")
    public String answerQuestion(
            @RequestParam("questionId") Integer questionId,
            @RequestParam("courseAnswer") String courseAnswer) {

        commentService.answerUpdateQuestion(
        		questionId, courseAnswer
        );

        return "redirect:/course/qa/psych";
    }
}