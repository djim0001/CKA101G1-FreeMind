package com.freemind.course.order.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.order.model.CourseOrder;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.course.order.model.OrderDetail;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.course.order.model.Refund;
import com.freemind.course.order.model.RefundService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member/refund")
public class RefundController {

    @Autowired
    private RefundService refundService;

    @Autowired
    private MemberService memberSvc;

    @Autowired
    private CourseOrderService courseOrderSvc;

    @Autowired
    private OrderDetailService orderDetailSvc;

    @ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
        return memberSvc.findByAccount(authentication.getName());
    }

    // 開啟退款申請頁
    @GetMapping("/detail")
    public String refundDetail(
    		@RequestParam("courseOrderId") Integer courseOrderId,
    		@RequestParam(defaultValue = "1") Integer page,
            @ModelAttribute("member") Member member,
            ModelMap model) {

        List<OrderDetail> details =
                orderDetailSvc.getOrderDetailsByCourseOrderId(courseOrderId);

        if (page < 1)  page = 1;
        Integer currentPage = page;
        Page<CourseOrder> allMyCourseOrder = courseOrderSvc.getOrdersByMember(member, currentPage - 1);
        model.addAttribute("allMyCourseOrder", allMyCourseOrder);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", allMyCourseOrder.getTotalPages());
        model.addAttribute("courseOrderId", courseOrderId);
        model.addAttribute("details", details);
        model.addAttribute("detailsMsg", "show");
        addRefundStatusMap(member, model);

        return "front-end/member/course/Refund";
    }

    // 送出退款申請
    @PostMapping("/apply")
    public String applyRefund(@RequestParam("courseOrderId") Integer courseOrderId,
                              @RequestParam("refundReason") String refundReason,
                              @ModelAttribute("member") Member member,
                              RedirectAttributes redirectAttributes) {

        CourseOrder order = courseOrderSvc.getOrderById(courseOrderId);

        Refund.CompositeRefund id =
                new Refund.CompositeRefund(
                        order.getCourseOrderId(),
                        member.getMemberId()
                );

        if (refundService.getRefundById(id) != null) {
            redirectAttributes.addFlashAttribute("errorMsg", "此訂單已經申請過退款");
            return "redirect:/member/refund/orderlist";
        }

        Refund refund = new Refund();

        refund.setCompositeRefund(id);
        refund.setCourseOrder(order);
        refund.setMember(member);
        refund.setRefundReason(refundReason);
        refund.setRefundAmount(order.getNetAmount());
        refund.setCreatedAt(LocalDateTime.now());
        refund.setRefundStatus( 0);

        refundService.addRefund(refund);

        redirectAttributes.addFlashAttribute("successMsg", "退款申請已送出！");

        return "redirect:/member/refund/orderlist";
    }


    
    //取得會員訂單
    @GetMapping("/orderlist")
    public String Refund(
            @RequestParam(defaultValue = "1") Integer page,
            @ModelAttribute("member") Member member,
            ModelMap model, HttpSession session) {
        if (page < 1)  page = 1;
        Integer currentPage = page;
        Page<CourseOrder> allMyCourseOrder = courseOrderSvc.getOrdersByMember(member, currentPage - 1);
        model.addAttribute("allMyCourseOrder", allMyCourseOrder);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", allMyCourseOrder.getTotalPages());
        addRefundStatusMap(member, model);

        return "front-end/member/course/Refund";
    }

    private void addRefundStatusMap(Member member, ModelMap model) {
        Map<Integer, Integer> refundStatusMap = new HashMap<>();

        List<Refund> refundList = refundService.getRefundByMember(member);

        for (Refund refund : refundList) {
            if (refund.getCourseOrder() != null) {
                refundStatusMap.put(
                        refund.getCourseOrder().getCourseOrderId(),
                        refund.getRefundStatus()
                );
            }
        }

        model.addAttribute("refundStatusMap", refundStatusMap);
    }
    
//    @PostMapping("/apply")
//    public String applyRefund(
//            @RequestParam Integer courseOrderId,
//            @RequestParam String refundReason,
//            HttpSession session) {
//
//        Member member = (Member) session.getAttribute("member");
//
//        refundService.applyRefund(
//                courseOrderId,
//                member.getMemberId(),
//                refundReason);
//
//        return "redirect:/member/course/my_course_order";
//    }
//    
//    @GetMapping("/orderlist")
//    public String Refund(
//            @RequestParam(defaultValue = "1") Integer page,
//            @ModelAttribute("member") Member member,
//            ModelMap model) {
//
//        if(page < 1){
//            page = 1;
//        }
//
//        Page<CourseOrder> allMyCourseOrder =
//                courseOrderSvc.getOrdersByMember(member,page-1);
//
//        model.addAttribute("allMyCourseOrder",allMyCourseOrder);
//        model.addAttribute("currentPage",page);
//        model.addAttribute("totalPages",allMyCourseOrder.getTotalPages());
//
//        return "front-end/member/course/Refund";
//    }
    
}

