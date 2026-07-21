package com.freemind.course.scheduler;

import java.time.YearMonth;
import java.time.ZoneId;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.freemind.course.order.model.PayoutService;

@Component
public class PayoutScheduler {

    private final PayoutService payoutService;

    public PayoutScheduler(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    /**
     * 每月 1 日凌晨 2 點，
     * 建立上一個月的心理師結算資料。
     */
    @Scheduled(
        cron = "0 0 2 1 * ?",
        zone = "Asia/Taipei"
    )
    public void generateMonthlyPayouts() {

        YearMonth previousMonth =
                YearMonth.now(ZoneId.of("Asia/Taipei"))
                         .minusMonths(1);

        int createdCount =
                payoutService.createMonthlyPayouts(previousMonth);

        System.out.println(
            "心理師月結算建立完成，月份："
            + previousMonth
            + "，新增筆數："
            + createdCount
        );
    }
}