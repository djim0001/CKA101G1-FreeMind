package com.freemind.activity.activity.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.activity.activity.util.HibernateUtil_CompositeQuery_Activity;
import com.freemind.activity.registration.model.Registration;
import com.freemind.activity.registration.model.RegistrationRepository;
import com.freemind.login.notice.service.NoticeService;

@Service
public class ActivityService {
	
	@Autowired
	private ActivityRepository repository;
	
	@Autowired
	private RegistrationRepository regisRepo;
	
	@Autowired
	private  SessionFactory sessionFactory;
	
	@Autowired
	private NoticeService noticeService;
	
	// 精選活動用
	private static final int PUBLISHED_STATUS = 2;
	
	public void addActivity(Activity activity) {
	    LocalDateTime now = LocalDateTime.now();

	    if (!activity.getRegisStart().isAfter(now)) {
	        throw new RuntimeException("報名開始時間，必須晚於現在時間");
	    }
	    if (!activity.getRegisEnd().isAfter(activity.getRegisStart())) {
	        throw new RuntimeException("報名截止時間，必須晚於報名開始時間");
	    }
	    if (!activity.getActivityStart().isAfter(activity.getRegisEnd())) {
	        throw new RuntimeException("活動開始時間，必須晚於報名截止時間");
	    }
	    if (!activity.getActivityEnd().isAfter(activity.getActivityStart())) {
	        throw new RuntimeException("活動結束時間，必須晚於活動開始時間");
	    }
		
	    if (activity.getCapacity() == null || activity.getCapacity() < 1) {
	        throw new RuntimeException("正取名額不能小於1");
	    }
	    if (activity.getWaitlistCapacity() == null || activity.getWaitlistCapacity() < 0) {
	        throw new RuntimeException("備取名額不能小於0");
	    }
	    
		activity.setActivityStatus(0);  // 活動狀態預設為「待審核」
		activity.setRegisCount(0);      // 報名人數預設為0
		activity.setWaitlistCount(0);   // 備取人數預設為0
		activity.setCreatedAt(LocalDateTime.now());  
		repository.save(activity);
	}
	
	public void updateActivity(Activity activity) {
		// 用活動ID，回頭去資料庫，把『目前實際存在資料庫裡』的這筆資料，重新查一次」——可知道「修改前」的真實狀態
		Activity existing = repository.findById(activity.getActivityId()).orElse(null);
		    
		// 發起者只能修改活動狀態為0：待審核、3：已退回的活動，其他活動狀態不可修改
	    if (existing == null || (existing.getActivityStatus() != 0 && existing.getActivityStatus() != 3)) {
	        throw new RuntimeException("此活動目前不可修改");
	    }
	    
	    activity.setMember(existing.getMember());   // 找出原本的發起人，設定到即將要存檔的activity身上
	    activity.setRegisCount(existing.getRegisCount()); // 補回目前報名人數，不讓使用者的表單洗掉這個值
	    activity.setWaitlistCount(existing.getWaitlistCount());
	    
	    // 保留原始圖片
	    if (activity.getPicture() == null) { 
	        activity.setPicture(existing.getPicture());
	    }
	    
	    activity.setUpdatedAt(LocalDateTime.now());
	    activity.setActivityStatus(0);  // 修改後，活動狀態重新改回待審核
	    repository.save(activity);
		
	}
	
	public void deleteActivity(Integer activityId) {
		if(repository.existsById(activityId))
			repository.deleteById(activityId);
	}
	
	public Activity getOneActivity(Integer activityId) {
		Optional<Activity> optional = repository.findById(activityId);
		return optional.orElse(null);
	}
	
	public List<Activity> getAll(){
		return repository.findAll();
	}
	
	// 給會員使用的複合查詢
	public List<Activity> getAllForMember(Map<String, String[]> map, Integer currentPage) {
	    return HibernateUtil_CompositeQuery_Activity.getAllC_ForMember(map, currentPage, sessionFactory.openSession());
	}
	
	// 分頁功能，算資料筆數
	public long getTotalCountForMember(Map<String, String[]> map) {
	    return HibernateUtil_CompositeQuery_Activity.getTotalCount_ForMember(map, sessionFactory.openSession());
	}
	
	// 查看自己發起的活動
	public List<Activity> getAllForOwner(Map<String, String[]> map, Integer memberId, Integer currentPage) {
	    return HibernateUtil_CompositeQuery_Activity.getAllC_ForOwner(map, memberId, currentPage, sessionFactory.openSession());
	}
	
	// 分頁功能，算資料筆數
	public long getTotalCountForOwner(Map<String, String[]> map, Integer memberId) {
	    return HibernateUtil_CompositeQuery_Activity.getTotalCount_ForOwner(map, memberId, sessionFactory.openSession());
	}
	
	// 發起者取消活動
	public void cancelActivity(Integer activityId, String cancelNote) {
	    Activity existing = repository.findById(activityId).orElse(null);

	    if (existing == null) {
	        throw new RuntimeException("查無此活動");
	    }

	    Integer status = existing.getActivityStatus();
	    if (status != 0 && status != 1 && status != 2 && status != 5) {
	        throw new RuntimeException("此活動目前不可取消");
	    }
	    
	    if (existing.getActivityEnd() != null && LocalDateTime.now().isAfter(existing.getActivityEnd())) {
	        throw new RuntimeException("活動已經結束，無法取消");
	    }

	    existing.setActivityStatus(4);  // 改成「取消」
	    existing.setCancelNote(cancelNote);  // 記錄取消原因
	    existing.setUpdatedAt(LocalDateTime.now());
	    repository.save(existing);
	    
	    List<Registration> confirmedRegis = regisRepo.findByActivityAndRegisStatusIn(existing, List.of(0, 1, 4));
	    for (Registration r : confirmedRegis) {
	        noticeService.sendToMember(r.getMember().getMemberId(), null,
	            "很抱歉，您報名的活動「" + existing.getActivityName() + "」已被發起人取消。", (byte) 2);
	    }
	}
	
	// 發起者延期已發布的活動
	public void postponeActivity(Integer activityId, String postponeNote) {
	    Activity existing = repository.findById(activityId).orElse(null);

	    if (existing == null || existing.getActivityStatus() != 2) {
	        throw new RuntimeException("此活動目前不可延期");
	    }
	    
	    if (existing.getActivityEnd() != null && LocalDateTime.now().isAfter(existing.getActivityEnd())) {
	        throw new RuntimeException("活動已經結束，無法延期");
	    }

	    existing.setActivityStatus(5);
	    existing.setPostponeNote(postponeNote);
	    existing.setUpdatedAt(LocalDateTime.now());
	    repository.save(existing);
	    
	
	    List<Registration> confirmedRegis = regisRepo.findByActivityAndRegisStatusIn(existing, List.of(0, 1, 4));
	    for (Registration r : confirmedRegis) {
	        noticeService.sendToMember(r.getMember().getMemberId(), null,
	            "很抱歉，您報名的活動「" + existing.getActivityName() + "」已延期，請留意新的活動時間。", (byte) 2);
	    }
	}
	
	// 發起者變更需延期活動的時間
	public void confirmNewSchedule(Integer activityId, LocalDateTime activityStart, LocalDateTime activityEnd,
	        LocalDateTime regisStart, LocalDateTime regisEnd) {
		
		
		Activity existing = repository.findById(activityId).orElse(null);

		if (existing == null || existing.getActivityStatus() != 5) {
			throw new RuntimeException("此活動目前無法變更時間");
		}

		// 選填欄位若未填，沿用資料庫原本的值，作為驗證比較基準
		LocalDateTime finalRegisStart = (regisStart != null) ? regisStart : existing.getRegisStart();
		LocalDateTime finalRegisEnd = (regisEnd != null) ? regisEnd : existing.getRegisEnd();

		
		// 時間先後順序驗證（比照新增活動的驗證邏輯）
		if (!finalRegisEnd.isAfter(finalRegisStart)) {
			throw new IllegalArgumentException("報名截止時間，必須晚於報名開始時間");
		}
		if (!activityStart.isAfter(finalRegisEnd)) {
			throw new IllegalArgumentException("活動開始時間，必須晚於報名截止時間");
		}
		if (!activityEnd.isAfter(activityStart)) {
			throw new IllegalArgumentException("活動結束時間，必須晚於活動開始時間");
		}
		existing.setActivityStart(activityStart);
		existing.setActivityEnd(activityEnd);

		if (regisStart != null) {
			existing.setRegisStart(regisStart);
		}
		if (regisEnd != null) {
			existing.setRegisEnd(regisEnd);
		}

		existing.setActivityStatus(2);
		existing.setUpdatedAt(LocalDateTime.now());
		repository.save(existing);

		List<Registration> confirmedRegis = regisRepo.findByActivityAndRegisStatusIn(existing, List.of(0, 1, 4));
		for (Registration r : confirmedRegis) {
			noticeService.sendToMember(r.getMember().getMemberId(), null,
				"您報名的活動「" + existing.getActivityName() + "」新時間已確定為 "
				+ existing.getActivityStart().toLocalDate() + " " + existing.getActivityStart().toLocalTime() + "，請留意。", (byte) 2);
		}
	}
	
	// 後台員工手動發布活動
	public void publishActivity(Integer activityId) {
	    Activity existing = repository.findById(activityId).orElse(null);

	    if (existing == null || existing.getActivityStatus() != 1) {
	        throw new RuntimeException("此活動目前不可發布");
	    }

	    existing.setActivityStatus(2);  // 改成「已發布」
	    existing.setPublishedAt(LocalDateTime.now());  // 發布時間，自動寫入當下
	    existing.setUpdatedAt(LocalDateTime.now());
	    repository.save(existing);
	    
	    noticeService.sendToMember(existing.getMember().getMemberId(), null,
	    	    "您發起的活動「" + existing.getActivityName() + "」已正式發布。", (byte) 2);
	}
	
	// 後台員工查看活動列表
	public List<Activity> getAllForAdmin(Map<String, String[]> map, Integer currentPage) {
	    return HibernateUtil_CompositeQuery_Activity.getAllC_ForAdmin(map, currentPage, sessionFactory.openSession());
	}

	public long getTotalCountForAdmin(Map<String, String[]> map) {
	    return HibernateUtil_CompositeQuery_Activity.getTotalCount_ForAdmin(map, sessionFactory.openSession());
	}
	
	// 後台員工審核活動
	public void approveActivity(Integer activityId) {
	    Activity existing = repository.findById(activityId).orElse(null);

	    if (existing == null || existing.getActivityStatus() != 0) {
	        throw new RuntimeException("此活動目前不可審核");
	    }

	    existing.setActivityStatus(1);  // 改成「已審核」
	    existing.setReviewedAt(LocalDateTime.now());
	    existing.setUpdatedAt(LocalDateTime.now());
	    repository.save(existing);
	    
	    noticeService.sendToMember(existing.getMember().getMemberId(), null,
	    	    "您發起的活動「" + existing.getActivityName() + "」已通過審核。", (byte) 2);
	}
	
	// 後台員工退回活動
	public void rejectActivity(Integer activityId, Integer rejectReason, String rejectNote) {
	    Activity existing = repository.findById(activityId).orElse(null);

	    if (existing == null || existing.getActivityStatus() != 0) {
	        throw new RuntimeException("此活動目前不可退回");
	    }

	    existing.setActivityStatus(3);  // 改成「已退回」
	    existing.setRejectReason(rejectReason);
	    existing.setRejectNote(rejectNote);
	    existing.setReviewedAt(LocalDateTime.now());
	    existing.setUpdatedAt(LocalDateTime.now());
	    repository.save(existing);
	    
	    noticeService.sendToMember(existing.getMember().getMemberId(), null,
	    	    "您發起的活動「" + existing.getActivityName() + "」審核未通過，請查看退回原因並修改。", (byte) 2);
	}
	
	// 排程發布:檢查所有已審核且排程時間已到的活動
	@Transactional
	public void publishScheduledActivities() {
	    List<Activity> readyList = repository.findByActivityStatusAndScheduledPublishAtLessThanEqual(1, LocalDateTime.now());
	    for (Activity activity : readyList) {
	        activity.setActivityStatus(2);
	        activity.setPublishedAt(LocalDateTime.now());
	        activity.setUpdatedAt(LocalDateTime.now());

	        noticeService.sendToMember(activity.getMember().getMemberId(), null,
	            "您發起的活動「" + activity.getActivityName() + "」已依排程正式發布。", (byte) 2);
	    }
	    if (!readyList.isEmpty()) {
	        System.out.println("排程發布完成,共發布 " + readyList.size() + " 筆活動");
	    }
	}
	
	// 排程設定:設定預定發布時間,活動維持已審核狀態,等排程器發布
	@Transactional
	public void schedulePublishActivity(Integer activityId, LocalDateTime scheduledAt) {
	    Activity existing = repository.findById(activityId).orElseThrow(() -> new IllegalArgumentException("活動不存在"));

	    if (existing.getActivityStatus() != 1) {
	        throw new IllegalStateException("此活動非為已審核狀態，無法設定排程發布");
	    }

	    if (!scheduledAt.isAfter(LocalDateTime.now())) {
	        throw new IllegalArgumentException("排程發布時間必須晚於現在時間");
	    }

	    existing.setScheduledPublishAt(scheduledAt);
	    existing.setUpdatedAt(LocalDateTime.now());
	    repository.save(existing);

	    noticeService.sendToMember(existing.getMember().getMemberId(), null,
	        "您發起的活動「" + existing.getActivityName() + "」已通過審核，將於 "
	        + scheduledAt.toLocalDate() + " " + scheduledAt.toLocalTime() + " 正式發布。", (byte) 2);
	}
	
	// 精選活動：從已發布且尚未結束的活動中，隨機抽 count 筆
	public List<Activity> getFeaturedActivities(int count) {
	    List<Activity> eligible = repository.findByActivityStatusAndActivityEndAfter(
	        PUBLISHED_STATUS, LocalDateTime.now());

	    // 濾掉已額滿的活動：精選活動應該要能報名，額滿的不適合出現在這裡
	    List<Activity> notFull = new ArrayList<>();
	    for (Activity a : eligible) {
	        if (!a.isFull()) {
	            notFull.add(a);
	        }
	    }

	    // Fisher-Yates 洗牌：從陣列尾端開始，每輪跟前面隨機一個位置交換
	    Random random = new Random();
	    for (int i = notFull.size() - 1; i > 0; i--) {
	        int j = random.nextInt(i + 1);
	        Activity temp = notFull.get(i);
	        notFull.set(i, notFull.get(j));
	        notFull.set(j, temp);
	    }

	    // 取前 count 筆，不足 count 筆就全部回傳
	    List<Activity> result = new ArrayList<>();
	    int limit = Math.min(count, notFull.size());
	    for (int i = 0; i < limit; i++) {
	        result.add(notFull.get(i));
	    }
	    return result;
	}
	
	// 依活動狀態算總數(不分頁,給後台統計卡片用)
	public long countByStatus(Integer status) {
	    return repository.countByActivityStatus(status);
	}
}
