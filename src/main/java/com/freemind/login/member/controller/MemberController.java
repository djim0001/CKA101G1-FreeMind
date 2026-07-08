package com.freemind.login.member.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 每次請求先從 DB 載入登入會員(表格Name)的資料，
    // POST 時表單欄位會「覆蓋」在這個物件上，沒送出的欄位（密碼、註冊時間等）保留 DB 原值
    
    // Controller 內的所有請求（@GetMapping、@PostMapping）執行前，先執行這個方法，並且把 return 的結果
    // 自動存入 Model 中的 "member"
    
    // Authentication 是 Security 提供的介面，代表「當前登入者的身分資訊」。
    // 當會員在登入頁輸入帳密，Security 驗證成功後，會建立一個 Authentication 物件存進 SecurityContext（綁在 session 上）
    @ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
        return memberService.findByAccount(authentication.getName());
    }

    // 只允許表單綁定這些欄位，防止有人自行送出 memberAccount、accountStatus 等欄位竄改資料
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("name", "gender", "phoneNumber", "birthday",
                "city", "dist", "address", "nickname", "email");
    }

    // 查看個人資料
    @GetMapping("/profile")
    public String profile() {
        return "front-end/member/memberpage/profile";
    }

    // 編輯個人資料頁面（表單由 @ModelAttribute 預填現有資料）
    @GetMapping("/update")
    public String updateForm() {
        return "front-end/member/memberpage/update";
    }

    // 提交修改
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("member") Member member,
                         BindingResult result,
                         @RequestParam(name = "profilePicFile", required = false) MultipartFile profilePicFile,
                         RedirectAttributes redirectAttributes) throws IOException {
        if (result.hasErrors()) {
            return "front-end/member/memberpage/update";
        }
        // 有選新頭像才覆蓋；沒選就沿用 DB 原本的頭像
        if (profilePicFile != null && !profilePicFile.isEmpty()) {
            member.setProfilePic(profilePicFile.getBytes());
        }
        memberService.updateMember(member);
        redirectAttributes.addFlashAttribute("success", "資料修改成功");
        return "redirect:/member/profile";
        //如果 POST 成功後沒有 redirect，用戶按 F5 重新整理時瀏覽器會重送一次 POST（跳出「要再次提交表單嗎？」）
        //資料就會被存兩次。
        
    }

    // 顯示登入會員自己的頭像
    @GetMapping("/profilePic")
    public ResponseEntity<byte[]> profilePic(Authentication authentication) throws IOException {
        Member member = memberService.findByAccount(authentication.getName());
        byte[] pic = member.getProfilePic();
        if (pic == null || pic.length == 0) {
            return ResponseEntity.notFound().build();
        }
        // 依圖片內容（檔頭 magic bytes）猜測 content-type，猜不到就當 jpeg
        String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(pic));
        if (contentType == null) {
            contentType = MediaType.IMAGE_JPEG_VALUE;
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(pic);
    }
}
