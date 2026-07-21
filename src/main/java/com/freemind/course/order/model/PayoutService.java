package com.freemind.course.order.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.repository.PsychologistRepository;

@Service
public class PayoutService {

	@Autowired
	private PayoutRepository repository;
    private PsychologistRepository psychologistRepository;
    private OrderDetailRepository orderDetailRepository;
	
    private static final int PAYOUT_STATUS_PENDING = 0;
	private static final BigDecimal COMMISSION_RATE =
            new BigDecimal("0.10");

	// 查詢全部
	public List<Payout> getAll() {
		return repository.findAll();
	}

	// 查詢單筆
	public Payout getOne(Integer payoutId) {
		return repository.findById(payoutId).orElse(null);
	}

	// 新增
	public Payout add(Payout payout) {

		Integer netAmount = payout.getGrossPayoutAmount() - payout.getPlatformCommission() - payout.getBillingOffset();

		payout.setNetPayoutAmount(netAmount);

		return repository.save(payout);
	}

	// 修改
	public Payout update(Payout payout) {

		Integer netAmount = payout.getGrossPayoutAmount() - payout.getPlatformCommission() - payout.getBillingOffset();

		payout.setNetPayoutAmount(netAmount);

		return repository.save(payout);
	}

	// 查詢某位心理師的撥款
	public List<Payout> getByPsychId(Integer psychId) {
		return repository.findByPsychologistPsychId(psychId);
	}

	public int countUnpaidPayouts() {
		return repository.countByPayoutStatus(0);
	}

	// 查詢某月份的撥款
	public List<Payout> getByBillingMonth(String billingMonth) {
		return repository.findByBillingMonth(billingMonth);
	}

	// 判斷是否有資料 有就回傳時間
	public Payout updatePayoutStatus(Integer payoutId, Integer payoutStatus) {

		Payout payout = repository.findById(payoutId).orElse(null);

		if (payout == null) {
			return null;
		}

		payout.setPayoutStatus(payoutStatus);

		if (payoutStatus == 1) {
			payout.setPaidAt(LocalDateTime.now());
		}

		return repository.save(payout);
	}
	// 未撥款給心理師的數量
	public int countPendingPayouts(Integer psychId) {

        if (psychId == null) {
            throw new IllegalArgumentException("心理師編號不能為空");
        }

        return repository
                .countByPsychologist_PsychIdAndPayoutStatus(
                        psychId,
                        0
                );
    }
	// 每月自動撥款
	 /**
     * 建立指定月份所有心理師的結算資料。
     *
     * @param billingYearMonth 結算月份，例如 2026-06
     */
    public int createMonthlyPayouts(YearMonth billingYearMonth) {

        String billingMonth = billingYearMonth.toString();

        LocalDateTime startDate = billingYearMonth
                .atDay(1)
                .atStartOfDay();

        LocalDateTime endDate = billingYearMonth
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay();

        List<Psychologist> psychologists =
                psychologistRepository.findAll();

        int createdCount = 0;

        for (Psychologist psychologist : psychologists) {

            Integer psychId = psychologist.getPsychId();

            boolean payoutExists =
            		repository
                        .existsByPsychologistPsychIdAndBillingMonth(
                            psychId,
                            billingMonth
                        );

            if (payoutExists) {
                continue;
            }

            Long grossAmountLong =
                    orderDetailRepository
                        .sumMonthlyRevenueByPsychologist(
                            psychId,
                            startDate,
                            endDate
                        );

            int grossAmount = grossAmountLong == null
                    ? 0
                    : Math.toIntExact(grossAmountLong);

            int platformCommission =
                    BigDecimal.valueOf(grossAmount)
                        .multiply(COMMISSION_RATE)
                        .setScale(0, RoundingMode.HALF_UP)
                        .intValueExact();

            // 其他抵扣，目前先設為 0
            int billingOffset = 0;

            int netAmount =
                    grossAmount
                    - platformCommission
                    - billingOffset;

            Payout payout = new Payout();

            payout.setBillingMonth(billingMonth);
            payout.setPsychologist(psychologist);

            // 尚未人工處理，所以可以先不指定 admin
            payout.setAdmin(null);

            payout.setGrossPayoutAmount(grossAmount);
            payout.setPlatformCommission(platformCommission);
            payout.setBillingOffset(billingOffset);
            payout.setNetPayoutAmount(Math.max(netAmount, 0));

            // 尚未付款
            payout.setPaidAt(null);
            payout.setPayoutStatus(PAYOUT_STATUS_PENDING);

            repository.save(payout);

            createdCount++;
        }

        return createdCount;
    }

}
