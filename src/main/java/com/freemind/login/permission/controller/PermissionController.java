package com.freemind.login.permission.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.login.permission.model.Permission;
import com.freemind.login.permission.model.PermissionService;

@Controller
@RequestMapping("/admin/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("")
    public String list(Model model) {
        List<Permission> permissions = permissionService.getAll();
        model.addAttribute("permissions", permissions);
        return "admin/permissions/list";
    }

    @GetMapping("/add")
    public String addForm() {
        return "admin/permissions/form";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam(required = false) Integer permId,
            @RequestParam String permName,
            @RequestParam String permDetail) {

        Permission permission = new Permission();
        if (permId != null) {
            permission.setPermId(permId);
        }
        permission.setPermName(permName);
        permission.setPermDetail(permDetail);

        permissionService.addPermission(permission);
        return "redirect:/admin/permissions";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Integer permId) {
        permissionService.deletePermission(permId);
        return "redirect:/admin/permissions";
    }

}
