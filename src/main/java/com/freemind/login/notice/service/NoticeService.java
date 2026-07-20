package com.freemind.login.notice.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.login.notice.entity.MemberNotice;
import com.freemind.login.notice.entity.PsychologistNotice;
import com.freemind.login.notice.repository.MemberNoticeRepository;
import com.freemind.login.notice.repository.PsychologistNoticeRepository;

@Service
public class NoticeService {

	private final MemberNoticeRepository memberNoticeRepository;
	private final PsychologistNoticeRepository psychologistNoticeRepository;
	
	public NoticeService(MemberNoticeRepository memberNoticeRepository , PsychologistNoticeRepository psychologistNoticeRepository) {
		this.memberNoticeRepository = memberNoticeRepository;
		this.psychologistNoticeRepository = psychologistNoticeRepository;
	}

	
	@Transactional
	public void sendToMember(Integer memberId , Integer adminId , String content , byte noticeType) {
		
		MemberNotice n = new MemberNotice();
		n.setMemberId(memberId);
		n.setAdminId(adminId == null ? 1 : adminId); 
		n.setNoticeContent(content);
		n.setNoticeType(noticeType);
		n.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		n.setIsRead(false);
		memberNoticeRepository.save(n);
		
	}
	@Transactional
	public void sendToPsych(Integer psychId , Integer adminId , String content , byte noticeType) {
		
		PsychologistNotice n = new PsychologistNotice();
		n.setPsychId(psychId);
		n.setAdminId(adminId == null ? 1 : adminId); 
		n.setNoticeContent(content);
		n.setNoticeType(noticeType);
		n.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		n.setIsRead(false);
		psychologistNoticeRepository.save(n);
	}

	//管理員 大量發送
	@Transactional
	public void sendToMembers(List<Integer> memberIds , Integer adminId , String content , byte noticeType) {
		for(Integer id : memberIds) {
			sendToMember(id , adminId , content , noticeType);
		}
	}
	@Transactional
	public void sendToPsychs(List<Integer> psychIds , Integer adminId , String content , byte noticeType) {
		for(Integer id : psychIds) {
			sendToPsych(id , adminId , content , noticeType);
		}
	}
	
	//通知頁
	@Transactional(readOnly = true)
	public List<MemberNotice> getMemberNotices(Integer memberId , Byte type) {
		return type == null
				? memberNoticeRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
				: memberNoticeRepository.findByMemberIdAndNoticeTypeOrderByCreatedAtDesc(memberId , type);
	}
 
	@Transactional(readOnly = true)
	public long countMemberUnread(Integer memberId) {
		return memberNoticeRepository.countByMemberIdAndIsReadFalse(memberId);
	}
 
	@Transactional(readOnly = true)
	public List<PsychologistNotice> getPsychNotices(Integer psychId , Byte type) {
		return type == null
				? psychologistNoticeRepository.findByPsychIdOrderByCreatedAtDesc(psychId)
				: psychologistNoticeRepository.findByPsychIdAndNoticeTypeOrderByCreatedAtDesc(psychId, type);		
	}
 
	@Transactional(readOnly = true)
	public long countPsychUnread(Integer psychId) {
		return psychologistNoticeRepository.countByPsychIdAndIsReadFalse(psychId);
	}
	
	//已讀
	@Transactional
	public void markMemberNoticeRead(Integer noticeId , Integer memberId) {
		memberNoticeRepository.findById(noticeId).ifPresent(n ->{
			if(n.getMemberId().equals(memberId)) {
				n.setIsRead(true);
			}
		});
	}
	
	@Transactional
	public void markPsychNoticeRead(Integer noticeId , Integer psychId) {
		psychologistNoticeRepository.findById(noticeId).ifPresent(n ->{
			if(n.getPsychId().equals(psychId)) {
				n.setIsRead(true);
			}
		});
	}
	
	//都已讀
	@Transactional
	public void markAllMemberRead(Integer memberId) {
		memberNoticeRepository.markAllRead(memberId);
	}
	@Transactional
	public void markAllPsychRead(Integer psychId) {
		psychologistNoticeRepository.markAllRead(psychId);
	}
	
}
