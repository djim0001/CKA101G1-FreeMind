package com.freemind.login.psychologist.controller;


 
import java.sql.Timestamp;
 
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
 
import com.freemind.login.psychologist.dto.PsychologistRegisterReq;
import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.service.PsychologistService;
import com.freemind.login.member.otp.OtpMailService;
import com.freemind.login.member.otp.OtpService;
 
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
 
/**
 * 心理師「註冊」與「忘記密碼」，架構完全比照 MemberAuthController。
 *
 * 路由刻意跟會員分開（/front-end/psychologist/...），避免跟既有的
 * /front-end/register、/front-end/forgot 撞名。
 *
 * OTP purpose 也跟會員分開命名（psych-register / psych-reset），
 * 因為 OtpService 的 Redis key 是 otp:<purpose>:<email>，
 * 如果同一個信箱同時想註冊會員又註冊心理師，purpose 相同會互相蓋掉。
 *
 * 流程：
 *   註冊     POST /front-end/psychologist/register        → 建立帳號(狀態0未啟用) + 寄OTP → 驗證頁
 *            POST /front-end/psychologist/register/verify → OTP 正確 → 帳號狀態改 1（啟用）→ 可登入
 *   忘記密碼 POST /front-end/psychologist/forgot          → 查信箱 + 寄OTP → 重設頁
 *            POST /front-end/psychologist/forgot/reset    → OTP 正確 → 更新為新密碼(BCrypt)
 *
 * 對應的模板路徑我先假設放在 front-end/psychologist/auth/ 底下，
 * 跟提供的四個 html（放在 templates/ 資料夾）一起使用，如果你們的
 * templates 資料夾命名不同，記得同步改這裡的回傳字串。
 */
@Controller
@RequestMapping("/psych")
public class PsychologistAuthController {
 
	private static final String OTP_PURPOSE_REGISTER = "psych-register";
	private static final String OTP_PURPOSE_RESET = "psych-reset";
 
	private final PsychologistService psychologistService;
	private final OtpService otpService;
	private final OtpMailService otpMailService;
	private final PasswordEncoder passwordEncoder;
 
	public PsychologistAuthController(PsychologistService psychologistService, OtpService otpService,
			OtpMailService otpMailService, PasswordEncoder passwordEncoder) {
		this.psychologistService = psychologistService;
		this.otpService = otpService;
		this.otpMailService = otpMailService;
		this.passwordEncoder = passwordEncoder;
	}
 
	/* ==================== 註冊 ==================== */
 
	@GetMapping("/register")
	public String registerForm(ModelMap model, HttpSession session) {
		PsychologistRegisterReq form = new PsychologistRegisterReq();
		// Google 登入導過來的：email 已通過 Google 驗證，預填並鎖定（模板設 readonly）
		String googleEmail = (String) session.getAttribute(GooglePsychologistLoginController.GOOGLE_REGISTER_EMAIL);
		if (googleEmail != null) {
			form.setEmail(googleEmail);
			model.addAttribute("googleRegister", true);
		}
		model.addAttribute("registerForm", form);
		return "front-end/psych/auth/register";
	}
 
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("registerForm") PsychologistRegisterReq form,
			BindingResult result, ModelMap model, HttpSession session) {
 
		String googleEmail = (String) session.getAttribute(GooglePsychologistLoginController.GOOGLE_REGISTER_EMAIL);
		boolean googleRegister = googleEmail != null && googleEmail.equals(form.getEmail());
 
		// 帳號、信箱不可重複
		if (psychologistService.findByAccount(form.getPsychAccount()) != null) {
			result.rejectValue("psychAccount", "duplicate", "此帳號已被使用");
		}
		if (psychologistService.findByEmail(form.getEmail()) != null) {
			result.rejectValue("email", "duplicate", "此信箱已被註冊");
		}
		// 兩次密碼須一致
		if (form.getPsychPassword() != null
				&& !form.getPsychPassword().equals(form.getConfirmPassword())) {
			result.rejectValue("confirmPassword", "mismatch", "兩次輸入的密碼不一致");
		}
 
		if (result.hasErrors()) {
			if (googleRegister) {
				model.addAttribute("googleRegister", true); // 錯誤重回頁面時維持 Google 模式
			}
			return "front-end/psych/auth/register";
		}
 
		// 先建立「未啟用」帳號（accountStatus=0 登不進），OTP 驗證通過才啟用
		Psychologist psychologist = new Psychologist();
		psychologist.setPsychAccount(form.getPsychAccount());
		psychologist.setPsychPassword(passwordEncoder.encode(form.getPsychPassword())); // BCrypt
		psychologist.setName(form.getName());
		psychologist.setGender(form.getGender());
		psychologist.setPhoneNumber(form.getPhoneNumber());
		psychologist.setEmail(form.getEmail());
		psychologist.setPsychCertificate(form.getPsychCertificate());
		psychologist.setPsychLoc(form.getPsychLoc());
		psychologist.setPsychFee(form.getPsychFee());
		// hasPracticeLicense、weeklyAvailability、profilePic、bankAccount 不在註冊表單內，
		// 沿用 entity 預設值（hasPracticeLicense=false），註冊後再由心理師個人資料頁補齊。
 
		// Google 註冊：信箱已由 Google 驗證，直接啟用；一般註冊：未啟用，待 OTP 驗證
		psychologist.setAccountStatus(googleRegister ? 1 : 0);
		psychologist.setRegisAt(new Timestamp(System.currentTimeMillis()));
		psychologistService.addPsychologist(psychologist);
 
		if (googleRegister) {
			// 免 OTP，直接完成註冊，之後即可用 Google 一鍵登入
			session.removeAttribute(GooglePsychologistLoginController.GOOGLE_REGISTER_EMAIL);
			return "redirect:/psych/psychologistLogin?verified";
		}
 
		sendOtpMail(OTP_PURPOSE_REGISTER, form.getEmail(), "心理師註冊");
		return "redirect:/psych/register/verify?email=" + form.getEmail();
	}
 
	/** 註冊 OTP 驗證頁 */
	@GetMapping("/register/verify")
	public String registerVerifyPage(@RequestParam("email") String email, ModelMap model) {
		model.addAttribute("email", email);
		return "front-end/psych/auth/registerVerify";
	}
 
	/** 驗證註冊 OTP：正確 → 啟用帳號 */
	@PostMapping("/register/verify")
	public String registerVerify(@RequestParam("email") String email,
			@RequestParam("otp") String otp, ModelMap model) {
 
		if (!otpService.verifyOtp(OTP_PURPOSE_REGISTER, email, otp)) {
			model.addAttribute("email", email);
			model.addAttribute("errorMessage", "驗證碼錯誤或已過期，請重新輸入或按重寄");
			return "front-end/psych/auth/registerVerify";
		}
 
		Psychologist psychologist = psychologistService.findByEmail(email);
		if (psychologist != null && psychologist.getAccountStatus() == 0) {
			psychologist.setAccountStatus(1); // 啟用
			psychologistService.updatePsychologist(psychologist);
		}
		return "redirect:/psych/psychologistLogin?verified";
	}
 
	/** 重寄註冊 OTP（60 秒冷卻） */
	@PostMapping("/register/resend")
	public String registerResend(@RequestParam("email") String email, ModelMap model) {
		Psychologist psychologist = psychologistService.findByEmail(email);
		model.addAttribute("email", email);
		if (psychologist == null || psychologist.getAccountStatus() != 0) {
			model.addAttribute("errorMessage", "此信箱沒有待驗證的帳號");
		} else if (sendOtpMail(OTP_PURPOSE_REGISTER, email, "心理師註冊")) {
			model.addAttribute("successMessage", "驗證碼已重新寄出");
		} else {
			model.addAttribute("errorMessage", "驗證碼寄送過於頻繁，請 60 秒後再試");
		}
		return "front-end/psych/auth/registerVerify";
	}
 
	/* ==================== 忘記密碼 ==================== */
 
	@GetMapping("/forgot")
	public String forgotPage() {
		return "front-end/psych/auth/forgot";
	}
 
	/** 輸入信箱 → 寄重設密碼 OTP */
	@PostMapping("/forgot")
	public String forgot(@RequestParam("email") String email, ModelMap model) {
		Psychologist psychologist = psychologistService.findByEmail(email);
		if (psychologist == null) {
			model.addAttribute("errorMessage", "查無此信箱的心理師帳號");
			return "front-end/psych/auth/forgot";
		}
		if (psychologist.getAccountStatus() == 2) {
			model.addAttribute("errorMessage", "此帳號已停權，無法重設密碼，請聯繫客服");
			return "front-end/psych/auth/forgot";
		}
		if (!sendOtpMail(OTP_PURPOSE_RESET, email, "心理師重設密碼")) {
			model.addAttribute("errorMessage", "驗證碼寄送過於頻繁，請 60 秒後再試");
			return "front-end/psych/auth/forgot";
		}
		model.addAttribute("email", email);
		return "front-end/psych/auth/forgotReset";
	}
 
	/** 驗證 OTP + 設定新密碼 */
	@PostMapping("/forgot/reset")
	public String forgotReset(@RequestParam("email") String email,
			@RequestParam("otp") String otp,
			@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword, ModelMap model) {
 
		model.addAttribute("email", email);
 
		// 新密碼強度規則與註冊共用（PsychologistRegisterReq.PASSWORD_PATTERN）
		if (newPassword == null || newPassword.length() < 6 || newPassword.length() > 20) {
			model.addAttribute("errorMessage", "密碼長度須為6~20字");
			return "front-end/psych/auth/forgotReset";
		}
		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("errorMessage", "兩次輸入的密碼不一致");
			return "front-end/psych/auth/forgotReset";
		}
		if (!otpService.verifyOtp(OTP_PURPOSE_RESET, email, otp)) {
			model.addAttribute("errorMessage", "驗證碼錯誤或已過期");
			return "front-end/psych/auth/forgotReset";
		}
 
		Psychologist psychologist = psychologistService.findByEmail(email);
		if (psychologist == null) {
			model.addAttribute("errorMessage", "查無此信箱的心理師帳號");
			return "front-end/psych/auth/forgot";
		}
		if (psychologist.getAccountStatus() == 2) {
			model.addAttribute("errorMessage", "此帳號已停權，無法重設密碼，請聯繫客服");
			return "front-end/psych/auth/forgot";
		}
		psychologist.setPsychPassword(passwordEncoder.encode(newPassword)); // BCrypt
		if (psychologist.getAccountStatus() == 0) {
			psychologist.setAccountStatus(1); // 未啟用帳號：OTP 驗證完順便啟用
		}
		psychologistService.updatePsychologist(psychologist);
		return "redirect:/psych/psychologistLogin?resetSuccess";
	}
 
	/** 產生 OTP 並寄出；回傳 false 代表在冷卻 */
	private boolean sendOtpMail(String purpose, String email, String purposeText) {
		String otp = otpService.generateOtp(purpose, email);
		if (otp == null) {
			return false;
		}
		otpMailService.sendOtp(email, purposeText, otp);
		return true;
	}
}
 

