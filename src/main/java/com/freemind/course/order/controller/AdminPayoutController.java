package com.freemind.course.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.course.order.model.Payout;
import com.freemind.course.order.model.PayoutService;

@Controller
@RequestMapping("/admin/adminPayout")
public class AdminPayoutController {

    @Autowired
    private PayoutService payoutService;

    // 顯示全部撥款資料
    @GetMapping("/listAll")
    public String listAllPayout(
            @RequestParam(value = "psychId", required = false) Integer psychId,
            ModelMap model) {

        List<Payout> allPayout;

        if (psychId == null) {
            allPayout = payoutService.getAll();
        } else {
            allPayout = payoutService.getByPsychId(psychId);
        }

        model.addAttribute("allPayout", allPayout);
        model.addAttribute("psychId", psychId);

        return "back-end/course/course/Payout";
    }

}