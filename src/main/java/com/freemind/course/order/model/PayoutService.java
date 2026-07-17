package com.freemind.course.order.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayoutService {

	@Autowired
	private PayoutRepository repository;

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

}
