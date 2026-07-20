package com.freemind.login.psychologist.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.ParameterMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.consultation.orders.model.Orders;
import com.freemind.consultation.orders.model.OrdersRepository;
import com.freemind.consultation.slots.model.Slots;
import com.freemind.consultation.slots.model.SlotsRepository;
import com.freemind.login.member.model.Member;
import com.freemind.login.notice.service.NoticeService;
import com.freemind.login.psychologist.dto.AvailableDateRes;
import com.freemind.login.psychologist.dto.ConflictOrderRes;
import com.freemind.login.psychologist.dto.ExpertiseRes;
import com.freemind.login.psychologist.dto.PsychologistAdminRes;
import com.freemind.login.psychologist.dto.PsychologistPasswordReq;
//import com.freemind.login.psychologist.dto.PsychologistAdminRes;
import com.freemind.login.psychologist.dto.PsychologistProfileRes;
import com.freemind.login.psychologist.dto.PsychologistSelfRes;
import com.freemind.login.psychologist.dto.PsychologistUpdateReq;
import com.freemind.login.psychologist.entity.Expertise;
import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.entity.PsychologistExpertise;
import com.freemind.login.psychologist.repository.ExpertiseRepository;
import com.freemind.login.psychologist.repository.PsychologistExpertiseRepository;
import com.freemind.login.psychologist.repository.PsychologistRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

@Service
public class PsychologistService {
	
	private final PsychologistRepository psychologistRepository;
	//會員看心理師詳情時可以直接知道天可以預約典籍可以直接預約
	private final SlotsRepository slotsRepository;
	
	// 心理師更新時間後 更新區要確認預約訂單 更新專長 把心理師有的專長都刪了重新加
	private final OrdersRepository ordersRepository;
	private final PsychologistExpertiseRepository psychologistExpertiseRepository;
	private final ExpertiseRepository expertiseRepository;
	
	private final PasswordEncoder passwordEncoder;
	private final PersistentTokenRepository psychTokenRepository;
	
	private final NoticeService noticeService;
	
	private final int DAYS = 14;
	
	
	
	
	
	public PsychologistService(
			PsychologistRepository repository , SlotsRepository slotsRepository ,
			OrdersRepository ordersRepository , PsychologistExpertiseRepository psychologistExpertiseRepository , ExpertiseRepository expertiseRepository,
			PasswordEncoder passwordEncoder,
			NoticeService noticeService,
			@Qualifier("psychPersistentTokenRepository") PersistentTokenRepository psychTokenRepository) {
			 
		this.psychologistRepository = repository;
		this.slotsRepository = slotsRepository;
		this.ordersRepository = ordersRepository;
		this.psychologistExpertiseRepository = psychologistExpertiseRepository;
		this.expertiseRepository = expertiseRepository;
		this.passwordEncoder = passwordEncoder;
		this.psychTokenRepository = psychTokenRepository;
		this.noticeService = noticeService;
	}

	
	
	
	
	
	
	
	
	
	
	public void updatePsychologist(Psychologist psychologist) {
		psychologistRepository.save(psychologist);
	}
 
	//課程FOR心理師有用到
	public Psychologist getOnePsychologist(Integer psychologist) {
		Optional<Psychologist> optional = psychologistRepository.findById(psychologist);
		return optional.orElse(null);
	}
	
	
	public List<Psychologist> getAll() {
		return psychologistRepository.findAll();
	}
	
	public Psychologist findByAccount(String account) {
		return psychologistRepository.findByPsychAccount(account);
	}
 
	public Psychologist findByEmail(String email) {
		return psychologistRepository.findByEmail(email);
	}
	
	public Psychologist addPsychologist(Psychologist psychologist) {
		return psychologistRepository.save(psychologist);
	}
	
	
	
	public PsychologistAdminRes getAdmin(Integer psychId) {
		Psychologist p = psychologistRepository.findById(psychId)
				.orElseThrow(() -> new IllegalArgumentException("找不到心理師"));
		return toAdminRes(p);
	}
	
	@Transactional
	public void updateLicense(Integer psychId, boolean approved) {
		Psychologist p = psychologistRepository.findById(psychId)
				.orElseThrow(() -> new IllegalArgumentException("找不到心理師"));
		p.setHasPracticeLicense(approved);

		if (!approved) {
			cancelFutureOrdersAndNotify(psychId);
		}
	}

	// 帳號狀態:0未啟用 1已啟用 2停權。停權 => 取消未來訂單
	@Transactional
	public void updateAccountStatus(Integer psychId, Integer status) {
		if (status == null || status < 0 || status > 2) {
			throw new IllegalArgumentException("帳號狀態錯誤");
		}
		Psychologist p = psychologistRepository.findById(psychId)
				.orElseThrow(() -> new IllegalArgumentException("找不到心理師"));
		p.setAccountStatus(status);

		if (status == 2) {
			cancelFutureOrdersAndNotify(psychId);
		}
	}
	private void cancelFutureOrdersAndNotify(Integer psychId) {
		List<Orders> futureOrders = ordersRepository
				.findByPsychologist_PsychIdAndOrderStatusInAndConsStartGreaterThanEqual(
						psychId, List.of(0, 1), LocalDateTime.now());

		for (Orders o : futureOrders) {
			boolean wasConfirmed = o.getOrderStatus() == 1; // 取消前先記住,取消後就都是2了
			cancelOrder(o); // 共用改時段那套:orderStatus=2、slot該小時2->0
			if (wasConfirmed) {
				noticeService.sendToMember(o.getMember().getMemberId(), null,"很抱歉,心理師目前無法提供服務,您 "
						+ o.getConsStart().toLocalDate() + " 的預約已取消。", (byte) 0);
			}
		}
	}
//	//管理員看所有心理師
	public List<PsychologistAdminRes> adminSearch(String name, String gender, String psychLoc,
			Integer minFee, Integer maxFee, String expertiseName,
			Integer accountStatus, Boolean hasPracticeLicense,
			String orderBy){
		Specification<Psychologist> spec = buildSearchSpec(name, gender, psychLoc, minFee, maxFee, expertiseName);

		
		if (accountStatus != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("accountStatus"), accountStatus));
		}
		if (hasPracticeLicense != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("hasPracticeLicense"), hasPracticeLicense));
		}
		
		Sort sort = (orderBy == null || orderBy.isBlank())
				? Sort.by(Sort.Direction.DESC, "regisAt")   // 預設:最新註冊在前(對審核最順手)
				: ADMIN_ORDER_BY_MAP.getOrDefault(orderBy, Sort.by(Sort.Direction.DESC, "regisAt"));

		return psychologistRepository.findAll(spec, sort).stream().map(this::toAdminRes).toList();
		
		
	}
	
	private static final Map<String, Sort> ADMIN_ORDER_BY_MAP = Map.of(
			"nameAsc",   Sort.by(Sort.Direction.ASC, "name"),
			"nameDesc",  Sort.by(Sort.Direction.DESC, "name"),
			"feeAsc",    Sort.by(Sort.Direction.ASC, "psychFee"),
			"feeDesc",   Sort.by(Sort.Direction.DESC, "psychFee"),
			"regisAsc",  Sort.by(Sort.Direction.ASC, "regisAt"),
			"regisDesc", Sort.by(Sort.Direction.DESC, "regisAt")
	);
	
	//管理員看心理師
	private PsychologistAdminRes toAdminRes(Psychologist p) {
		PsychologistAdminRes res = new PsychologistAdminRes();
//		//心理師編號
//		private Integer psychId;
//		//心理師帳號	
//	    private String psychAccount;
//	    // 帳號狀態 0:未啟用 1:已啟用 2:停權
//	    private Integer accountStatus;
//	    private String name;
//	    private String gender;
//	    private String phoneNumber;
//	    private String email;
//	    // 心理師證照
//	    private String psychCertificate;
//	    // 審核狀態:是否有執業許可
//	    private Boolean hasPracticeLicense;
//	    private String psychLoc;
//	    private Integer psychFee;
//	    private String weeklyAvailability;
//	    private LocalDateTime regisAt;
//	    // 心理師照片
//	  	private String profilePic;
//	    // 銀行帳號 撥款需要
//	    private String bankAccount;
		res.setPsychId(p.getPsychId());
		res.setPsychAccount(p.getPsychAccount());
		res.setName(p.getName());
		res.setGender(p.getGender());
		res.setPhoneNumber(p.getPhoneNumber());
		res.setEmail(p.getEmail());
		res.setPsychCertificate(p.getPsychCertificate());
		res.setHasPracticeLicense(p.getHasPracticeLicense());
		res.setPsychLoc(p.getPsychLoc());
		res.setPsychFee(p.getPsychFee());
		res.setWeeklyAvailability(p.getWeeklyAvailability());
		res.setRegisAt(p.getRegisAt());
		res.setProfilePic(p.getProfilePic() !=null ? p.getProfilePic() : "");
		res.setBankAccount(p.getBankAccount());
		res.setAccountStatus(p.getAccountStatus());
		return res ;
	}
	

	
	
//	public List<PsychologistSelfRes> getPsychAll(){
//		List<Psychologist> all = psychologistRepository.findAll();
//		List<PsychologistSelfRes> result = new ArrayList<>();
//		
//		for(Psychologist p : all) {
//			if(p.getAccountStatus()==1) {
//				result.add(toPsychSelfRes(p));
//			}
//		}
//		return result;
//	}
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	//===========================心理師===========================
	@Transactional(readOnly = true)
	public PsychologistSelfRes getPsychSelf(Integer psychId) {
		
		Psychologist p = psychologistRepository.findById(psychId)
				.orElseThrow(()-> new  IllegalArgumentException("找不到心理師"));
//		Optional<Psychologist> box = repository.findById(psychId);
//		
//		if(box.isEmpty()) {
//			throw new IllegalArgumentException("找不到心理師");
//		}
//		Psychologist p =box.get();
		return toPsychSelfRes(p);
	}
	
	//心理師更新自己基本資料 -> 資料庫
	@Transactional
	public List<ConflictOrderRes> updateSelf(Integer psychId , PsychologistUpdateReq req){
		
		Psychologist p = psychologistRepository.findById(psychId)
				.orElseThrow(() -> new IllegalArgumentException("找不到心理師"));
		
		String newAvail = req.getWeeklyAvailability();
		boolean availChanged = newAvail != null && !newAvail.isBlank() && !newAvail.equals(p.getWeeklyAvailability());
		
		boolean locChanged = !req.getPsychLoc().equals(p.getPsychLoc());
		
		
		if(availChanged) {
			List<Orders> conflictOrders = findConflictOrders(psychId , newAvail);
		
	
			if(!conflictOrders.isEmpty()) {
				Set<Integer> keepIds = req.getKeepOrderIds() == null ? Set.of() : Set.copyOf(req.getKeepOrderIds());
				Set<Integer> cancelIds = req.getCancelOrderIds() == null ? Set.of() : Set.copyOf(req.getCancelOrderIds());
			
				
			List<ConflictOrderRes> undecided = conflictOrders.stream()
					.filter(o -> !keepIds.contains(o.getOrderId()) && !cancelIds.contains(o.getOrderId()))
					.map(o -> new ConflictOrderRes(o.getOrderId() , o.getConsStart() , o.getConsEnd() , o.getOrderStatus()))
					.toList();
			
			if(!undecided.isEmpty()) {
				return undecided;
			}
			for(Orders o : conflictOrders) {
				//把有改的時間的訂單到concelOrder
				if(cancelIds.contains(o.getOrderId())) {
					cancelOrder(o);
					noticeService.sendToMember(o.getMember().getMemberId(), null, 
							"很抱歉,心理師 " + p.getName() + " 因班表異動,您 "
							+ o.getConsStart().toLocalDate() + " "
							+ o.getConsStart().toLocalTime() + " 的預約已取消。", (byte) 0);
				}
			  }
			}
		
		closeSlotsByNewTemplate(psychId , newAvail);
		
		p.setWeeklyAvailability(newAvail);
		
		}
		
		p.setPhoneNumber(req.getPhoneNumber());
		p.setEmail(req.getEmail());
		p.setPsychLoc(req.getPsychLoc());
		p.setPsychFee(req.getPsychFee());
		
		if(req.getProfilePic() != null && !req.getProfilePic().isBlank()) {
			p.setProfilePic(req.getProfilePic());
		}
		
		if(req.getBankAccount() != null && !req.getBankAccount().isBlank()) {
			p.setBankAccount(req.getBankAccount());
		}
		
		if(req.getExpertiseIds() != null) {
			replaceExpertises(p , req.getExpertiseIds());
		}
		
		
		psychologistRepository.save(p);
		
		ensureNextDaysSlots(p);
		
		if(locChanged) {
			List<Orders> future = ordersRepository.findByPsychologist_PsychIdAndOrderStatusInAndConsStartGreaterThanEqual(psychId , List.of(0,1) , LocalDateTime.now());
		
			for(Orders o : future) {
				noticeService.sendToMember(o.getMember().getMemberId() , null , 
						"心理師" + p.getName() + "的工作地點已更新為「" + req.getPsychLoc() + "」,您" + o.getConsStart().toLocalDate() + "的預約請留意" , (byte)0);
				
			}
		}
	
		return List.of();
		
	}
	
	
	@Transactional
	public void changePassword(Integer psychId , PsychologistPasswordReq req) {
		Psychologist p =psychologistRepository.findById(psychId)
				.orElseThrow(() -> new IllegalArgumentException("找不到心理師"));
		
		if (!passwordEncoder.matches(req.getOldPassword(), p.getPsychPassword())) {
			throw new IllegalArgumentException("舊密碼錯誤");
		}
		if (!req.getNewPassword().equals(req.getConfirmPassword())) {
			throw new IllegalArgumentException("兩次新密碼不一致");
		}
		if (passwordEncoder.matches(req.getNewPassword(), p.getPsychPassword())) {
			throw new IllegalArgumentException("新密碼不可與舊密碼相同");
		}

		p.setPsychPassword(passwordEncoder.encode(req.getNewPassword()));

		// persistent_logins 裡這個帳號的 token 全刪:被偷的 remember-me cookie 立即失效
		psychTokenRepository.removeUserTokens(p.getPsychAccount());
	}
	
	
	//找新上班時間有撞到的訂單
	private List<Orders> findConflictOrders(Integer psychId , String newAvail){
		
		List<Orders> futureOrders = ordersRepository.findByPsychologist_PsychIdAndOrderStatusInAndConsStartGreaterThanEqual(psychId, List.of(0 , 1), LocalDateTime.now());
		
		List<Orders> conflicts = new ArrayList<>();
		for(Orders o : futureOrders) {
			int dayOfWeek = o.getConsStart().getDayOfWeek().getValue();
			int hour = o.getConsStart().getHour();
			int idx = (dayOfWeek - 1)*24 + hour;
			
			if(newAvail.charAt(idx) == '0') {
				conflicts.add(o);
			}
		}
		
		return conflicts;
	}
	
	//取消定單 把預約狀態 已預約(2)改不可預約(0)
	private void cancelOrder(Orders o) {
		o.setOrderStatus(2);
		
		Slots slot = o.getSlot();
		if(slot != null && slot.getConsStatus() != null) {
			int hour = o.getConsStart().getHour();
			char[] cs = slot.getConsStatus().toCharArray();
			if(hour < cs.length && cs[hour] == '2') {
				cs[hour] = '0';
				slot.setConsStatus(new String(cs));
			}
		}
		
	}
	
	
	//確認排編 不是null 是 168 且有至少1個小時上班
	private boolean hasValidTemplate(String tpl) {
		return tpl != null && tpl.length() == 168 && tpl.contains("1");
	}
	//補時間 會員點心理師詳情 心理師更新資料
	private void ensureNextDaysSlots(Psychologist p) {
		String tpl = p.getWeeklyAvailability();
		if(!hasValidTemplate(tpl)) {
			return;
		}
		
		LocalDate today = LocalDate.now();
		LocalDate endDate = today.plusDays(DAYS);
		
		Set<LocalDate> existingDates = slotsRepository
				.findByPsychIdAndSlotDateBetween(p.getPsychId(), today, endDate)
				.stream()
				.map(Slots::getSlotDate)
				.collect(Collectors.toSet());
		
		for(int i = 0 ; i <= DAYS ; i++) {
			LocalDate date = today.plusDays(i);
			if(existingDates.contains(date)) {
				continue;
			}
			int dayOfWeek = date.getDayOfWeek().getValue();
			String dat24 = tpl.substring((dayOfWeek - 1)*24 , (dayOfWeek - 1)*24 + 24);
			
			Slots s = new Slots();
			s.setPsychologist(p);
			s.setSlotDate(date);
			s.setConsStatus(dat24);
			slotsRepository.save(s);
			
		}
	}
	
	
	
	//把可預約關掉
	private void closeSlotsByNewTemplate(Integer psychId , String newAvail) {
		LocalDate today = LocalDate.now();
		List<Slots> slotsList = slotsRepository.
				findByPsychIdAndSlotDateBetween(psychId , today , today.plusDays(DAYS));
		
		for(Slots slot : slotsList) {
			if(slot.getConsStatus() == null) {
				continue;
			}
			int dayOfWeek = slot.getSlotDate().getDayOfWeek().getValue();
			char[] cs = slot.getConsStatus().toCharArray();
			boolean changed = false;
			
			for(int h = 0 ; h < 24 && h < cs.length ; h++) {
				int idx = (dayOfWeek - 1)*24 + h;
				if(newAvail.charAt(idx) == '0' && cs[h] == '1') {
					cs[h] = '0';
					changed = true;
				}
				if(newAvail.charAt(idx) == '1' && cs[h] == '0') {
					cs[h] = '1';
					changed = true;
				}
			}	
			if(changed) {
				slot.setConsStatus(new String(cs));
			}
				
			
		}
	}
	
	
	
	//把專長刪了重設定
	private void replaceExpertises(Psychologist p , List<Integer> expertiseIds) {
		psychologistExpertiseRepository.deleteByCompositeExpertiseDetail_PsychId(p.getPsychId());
		psychologistExpertiseRepository.flush();
		List<PsychologistExpertise> newLinks = new ArrayList<>();
		for(Integer expertiseId : expertiseIds) {
			Expertise e = expertiseRepository.findById(expertiseId).
					orElseThrow(() -> new IllegalArgumentException("找不到專長：" + expertiseId));
			
			PsychologistExpertise pe = new PsychologistExpertise(); 
			pe.setCompositeExpertiseDetail(
					new PsychologistExpertise.CompositeExpertiseDetail(p.getPsychId() , expertiseId));
			pe.setPsychologist(p);
			pe.setExpertise(e);
			newLinks.add(psychologistExpertiseRepository.save(pe));
		
		}
		
		if(p.getPsychologistExpertises() != null) {
			p.getPsychologistExpertises().clear();
			p.getPsychologistExpertises().addAll(newLinks);
			
		}
		
	}
	
	
	
	
	
	
	//心理師看子幾
		private PsychologistSelfRes toPsychSelfRes(Psychologist p) {
			PsychologistSelfRes res = new PsychologistSelfRes();
//			private String psychAccount;
//		    // 帳號狀態 0:未啟用 1:已啟用 2:停權
//		    private Integer accountStatus;
//			private String name;
//		    private String gender;
//			private String phoneNumber;
//			private String email;
//			// 心理師證照
//			private String psychCertificate;
//		    // 審核狀態:是否有執業許可
//		    private Boolean hasPracticeLicense;
//			private String psychLoc;
//			private Integer psychFee;
//			private String weeklyAvailability;
//		    private Timestamp regisAt;
//			 // 心理師照片
//		  	private String profilePic;
//			private String bankAccount;
			res.setPsychId(p.getPsychId());
			res.setPsychAccount(p.getPsychAccount());
			res.setName(p.getName());
			res.setGender(p.getGender());
			res.setPhoneNumber(p.getPhoneNumber());
			res.setEmail(p.getEmail());
			res.setPsychCertificate(p.getPsychCertificate());
			res.setHasPracticeLicense(p.getHasPracticeLicense());
			res.setPsychLoc(p.getPsychLoc());
			res.setPsychFee(p.getPsychFee());
			res.setWeeklyAvailability(p.getWeeklyAvailability());
			res.setRegisAt(p.getRegisAt());
			res.setProfilePic(p.getProfilePic() != null ? p.getProfilePic() : "");
			res.setBankAccount(p.getBankAccount());
			
			res.setAccountStatus(p.getAccountStatus());
			
			if(p.getPsychologistExpertises() != null) {
				List<ExpertiseRes> expertiseList = p.getPsychologistExpertises()
						.stream().map(pe -> new ExpertiseRes(pe.getExpertise().getExpertiseId() ,
															pe.getExpertise().getExpertiseName())).toList();
			
				res.setExpertiseList(expertiseList);
			}else {
				res.setExpertiseList(List.of());
			}
			return res ;
		}
	
	
	//====================會員====================
	//會員看所有心理師

	//LIST查詢
	@Transactional(readOnly = true)
	public List<PsychologistProfileRes> search(String name , String gender , String psychLoc ,
												Integer minFee , Integer maxFee ,
												String expertiseName , LocalDate slotDate,
												String orderBy ){
		if(slotDate != null) {
			LocalDate today = LocalDate.now();
			LocalDate maxDate = today.plusDays(14);
			if(slotDate.isBefore(today)||slotDate.isAfter(maxDate)) {
				throw new IllegalArgumentException("查詢日期僅限今天起14天內");
			}
		}
		
		
		
		
		Specification<Psychologist> spec = buildSearchSpec(name, gender, psychLoc, minFee, maxFee, expertiseName);
		spec = spec.and((root, query, cb) -> cb.equal(root.get("accountStatus"), (byte) 1));
		spec = spec.and((root, qurey, cb) -> cb.isTrue(root.get("hasPracticeLicense")));

        //查某天可否預約
		if(slotDate != null) {
			spec =spec.and((root , query , cb)->{
				query.distinct(true);
				Join<Psychologist , Slots> slotJoin = root.join("consultationSlots",JoinType.INNER);
				return cb.and(
						cb.equal(slotJoin.get("slotDate"), slotDate),
						cb.or(
						cb.like(slotJoin.get("consStatus"), "%1%"),
						cb.like(slotJoin.get("consStatus"), "%4%"))
						);
			});
		}
		
	Sort sort = buildSort(orderBy);
	
	return psychologistRepository.findAll(spec , sort).stream()
			.map(this::toProfileRes).toList();
}
	
	
	//可以排序的白名單 前端名稱->entity名稱
	private static final Map<String , Sort> ORDER_BY_MAP  = Map.of(
			"nameAsc",     Sort.by(Sort.Direction.ASC, "name"),
	        "nameDesc",    Sort.by(Sort.Direction.DESC, "name"),
	        "feeAsc",      Sort.by(Sort.Direction.ASC, "psychFee"),
	        "feeDesc",     Sort.by(Sort.Direction.DESC, "psychFee"),
	        "genderAsc",   Sort.by(Sort.Direction.ASC, "gender"),
	        "genderDesc",  Sort.by(Sort.Direction.DESC, "gender")
			);
	 
	private Sort buildSort(String orderBy) {
		
		if(orderBy == null || orderBy.isBlank()) {
			return Sort.unsorted();
		}
		//不是我SORTABLE_FIELDS裡的欄位就略過

		
		return ORDER_BY_MAP.getOrDefault(orderBy , Sort.unsorted());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//會員看心理師詳細資料
	@Transactional 
	public PsychologistProfileRes getProfile(Integer psychId) {
		Psychologist p = psychologistRepository.findById(psychId).
				orElseThrow(()-> new IllegalArgumentException("找不到心理師") );
		
		
//		Optional<Psychologist> box = repository.findById(psychId);
//		if(box.isEmpty()) {
//			throw new IllegalArgumentException("找不到心理師");
//		}
//		Psychologist p = box.get();
		
		ensureNextDaysSlots(p);
		
		PsychologistProfileRes res = toProfileRes(p);
		res.setAvailableDates(buildNext14DaysCalender(psychId));
		
		
		return res;
	}
	
	
	private List<AvailableDateRes> buildNext14DaysCalender(Integer psychId){
		//找今天到14天 
		LocalDate today = LocalDate.now();
		LocalDate endDate = today.plusDays(14);
		
		
		//抓時間的資料
		List<Slots> slotsList = slotsRepository
				.findByPsychIdAndSlotDateBetween(psychId, today, endDate);
		//Key Value
		Map<LocalDate , String > statusByDate = slotsList.stream().
				collect(Collectors.toMap(Slots::getSlotDate , Slots::getConsStatus));
		//空白
		List<AvailableDateRes> calender = new ArrayList<>();
		
		//算今天周幾 對其 心理師詳細資料 星期位置
		int todayDayOfWeek = today.getDayOfWeek().getValue();
		//看藥補幾個空格 周一開始所以周一不用補所以-1
		int leadingEmptyCount = todayDayOfWeek - 1;
		
		for(int i = 0 ; i < leadingEmptyCount ; i++) {
			AvailableDateRes emptyRes = new AvailableDateRes();
			emptyRes.setEmpty(true);
			calender.add(emptyRes);
		}
		
		
		for(int i = 0 ; i <= 14 ; i++ ) {
			LocalDate date = today.plusDays(i);
			String consStatus = statusByDate.get(date);
			
			AvailableDateRes dateRes = new AvailableDateRes();
			dateRes.setDate(date);
			//看預約狀態
			dateRes.setHasAvailability(consStatus != null && (consStatus.contains("1") || consStatus.contains("4")));
			calender.add(dateRes);
		}
		
		return calender;
		
	}
	
	
	
	//會員看心理師
	private PsychologistProfileRes toProfileRes(Psychologist p) {		
//		private Integer psychId; 
//		//姓名
//		private String name;
//		//性別
//		private String gender;
//		//心理師證照
//		private String psychCertificate;
//		//是否有執業許可
//		private boolean hasPracticeLicense;
//		//心理師工作地點
//		private String psychLoc;
//		//心理師諮詢費
//		private Integer psychFee;
//		//心理師照片
//		private String profilePic;
		PsychologistProfileRes res = new PsychologistProfileRes();
		res.setPsychId(p.getPsychId());
		res.setName(p.getName());
		res.setGender(p.getGender());
		res.setPsychCertificate(p.getPsychCertificate());
		res.setHasPracticeLicense(p.getHasPracticeLicense());
		res.setPsychLoc(p.getPsychLoc());
		res.setPsychFee(p.getPsychFee());
		//照片
		res.setProfilePic(p.getProfilePic() != null ? p.getProfilePic() : "");
//		if(p.getProfilePic()==null) {
//			res.setProfilePic("");//沒照片船的
//		}else {
//			res.setProfilePic(p.getProfilePic());
//		}
//			
		
		res.setScheduled(hasValidTemplate(p.getWeeklyAvailability()));
		
		if (p.getPsychologistExpertises() != null) {
	        List<String> expertiseNames = p.getPsychologistExpertises().stream()
	                .map(pe -> pe.getExpertise().getExpertiseName())
	                .toList();
	        res.setExpertiseList(expertiseNames);
	    }
		
		
		return res;
	}
	
	// ===== 共用 會員 管理員
		private Specification<Psychologist> buildSearchSpec(String name, String gender, String psychLoc,
				Integer minFee, Integer maxFee, String expertiseName) {

			Specification<Psychologist> spec = Specification.allOf();

			if (name != null && !name.isBlank()) {
				spec = spec.and((root, query, cb) -> cb.like(root.get("name"), "%" + name + "%"));
			}
			if (gender != null && !gender.isBlank()) {
				spec = spec.and((root, query, cb) -> cb.equal(root.get("gender"), gender));
			}
			if (psychLoc != null && !psychLoc.isBlank()) {
				spec = spec.and((root, query, cb) -> cb.like(root.get("psychLoc"), "%" + psychLoc + "%"));
			}
			if (minFee != null) {
				spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("psychFee"), minFee));
			}
			if (maxFee != null) {
				spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("psychFee"), maxFee));
			}
			if (expertiseName != null && !expertiseName.isBlank()) {
				spec = spec.and((root, query, cb) -> {
					query.distinct(true);
					Join<Psychologist, PsychologistExpertise> peJoin = root.join("psychologistExpertises", JoinType.INNER);
					Join<PsychologistExpertise, Expertise> expJoin = peJoin.join("expertise", JoinType.INNER);
					return cb.equal(expJoin.get("expertiseName"), expertiseName);
				});
			}
			return spec;
		}
}
