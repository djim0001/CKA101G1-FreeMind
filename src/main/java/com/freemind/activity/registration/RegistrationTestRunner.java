package com.freemind.activity.registration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.activity.activity.model.Activity;
import com.freemind.activity.activity.model.ActivityRepository;
import com.freemind.activity.registration.model.Registration;
import com.freemind.activity.registration.model.RegistrationRepository;
import com.freemind.activity.registration.model.RegistrationService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberRepository;

//@Component
public class RegistrationTestRunner implements CommandLineRunner {

	@Autowired
	private RegistrationRepository regisRepo;
	@Autowired
	private MemberRepository memberRepo;
	@Autowired
	private ActivityRepository activityRepo;
	
	@Autowired
	private RegistrationService service;


	@Override
	public void run(String... args) throws Exception {
		System.out.println("測試");
		// 先撈測試主角(會員1)
		                                // 沒有傳入資料則會丟出例外
		Member m1 = memberRepo.findById(1).orElseThrow();
		Activity a1 = activityRepo.findById(1).orElseThrow(); // 會員1有報名的活動(活動1)
		Activity a2 = activityRepo.findById(2).orElseThrow(); // 會員1已取消報名(活動2)
		Activity a4 = activityRepo.findById(4).orElseThrow(); // 會員1沒有報名(活動4)
//		System.out.println("主角就位:" + m1.getName() + " / " + a1.getActivityName());
		
		
//		System.out.println("----- 測試1:擋重複報名 -----");
//		// 測試1-1：預期 true (1號會員有報名1號活動，狀態為0待審核)
//		boolean test1 = regisRepo.existsByMemberAndActivityAndRegisStatusIn(m1, a1, List.of(0, 1));
//		System.out.println("對1號活動有有效報名?" + test1 + "(預期 true)");
//		// 測試1-2：預期 false (1號會員已取消2號活動，狀態為3已取消)
//		boolean test2 = regisRepo.existsByMemberAndActivityAndRegisStatusIn(m1, a2, List.of(0, 1));
//		System.out.println("對2號活動有有效報名?" + test2 + "(預期 false,已取消不算)");
//		// 測試1-3：預期 false (1號會員沒有報名4號活動)
//		boolean test3 = regisRepo.existsByMemberAndActivityAndRegisStatusIn(m1, a4, List.of(0, 1));
//		System.out.println("對4號活動有有效報名?" + test3 + "(預期 false,沒報名過)");
//		
		
		// 測試2：印筆數 + 每筆的活動名稱,狀態,報名時間(驗排序)
//		System.out.println("----- 測試2:我的報名清單 -----");
//		List<Registration> myList = regisRepo.findByMemberWithActivity(m1);
//		System.out.println("1號會員共有 " + myList.size() + " 筆報名");
//		for (Registration r : myList) {
//		    System.out.println(r.getActivity().getActivityName()
//		            + " / 狀態:" + r.getRegisStatusText()
//		            + " / 報名時間:" + r.getRegisAt());
//		}
		
		// 測試3：印筆數 + 每筆的會員名稱,狀態,報名時間(驗排序)
//		System.out.println("----- 測試3:活動報名名單 -----");
//		List<Registration> nameList = regisRepo.findByActivityWithMember(a1);
//		System.out.println("1號活動共有 " + nameList.size() + " 筆報名");
//		for (Registration r : nameList) {
//		    System.out.println(r.getMember().getName()
//		            + " / 狀態:" + r.getRegisStatusText()
//		            + " / 報名時間:" + r.getRegisAt());
//		}
		
		// 測試4
//		System.out.println("----- 測試4:報名 Service -----");
//		// 4-1：重複報名被擋下
//		try {
//			service.register(m1, a1);
//		    System.out.println("4-1:重複報名未被擋下(預期:IllegalStateException)");
//		} catch (IllegalStateException e) {
//		    System.out.println("4-1:被正確擋下,訊息:" + e.getMessage());
//		}
//		// 4-2：報名成功
//		Registration saved = service.register(m1, a4);
//		System.out.println("4-2:報名成功,新的 regis_id = " + saved.getRegisId()
//		        + ",狀態 = " + saved.getRegisStatusText());
		
	
//		//測試5
//		System.out.println("----- 測試5:審核 Service -----");
//		// 5-1:亂給的報名編號 → 預期 IllegalArgumentException
//		try {
//		    service.approve(999);
//		    System.out.println("5-1:無效編號未被擋下(預期:IllegalArgumentException)");
//		} catch (IllegalArgumentException e) {
//		    System.out.println("5-1:正確擋下,訊息:" + e.getMessage());
//		}
//
//		// 5-2:審核一筆狀態不是0的 → 預期 IllegalStateException
//		try {
//			service.approve(5);  //此筆申請紀錄：狀態3已取消報名
//			System.out.println("5-2:非待審核狀態未被擋下(預期:IllegalStateException)");
//		} catch (IllegalStateException e) {
//			System.out.println("5-2:正確擋下,訊息:" + e.getMessage());
//		}
//		
//		// 5-3:審一筆狀態0的 → 預期回傳狀態=已報名成功
//		// 在交易內,受託管實體靠 dirty checking 自動同步(save 主要是給新建實體用的)
//		Registration approved = service.approve(1); //此筆申請紀錄：狀態0待審核
//		System.out.println("5-3:審核結果:" + approved.getRegisStatusText()
//		        + ",活動目前人數:" + approved.getActivity().getRegisCount());
//
//		// 5-4:審一筆「活動已滿」的狀態0報名 → 預期回傳狀態=報名失敗
//		// (要先照上面說的把某活動弄滿,再插一筆對它的狀態0報名,或用現有資料組合)
//		Registration approved = service.approve(12);
//		System.out.println("5-4:審核結果:"+approved.getRegisStatusText()
//				+ ",活動目前人數:" + approved.getActivity().getRegisCount());
		
		// 測試6
//		System.out.println("----- 測試6:取消 Service -----");
//
//		// 6-1:取消一筆狀態1的 → 預期 狀態變3、活動人數-1
//		Registration cancelled = service.cancel(2, 0, "臨時有事");
//		System.out.println("6-1:" + cancelled.getRegisStatusText()
//		        + ",取消原因:" + cancelled.getCancelReasonText()
//		        + ",活動目前人數:" + cancelled.getActivity().getRegisCount());

		// 6-2:對同一筆再取消一次(現在已是狀態3) → 預期 IllegalStateException
//		try {
//		    service.cancel(2, 0, "再取消一次");
//		    System.out.println("6-2:非有效狀態未被擋下(預期:IllegalStateException)");
//		} catch (IllegalStateException e) {
//		    System.out.println("6-2:正確擋下,訊息:" + e.getMessage());
//		}

		// 6-3:取消一筆狀態0的 → 預期 狀態變3、人數「不變」(驗證待審核不佔名額所以不-1)
//		Registration cancelled = service.cancel(11, 1, "腸胃炎");
//		System.out.println("6-3:" + cancelled.getRegisStatusText()
//		        + ",取消原因:" + cancelled.getCancelReasonText()
//		        + ",取消時間:" + cancelled.getCancelledAt());
		
		// 測試7
		System.out.println("----- 測試7:評論 Service -----");
		// 7-1:成功評論(報名狀態為1、活動已結束)
//		Registration reviewed = service.review(14, 3, "");
//		System.out.println("7-1:"+ reviewed.getRating()
//				+ ",評論時間:" + reviewed.getReviewedAt());
		// 7-2:擋下重複評論
//		try {
	//	    service.review(14, 2, "");
	//	    System.out.println("7-2:非有效狀態未被擋下(預期:IllegalStateException)");
//		} catch (IllegalStateException e) {
//		    System.out.println("7-2:正確擋下,訊息:" + e.getMessage());
//		}
		// 7-3:擋下活動尚未結束的評論
//		try {
//			service.review(6, 5, "這個活動很棒");
//			System.out.println("7-3:非有效狀態未被擋下(預期:IllegalStateException)");
//		} catch (IllegalStateException e) {
//		    System.out.println("7-3:正確擋下,訊息:" + e.getMessage());
//		}
		
		// 7-4:測試未填寫評分
//		try {
//			service.review(14, null, null);
//			System.out.println("7-4:未填寫評分未被擋下(預期:IllegalArgumentException)");
//		} catch (IllegalArgumentException e) {
//			System.out.println("7-4:正確擋下,訊息:" + e.getMessage());
//		}
      }
	

		

}
