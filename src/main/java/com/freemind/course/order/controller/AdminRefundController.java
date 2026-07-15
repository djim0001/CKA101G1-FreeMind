package com.freemind.course.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.order.model.OrderDetailService;
import com.freemind.course.order.model.Refund;
import com.freemind.course.order.model.RefundService;

@Controller
@RequestMapping("/admin/refund")
public class AdminRefundController {

    @Autowired
    private RefundService refundService;

    @Autowired
    private OrderDetailService orderDetailService;


    // 退款列表
    @GetMapping("/list")
    public String list(Model model) {

        model.addAttribute("refundList", refundService.getAllRefund());

        return "back-end/course/course/RefundList";
    }


    // 退款詳細資料
    @GetMapping("/detail/{courseOrderId}/{memberId}")
    public String detail(@PathVariable Integer courseOrderId,
                         @PathVariable Integer memberId,
                         Model model) {

        Refund.CompositeRefund id =
                new Refund.CompositeRefund(courseOrderId, memberId);

        Refund refund = refundService.getRefundById(id);

        model.addAttribute("refund", refund);
        model.addAttribute(
                "orderDetails",
                orderDetailService.getOrderDetailsByCourseOrderId(courseOrderId)
        );

        return "back-end/course/course/RefundDetail";
    }

    // ==========================
    // 審核成功
    // ==========================
    @PostMapping("/approve/{courseOrderId}/{memberId}")
    public String approve(@PathVariable Integer courseOrderId,
                          @PathVariable Integer memberId,
                          RedirectAttributes redirectAttributes) {

        refundService.approveRefund(courseOrderId, memberId);

        redirectAttributes.addFlashAttribute("success", "退款審核成功，已退款");

        return "redirect:/admin/refund/list";
    }

    // ==========================
    // 審核失敗
    // ==========================
    @PostMapping("/reject/{courseOrderId}/{memberId}")
    public String reject(@PathVariable Integer courseOrderId,
                         @PathVariable Integer memberId,
                         RedirectAttributes redirectAttributes) {

        refundService.rejectRefund(courseOrderId, memberId);

        redirectAttributes.addFlashAttribute("success", "退款審核失敗");

        return "redirect:/admin/refund/list";
    }

}
