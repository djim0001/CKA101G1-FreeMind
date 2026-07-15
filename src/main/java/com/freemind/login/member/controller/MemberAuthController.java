package com.freemind.login.member.controller;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.login.member.dto.RegisterForm;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.member.otp.OtpMailService;
import com.freemind.login.member.otp.OtpService;

import jakarta.validation.Valid;

/**
 * 會員「註冊」與「忘記密碼」（皆搭配 Email OTP 一次性驗證，OTP 存 Redis）。
 *
 * 特別開一個 Controller 而不放 MemberController 的原因：
 * MemberController 的 @ModelAttribute("member") 會對每個請求用登入身分查會員，
 * 註冊/忘記密碼的使用者「還沒登入」，放那邊會 NullPointerException。
 *
 * 流程：
 *   註冊     POST /front-end/register        → 建立帳號(狀態0未啟用) + 寄OTP → 驗證頁
 *            POST /front-end/register/verify → OTP 正確 → 帳號狀態改 1（啟用）→ 可登入
 *   忘記密碼 POST /front-end/forgot          → 查信箱 + 寄OTP → 重設頁
 *            POST /front-end/forgot/reset    → OTP 正確 → 更新為新密碼(BCrypt)
 */
@Controller
@RequestMapping("/front-end")
public class MemberAuthController {

	private final MemberService memberService;
	private final OtpService otpService;
	private final OtpMailService otpMailService;
	private final PasswordEncoder passwordEncoder;

	public MemberAuthController(MemberService memberService, OtpService otpService,
			OtpMailService otpMailService, PasswordEncoder passwordEncoder) {
		this.memberService = memberService;
		this.otpService = otpService;
		this.otpMailService = otpMailService;
		this.passwordEncoder = passwordEncoder;
	}

	/* ==================== 註冊 ==================== */

	@GetMapping("/register")
	public String registerForm(ModelMap model) {
		model.addAttribute("registerForm", new RegisterForm());
		return "front-end/member/auth/register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
			BindingResult result, ModelMap model) {

		// 帳號、信箱不可重複
		if (memberService.findByAccount(form.getMemberAccount()) != null) {
			result.rejectValue("memberAccount", "duplicate", "此帳號已被使用");
		}
		if (memberService.findByEmail(form.getEmail()) != null) {
			result.rejectValue("email", "duplicate", "此信箱已被註冊");
		}
		if (result.hasErrors()) {
			return "front-end/member/auth/register";
		}

		// 先建立「未啟用」帳號（accountStatus=0 登不進），OTP 驗證通過才啟用
		Member member = new Member();
		member.setMemberAccount(form.getMemberAccount());
		member.setMemberPassword(passwordEncoder.encode(form.getMemberPassword())); // BCrypt
		member.setName(form.getName());
		member.setGender(form.getGender());
		member.setPhoneNumber(form.getPhoneNumber());
		member.setEmail(form.getEmail());
		member.setAccountStatus(0);
		member.setRegisAt(LocalDateTime.now());
		memberService.addMember(member);

		sendOtpMail("register", form.getEmail(), "會員註冊");
		return "redirect:/front-end/register/verify?email=" + form.getEmail();
	}

	/** 註冊 OTP 驗證頁 */
	@GetMapping("/register/verify")
	public String registerVerifyPage(@RequestParam("email") String email, ModelMap model) {
		model.addAttribute("email", email);
		return "front-end/member/auth/registerVerify";
	}

	/** 驗證註冊 OTP：正確 → 啟用帳號 */
	@PostMapping("/register/verify")
	public String registerVerify(@RequestParam("email") String email,
			@RequestParam("otp") String otp, ModelMap model) {

		if (!otpService.verifyOtp("register", email, otp)) {
			model.addAttribute("email", email);
			model.addAttribute("errorMessage", "驗證碼錯誤或已過期，請重新輸入或按重寄");
			return "front-end/member/auth/registerVerify";
		}

		Member member = memberService.findByEmail(email);
		if (member != null && member.getAccountStatus() == 0) {
			member.setAccountStatus(1); // 啟用
			memberService.updateMember(member);
		}
		return "redirect:/front-end/login?verified";
	}

	/** 重寄註冊 OTP（60 秒冷卻） */
	@PostMapping("/register/resend")
	public String registerResend(@RequestParam("email") String email, ModelMap model) {
		Member member = memberService.findByEmail(email);
		model.addAttribute("email", email);
		if (member == null || member.getAccountStatus() != 0) {
			model.addAttribute("errorMessage", "此信箱沒有待驗證的帳號");
		} else if (sendOtpMail("register", email, "會員註冊")) {
			model.addAttribute("successMessage", "驗證碼已重新寄出");
		} else {
			model.addAttribute("errorMessage", "驗證碼寄送過於頻繁，請 60 秒後再試");
		}
		return "front-end/member/auth/registerVerify";
	}

	/* ==================== 忘記密碼 ==================== */

	@GetMapping("/forgot")
	public String forgotPage() {
		return "front-end/member/auth/forgot";
	}

	/** 輸入信箱 → 寄重設密碼 OTP */
	@PostMapping("/forgot")
	public String forgot(@RequestParam("email") String email, ModelMap model) {
		Member member = memberService.findByEmail(email);
		if (member == null) {
			model.addAttribute("errorMessage", "查無此信箱的會員");
			return "front-end/member/auth/forgot";
		}
		if (member.getAccountStatus() == 2) {
		    model.addAttribute("errorMessage", "此帳號已停權，無法重設密碼，請聯繫客服");
		    return "front-end/member/auth/forgot";
		}
		if (!sendOtpMail("reset", email, "重設密碼")) {
			model.addAttribute("errorMessage", "驗證碼寄送過於頻繁，請 60 秒後再試");
			return "front-end/member/auth/forgot";
		}
		model.addAttribute("email", email);
		return "front-end/member/auth/forgotReset";
	}

	/** 驗證 OTP + 設定新密碼 */
	@PostMapping("/forgot/reset")
	public String forgotReset(@RequestParam("email") String email,
			@RequestParam("otp") String otp,
			@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword, ModelMap model) {

		model.addAttribute("email", email);

		// 新密碼強度規則與註冊共用（RegisterForm.PASSWORD_PATTERN）
		if (newPassword == null || !newPassword.matches(RegisterForm.PASSWORD_PATTERN)) {
			model.addAttribute("errorMessage", "新" + RegisterForm.PASSWORD_MESSAGE);
			return "front-end/member/auth/forgotReset";
		}
		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("errorMessage", "兩次輸入的密碼不一致");
			return "front-end/member/auth/forgotReset";
		}
		if (!otpService.verifyOtp("reset", email, otp)) {
			model.addAttribute("errorMessage", "驗證碼錯誤或已過期");
			return "front-end/member/auth/forgotReset";
		}

		Member member = memberService.findByEmail(email);
		if (member == null) {
			model.addAttribute("errorMessage", "查無此信箱的會員");
			return "front-end/member/auth/forgot";
		}
		if (member.getAccountStatus() == 2) {
		    model.addAttribute("errorMessage", "此帳號已停權，無法重設密碼，請聯繫客服");
		    return "front-end/member/auth/forgot";
		}
		member.setMemberPassword(passwordEncoder.encode(newPassword)); // BCrypt
	    if (member.getAccountStatus() == 0) {
	        member.setAccountStatus(1); // 未啟用帳號：OTP 已證明信箱所有權，順便啟用
	    }
	    memberService.updateMember(member);
	    return "redirect:/front-end/login?resetSuccess";
	}
	
	/** 產生 OTP 並寄出；回傳 false 代表仍在冷卻時間內 */
	private boolean sendOtpMail(String purpose, String email, String purposeText) {
		String otp = otpService.generateOtp(purpose, email);
		if (otp == null) {
			return false;
		}
		otpMailService.sendOtp(email, purposeText, otp);
		return true;
	}
}
