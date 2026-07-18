package com.freemind.course.order.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

@Service
public class RefundService {

    @Autowired
    private RefundRepository repository;

    @Autowired
    private CourseOrderService courseOrderService;

    @Autowired
    private MemberService memberService;

    // 新增退款申請
    public void addRefund(Refund refund) {
        repository.save(refund);
    }

    // 查詢全部退款申請
    public List<Refund> getAllRefund() {
        return repository.findAll();
    }

    // 依複合主鍵查詢退款申請
    public Refund getRefundById(Refund.CompositeRefund id) {
        Optional<Refund> refund = repository.findById(id);
        return refund.orElse(null);
    }

    // 修改退款申請
    public void updateRefund(Refund refund) {
        repository.save(refund);
    }

    // 刪除退款申請
    public void deleteRefund(Refund.CompositeRefund id) {
        repository.deleteById(id);
    }

    // 取得會員退款資料
    public List<Refund> getRefundByMember(Member member) {
        return repository.findByMember(member);
    }

    // ==========================
    // 會員申請退款
    // ==========================
    public void applyRefund(Integer courseOrderId,
                            Integer memberId,
                            String refundReason) {

        CourseOrder courseOrder = courseOrderService.getOrderById(courseOrderId);

        Member member = memberService.getOneMember(memberId);

        Refund refund = new Refund();

        refund.setCourseOrder(courseOrder);
        refund.setMember(member);
        refund.setRefundReason(refundReason);
        refund.setRefundAmount(courseOrder.getNetAmount());
        refund.setCreatedAt(LocalDateTime.now());
        refund.setRefundStatus(0); // 待審核

        repository.save(refund);
    }

    // ==========================
    // 審核成功
    // ==========================
    public void approveRefund(Integer courseOrderId, Integer memberId, Integer amount) {

        Refund.CompositeRefund id =
                new Refund.CompositeRefund(courseOrderId, memberId);

        Refund refund = getRefundById(id);

        if (refund != null) {
            refund.setRefundStatus(3); // 已退款
            refund.setRefundAmount(amount);
            refund.setRefundedAt(LocalDateTime.now());
            repository.save(refund);
        }
    }

    // ==========================
    // 審核失敗
    // ==========================
    public void rejectRefund(Integer courseOrderId, Integer memberId) {

        Refund.CompositeRefund id =
                new Refund.CompositeRefund(courseOrderId, memberId);

        Refund refund = getRefundById(id);

        if (refund != null) {
            refund.setRefundStatus(2); // 審核失敗
            repository.save(refund);
        }
    }

}
