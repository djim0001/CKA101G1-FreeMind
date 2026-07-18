package com.freemind;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    
    @GetMapping("/")
    public String index() {
           
        return "index2"; //view
    }
    

    @GetMapping("/3")
    public String index3() {
           
        return "index3"; //view
    }
    
    @GetMapping("/course_index")
    public String selectCourse() {
    	return "back-end/course/courseIndex"; //view
    }
    
    @GetMapping("/consultation")
    public String consultationIndex() {
        return "back-end/consultation/consultationIndex";
    }
    

}