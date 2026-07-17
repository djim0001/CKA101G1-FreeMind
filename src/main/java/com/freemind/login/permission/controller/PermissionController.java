package com.freemind.login.permission.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminService;
import com.freemind.login.permission.model.Permission;
import com.freemind.login.permission.model.PermissionService;

@Controller
@RequestMapping("/admin/permissions")
public class PermissionController {

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private AdminService adminService;

	@GetMapping("")
	public String list(Model model) {
		List<Permission> permissions = permissionService.getAll();
		model.addAttribute("permissions", permissions);
		return "back-end/login/permissions/permissionList";
	}

	@GetMapping("/add")
	public String addForm() {
		return "back-end/login/permissions/permissionForm";
	}

	/** 編輯權限：載入既有資料後共用新增的表單 */
	@GetMapping("/edit")
	public String editForm(@RequestParam("permId") Integer permId, Model model) {
		model.addAttribute("permission", permissionService.getOnePermission(permId));
		return "back-end/login/permissions/permissionForm";
	}

	@PostMapping("/save")
	public String save(@RequestParam(required = false) Integer permId, @RequestParam String permName,
			@RequestParam String permDetail) {

		Permission permission = new Permission();
		permission.setPermName(permName);
		permission.setPermDetail(permDetail);

		if (permId != null) {
			permission.setPermId(permId);
			permissionService.updatePermission(permission); // 編輯：有 permId 走更新
		} else {
			permissionService.addPermission(permission);    // 新增
		}
		return "redirect:/admin/permissions";
	}

	@GetMapping("/delete")
	public String delete(@RequestParam Integer permId) {
		permissionService.deletePermission(permId);
		return "redirect:/admin/permissions";
	}

	/** 指派權限頁：列出全部權限，勾選該管理員已有的 */
	@GetMapping("/assign")
	public String assignForm(@RequestParam("adminId") Integer adminId, Model model) {
		model.addAttribute("adminVO", adminService.getOneAdmin(adminId));
		model.addAttribute("allPermissions", permissionService.getAll());
		return "back-end/login/permissions/permissionAssign";
	}

	/** 儲存指派：整組覆蓋該管理員的權限集合（下次登入生效） */
	@PostMapping("/assign")
	public String assign(@RequestParam("adminId") Integer adminId,
			@RequestParam(value = "permIds", required = false) List<Integer> permIds) {
		Admin admin = adminService.getOneAdmin(adminId);
		Set<Permission> selected = new HashSet<>();
		if (permIds != null) {
			permIds.forEach(id -> selected.add(permissionService.getOnePermission(id)));
		}
		admin.setPermissions(selected);
		adminService.updateAdmin(admin);
		return "redirect:/admin/listAllAdmin";
	}

}
