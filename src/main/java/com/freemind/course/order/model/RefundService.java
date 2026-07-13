package com.freemind.course.order.model;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefundService {

    @Autowired
    private RefundRepository repository;

    /**
     * 新增退款申請
     */
    public void addRefund(Refund refund) {
        repository.save(refund);
    }

    /**
     * 查詢全部退款申請
     */
    public List<Refund> getAllRefund() {
        return repository.findAll();
    }

    /**
     * 依複合主鍵查詢退款申請
     */
    public Refund getRefundById(Refund.CompositeRefund id) {
        Optional<Refund> refund = repository.findById(id);
        return refund.orElse(null);
    }

    /**
     * 修改退款申請
     */
    public void updateRefund(Refund refund) {
        repository.save(refund);
    }

    /**
     * 刪除退款申請
     */
    public void deleteRefund(Refund.CompositeRefund id) {
        repository.deleteById(id);
    }
}
